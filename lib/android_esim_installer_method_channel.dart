import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'android_esim_installer_platform_interface.dart';

/// An implementation of [AndroidEsimInstallerPlatform] that uses method channels.
class MethodChannelAndroidEsimInstaller extends AndroidEsimInstallerPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('android_esim_installer');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
