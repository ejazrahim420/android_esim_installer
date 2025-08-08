import 'package:flutter_test/flutter_test.dart';
import 'package:android_esim_installer/android_esim_installer.dart';
import 'package:android_esim_installer/android_esim_installer_platform_interface.dart';
import 'package:android_esim_installer/android_esim_installer_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockAndroidEsimInstallerPlatform
    with MockPlatformInterfaceMixin
    implements AndroidEsimInstallerPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final AndroidEsimInstallerPlatform initialPlatform = AndroidEsimInstallerPlatform.instance;

  test('$MethodChannelAndroidEsimInstaller is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelAndroidEsimInstaller>());
  });

  test('getPlatformVersion', () async {
    AndroidEsimInstaller androidEsimInstallerPlugin = AndroidEsimInstaller();
    MockAndroidEsimInstallerPlatform fakePlatform = MockAndroidEsimInstallerPlatform();
    AndroidEsimInstallerPlatform.instance = fakePlatform;

    expect(await androidEsimInstallerPlugin.getPlatformVersion(), '42');
  });
}
