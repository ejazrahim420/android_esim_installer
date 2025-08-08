# android_esim_installer

A Flutter plugin (Android-only, Java) to install eSIM profiles programmatically using `EuiccManager`.

---

## ⚠️ Required Setup for Users

This plugin **will not work** without the following setup in your app.

### 1. AndroidManifest.xml changes
In your app’s `android/app/src/main/AndroidManifest.xml`:

- Add the `tools` namespace to the `<manifest>` tag if not already present:
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.example.app">
````

* Add **protected eSIM write permission**:

```xml
<uses-permission
    android:name="android.permission.WRITE_EMBEDDED_SUBSCRIPTIONS"
    tools:ignore="ProtectedPermissions" />
```

* Declare and request your app-specific broadcast permission:

```xml
<!-- Declare a custom signature-level permission the LPA expects your app to own -->
<permission
    android:name="com.your_package_name.lpa.permission.BROADCAST"
    android:protectionLevel="signature" />

<!-- Request/Use that permission in your own app -->
<uses-permission android:name="com.your_package_name.lpa.permission.BROADCAST" />
```

⚠️ Replace every `com.your_package_name` with your **actual applicationId** from `build.gradle`.

* (Optional but recommended) Add telephony feature declarations:

```xml
<uses-feature android:name="android.hardware.telephony" android:required="false" />
<uses-feature android:name="android.hardware.telephony.euicc" android:required="false" />
```

---

### 2. Gradle minimum SDK

In your `android/app/build.gradle`:

```gradle
android {
  defaultConfig {
    minSdkVersion 28
  }
  compileSdkVersion 35
  targetSdkVersion 35
}
```

---

### 3. Privileged Permission Warning

The permission:

```
android.permission.WRITE_EMBEDDED_SUBSCRIPTIONS
```

is **signature/privileged**.
Your app will **not** be allowed to install eSIMs unless:

* It is a **system/privileged app** signed with the platform key, **or**
* It has **carrier privileges** granted by the eUICC/carrier.
* To use this plugin, you must **sign your eSIMs** from the eUICC/carrier with your package name or SHA-1 keys.

If you do not meet these conditions, the plugin will return:

```
error:PERMISSION_DENIED
```

and installation will not proceed.

---

## Installation

Add to your `pubspec.yaml`:

```yaml
dependencies:
  android_esim_installer: ^1.0.1
```

Run:

```bash
flutter pub get
```

---

## Usage

```dart
import 'package:android_esim_installer/android_esim_installer.dart';

...

await AndroidEsimInstaller.install(
  activationCode: 'LPA:1$YOUR_SMDP_PLUS_ADDRESS$YOUR_MATCHING_ID',
  appPackageName: 'com.your_package_name',
  onInstalling: (EsimInstallCallback message) {

  },
  onResolving: (EsimInstallCallback message) {
    
  }, 
  onSuccess: (EsimInstallCallback message) {
    
  },
  onError: (EsimInstallCallback message) {
    
  },
);
```

---

## Status Callbacks

The `onStatus` callback will receive:

* `"installing"` → Download started
* `"success"` → eSIM installed successfully
* `"resolving"` → Requires user resolution
* `"error:..."` → Installation failed with reason

---

## Example Output

```
installing
success
```

or

```
error:PERMISSION_DENIED , Your app does not have the required permissions for direct eSIM installation.
```

---
