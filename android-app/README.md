# Surya Optics Billing — Android client

This Android Studio project wraps the existing Spring Boot/Thymeleaf billing application in a mobile WebView so the current billing, customer, prescription, product, report and settings flows can be used from Android.

## Important architecture note

The source is a Spring Boot web application with a MySQL database, not a native Android app. The APK is a client for a deployed server; it does not host Spring Boot or MySQL inside the APK. Deploy the backend over HTTPS first, then enter that HTTPS URL when the app opens. The URL can be changed using Server in the app toolbar.

## Build

1. Open this `android-app` folder in Android Studio (JDK 17 and Android SDK 35).
2. Let Gradle sync.
3. Configure the HTTPS URL of your deployed Surya Optics server when the app launches.
4. Build > Build APK(s). The APK is written under `app/build/outputs/apk/`.

The Android client supports session cookies, file selection for uploads, downloads, back navigation, progress display and server URL configuration. Configure `DB_USERNAME` and `DB_PASSWORD` as environment variables on the backend; do not deploy with default or blank credentials.


## CI output

The repository workflow builds a debug APK and publishes it as a GitHub Actions artifact. A release-ready Play Store APK must be signed separately with a protected release keystore.
