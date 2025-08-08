## 1.0.0

- Initial release of `android_esim_installer`.
- Android-only (Java) Flutter plugin to programmatically install eSIM profiles via `EuiccManager`.
- Exposes a single Dart method:
  - `AndroidEsimInstaller.install(activationCode, lpaDeclaredPermission, onStatus)`
- Sends real-time installation status back to Dart:
  - `"installing"`, `"success"`, `"resolving"`, `"error:..."`
- Requires **user setup**:
  - AndroidManifest changes (custom LPA broadcast permission + protected WRITE_EMBEDDED_SUBSCRIPTIONS permission).
  - Gradle `minSdkVersion` 28+.
  - App must be a **system app** or have **carrier privileges** to install

## 1.0.1

- Initial release of `android_esim_installer`.

