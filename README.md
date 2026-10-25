# opticianbilling

Surya Optics billing application (Spring Boot + Thymeleaf) with an Android WebView client.

## Planned updates
- Capture customer age in customer records and forms.
- Display an explicit `+` sign for positive prescription lens values.
- Build a debug Android APK using the GitHub Actions workflow in `.github/workflows/android-apk.yml`.

## Android client
The Android client is in `android-app/`. It loads the deployed HTTPS web application; it does not bundle the Spring Boot server or database. Configure the backend URL in the Android app before building.

## Database
Apply `database/migrations/20261025_add_customer_age.sql` to the existing database before using the customer-age field. Back up the database first and verify the column/table names against the deployment schema.

## Build
Open `android-app/` in Android Studio, or run the repository's Android APK workflow after the source has been pushed. The generated debug APK is an installable test build, not a signed Play Store release.