# Surya Optics Billing

Spring Boot + Thymeleaf billing application source with an Android WebView client.

## Current repository status

- Android WebView project is committed under `android-app/`.
- GitHub Actions successfully built the **debug APK** in workflow run [Build Android APK](https://github.com/solankiabhay/opticianbilling/actions/workflows/android-apk.yml).
- Customer age field, DTO validation, and add/edit/list/view UI changes are committed.
- Prescription DTO display accessors format positive diopter values with a leading `+`.
- The repository still needs the remaining original backend source files committed and a complete Maven build verified before this can be considered a complete deployment.

## Android client

The APK is a client for a deployed billing server. It does not embed Spring Boot or MySQL. Deploy the backend over HTTPS, then enter its URL in the Android app using the Server button. Build output is available as the `surya-optics-debug-apk` artifact in the GitHub Actions run.

## Customer age

Age is optional and validated between 0 and 130 years in the DTO. Existing records can remain null. Before applying the migration, back up the database and verify that the deployed customer table is named `customers`.

```sql
ALTER TABLE customers ADD COLUMN age INT NULL;
```

Migration file: `database/migrations/20261025_add_customer_age.sql`.

## Prescription display

The DTO display accessors show positive SPH, CYL and ADD values with a leading plus sign, such as `+1.25`; zero and negative values keep their normal representation.

## Configuration

Set backend database credentials using environment variables; never commit real secrets:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

See `.env.example` for placeholders. Do not deploy with blank/default database credentials.

## Release note

The current Android output is a debug APK for testing. A Play Store or production distribution needs a release build signed with a protected release keystore.
