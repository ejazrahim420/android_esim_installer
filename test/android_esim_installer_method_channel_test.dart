import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:android_esim_installer/android_esim_installer_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelAndroidEsimInstaller platform = MethodChannelAndroidEsimInstaller();
  const MethodChannel channel = MethodChannel('android_esim_installer');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
