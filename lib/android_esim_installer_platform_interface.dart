import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'android_esim_installer_method_channel.dart';

abstract class AndroidEsimInstallerPlatform extends PlatformInterface {
  /// Constructs a AndroidEsimInstallerPlatform.
  AndroidEsimInstallerPlatform() : super(token: _token);

  static final Object _token = Object();

  static AndroidEsimInstallerPlatform _instance = MethodChannelAndroidEsimInstaller();

  /// The default instance of [AndroidEsimInstallerPlatform] to use.
  ///
  /// Defaults to [MethodChannelAndroidEsimInstaller].
  static AndroidEsimInstallerPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [AndroidEsimInstallerPlatform] when
  /// they register themselves.
  static set instance(AndroidEsimInstallerPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
