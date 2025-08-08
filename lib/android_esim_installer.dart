import 'android_esim_installer_platform_interface.dart';
import 'package:flutter/services.dart';

typedef EsimInstallCallback = void Function(String status);

class AndroidEsimInstaller {
  static const MethodChannel _channel = MethodChannel('android_esim_installer');

  /// Start eSIM install using an activationCode (LPA:1$... or SM-DP+ + matching code)
  /// and the app-specific broadcast permission you declared in AndroidManifest.
  ///
  /// The native side will invoke 'esim_install_status' with:
  ///   - "installing"
  ///   - "success"
  ///   - "resolving"
  ///   - "error:...."
  ///

  static Future<void> install({
    required String activationCode,
    required String appPackageName,
    required EsimInstallCallback onInstalling,
    required EsimInstallCallback onSuccess,
    required EsimInstallCallback onResolving,
    required EsimInstallCallback onError,
  }) async {
    _channel.setMethodCallHandler((call) async {
      if (call.method == 'esim_install_status') {
        final String msg = (call.arguments ?? '').toString();
        if (msg.trim().toLowerCase() == "installing") {
          onInstalling(msg);
        } else if (msg.trim().toLowerCase() == "resolving") {
          onResolving(msg);
        } else if (msg.trim().toLowerCase() == "success") {
          onSuccess(msg);
        } else {
          onError(msg);
        }
      }
    });

    await _channel.invokeMethod<void>('installEsim', {
      'activationCode': activationCode,
      'lpaDeclaredPermission': "$appPackageName.lpa.permission.BROADCAST",
    });
  }

  Future<String?> getPlatformVersion() {
    return AndroidEsimInstallerPlatform.instance.getPlatformVersion();
  }
}
