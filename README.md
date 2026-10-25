# Surya Optics Billing

Spring Boot + Thymeleaf billing application source with an Android WebView client.

## Current repository status

- Android WebView project is committed under `android-app/`.
- GitHub Actions successfully built the **debug APK** in workflow run [Build Android APK](https://github.com/solankiabhay/opticianbilling/actions/workflows/android-apk.yml).
- Customer age field, DTO validation, and add/edit/list/view UI changes are committed.
- Basic invoice creation/listing/details and prescription create/edit/list/view screens are implemented, with unit tests for invoice arithmetic and signed optical power.
- Prescription DTO display accessors format positive diopter values with a leading `+`.
- The Java CI workflow currently passes for the committed code and unit tests. However, this is not yet a complete restoration of every original module: the remaining original product inventory, bill line items, payment workflows, reporting, PDF/export and related modules still need to be restored before claiming full feature parity.

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
- `APP_USER` (defaults to `admin`)
- `APP_PASSWORD` (required; at least 12 characters)

See `.env.example` for placeholders. Do not deploy with blank/default database credentials.

## Release note

The current Android output is a debug APK for testing. A Play Store or production distribution needs a release build signed with a protected release keystore.
