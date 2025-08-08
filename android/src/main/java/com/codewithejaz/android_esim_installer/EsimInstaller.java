package com.codewithejaz.android_esim_installer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.telephony.euicc.DownloadableSubscription;
import android.telephony.euicc.EuiccManager;
import android.util.Log;

import io.flutter.plugin.common.MethodChannel;

public class EsimInstaller {

    private static final String tag = "AndroidEsimInstaller";
    private static final String ACTION_DOWNLOAD_SUBSCRIPTION = "download_subscription";

    private final Activity activity;
    private final MethodChannel channel;
    private BroadcastReceiver subscriptionDownloadReceiver;

    public EsimInstaller(Activity activity, MethodChannel channel) {
        this.activity = activity;
        this.channel = channel;
    }

    // Safe cleanup analog to onDestroy
    public void cleanupReceiver() {
        if (subscriptionDownloadReceiver != null) {
            try {
                activity.unregisterReceiver(subscriptionDownloadReceiver);
            } catch (IllegalArgumentException e) {
                Log.w(tag, "Receiver already unregistered.", e);
            } finally {
                subscriptionDownloadReceiver = null;
            }
        }
    }

    // ===== YOUR CODE (body preserved; only contextual 'activity' references added) =====
    public void installEsimWithEuiccManager(String activationCode, String LPA_DECLARED_PERMISSION) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            channel.invokeMethod("esim_install_status", "error: UNSUPPORTED_OS , eSIM API requires Android 9 (API 28) or higher.");
            return;
        }

        EuiccManager mgr = (EuiccManager) activity.getSystemService(Context.EUICC_SERVICE);

        if (mgr == null || !mgr.isEnabled()) {
            channel.invokeMethod("esim_install_status", "error: eSIM not enabled");
            return;
        }

        subscriptionDownloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!ACTION_DOWNLOAD_SUBSCRIPTION.equals(intent.getAction())) {
                    channel.invokeMethod("esim_install_status", "error: esim not supported");
                    return;
                }

                int resultCode = getResultCode();
                int detailedCode = intent.getIntExtra(EuiccManager.EXTRA_EMBEDDED_SUBSCRIPTION_DETAILED_CODE, 99);

                if (resultCode == EuiccManager.EMBEDDED_SUBSCRIPTION_RESULT_OK) {
                    channel.invokeMethod("esim_install_status", "success");
                } else if (resultCode == EuiccManager.EMBEDDED_SUBSCRIPTION_RESULT_RESOLVABLE_ERROR) {
                    channel.invokeMethod("esim_install_status", "resolving");
                    try {
                        PendingIntent callbackIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            mgr.startResolutionActivity(activity, 0, intent, callbackIntent);
                        } else {
                            channel.invokeMethod("esim_install_status", "error:");
                        }

                    } catch (Exception e) {
                        Log.e(tag, "Resolution activity failed", e);
                        channel.invokeMethod("esim_install_status", "error: resolution failed - " + e.getMessage());
                    }
                } else {
                    String errorMessage = "error: Operation failed with resultCode " + resultCode + " and detailedCode " + detailedCode;
                    Log.e(tag, errorMessage);
                    channel.invokeMethod("esim_install_status", errorMessage);
                }

                if (resultCode != EuiccManager.EMBEDDED_SUBSCRIPTION_RESULT_RESOLVABLE_ERROR) {
                    try {
                        activity.unregisterReceiver(this);
                    } catch (IllegalArgumentException e) {
                        Log.w(tag, "Receiver was already unregistered.", e);
                    } finally {
                        subscriptionDownloadReceiver = null;
                    }
                }
            }
        };

        // registerReceiver with user-supplied app-specific permission
        int flags = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ? Context.RECEIVER_EXPORTED : 0;
        activity.registerReceiver(
                subscriptionDownloadReceiver,
                new IntentFilter(ACTION_DOWNLOAD_SUBSCRIPTION),
                LPA_DECLARED_PERMISSION,
                null,
                flags
        );

        DownloadableSubscription sub = DownloadableSubscription.forActivationCode(activationCode);

        if (sub == null) {
            channel.invokeMethod("esim_install_status", "error:INVALID_CODE , The activation code is invalid.");
            cleanupReceiver();
            return;
        }

        Intent callbackBaseIntent = new Intent(ACTION_DOWNLOAD_SUBSCRIPTION).setPackage(activity.getPackageName());
        PendingIntent callbackIntent = PendingIntent.getBroadcast(activity, 0, callbackBaseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        try {
            mgr.downloadSubscription(sub, true /* switchAfterDownload */, callbackIntent);
            channel.invokeMethod("esim_install_status", "installing");

        } catch (SecurityException e) {
            Log.e(tag, "PERMISSION DENIED: App lacks WRITE_EMBEDDED_SUBSCRIPTIONS permission.", e);
            channel.invokeMethod("esim_install_status", "error:PERMISSION_DENIED ,  Your app does not have the required permissions for direct eSIM installation. , This is expected for standard apps. The app must be a system app or have carrier privileges.");
            cleanupReceiver();
        }
    }
    // ======================================================================
}
