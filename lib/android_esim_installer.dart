import 'android_esim_installer_platform_interface.dart';
// lib/android_esim_installer.dart
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
  /// You can provide an optional [onStatus] callback to receive these updates.
  static Future<void> install({
    required String activationCode,
    required String lpaDeclaredPermission,
    required EsimInstallCallback onStatus,
  }) async {
    // optional status callback from native -> Dart
    _channel.setMethodCallHandler((call) async {
      if (call.method == 'esim_install_status') {
        final msg = (call.arguments ?? '').toString();
        onStatus(msg);
      }
    });

    await _channel.invokeMethod<void>('installEsim', {
      'activationCode': activationCode,
      'lpaDeclaredPermission':
          "$lpaDeclaredPermission.lpa.permission.BROADCAST",
    });
  }

  Future<String?> getPlatformVersion() {
    return AndroidEsimInstallerPlatform.instance.getPlatformVersion();
  }
}
