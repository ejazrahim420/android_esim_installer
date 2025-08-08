package com.codewithejaz.android_esim_installer;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class AndroidEsimInstallerPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {

  private MethodChannel channel;
  private Context appContext;
  private Activity activity;
  private EsimInstaller installer;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    channel = new MethodChannel(binding.getBinaryMessenger(), "android_esim_installer");
    channel.setMethodCallHandler(this);
    appContext = binding.getApplicationContext();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
    if ("installEsim".equals(call.method)) {
      if (activity == null) {
        result.error("NO_ACTIVITY", "Plugin requires a foreground Activity.", null);
        return;
      }
      Map<String, Object> args = (Map<String, Object>) call.arguments;
      String activationCode = args != null && args.get("activationCode") != null ? String.valueOf(args.get("activationCode")) : "";
      String lpaPermission  = args != null && args.get("lpaDeclaredPermission") != null ? String.valueOf(args.get("lpaDeclaredPermission")) : "";

      if (installer == null) {
        installer = new EsimInstaller(activity, channel);
      }
      installer.installEsimWithEuiccManager(activationCode, lpaPermission);
      result.success(null);
    } else {
      result.notImplemented();
    }
  }

  @Override public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    if (installer != null) {
      installer.cleanupReceiver();
      installer = null;
    }
    channel.setMethodCallHandler(null);
    channel = null;
    appContext = null;
  }

  // ActivityAware
  @Override public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }
  @Override public void onDetachedFromActivityForConfigChanges() {
    if (installer != null) installer.cleanupReceiver();
    activity = null;
  }
  @Override public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }
  @Override public void onDetachedFromActivity() {
    if (installer != null) installer.cleanupReceiver();
    activity = null;
  }
}
