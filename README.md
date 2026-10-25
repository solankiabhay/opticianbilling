# Surya Optics Billing

Optical shop billing application built with Spring Boot, Spring Security, Thymeleaf, JPA/MySQL and a lightweight Android WebView client.

## Features currently implemented

- **Authentication:** responsive branded sign-in page, BCrypt-encoded environment-provided admin password, authenticated routes and logout.
- **Customer management:** create, edit, search, view profile, age field (optional, 0–130), purchase history and prescription history.
- **Prescriptions:** create, edit, list and view prescriptions; positive SPH/CYL/ADD display values include a leading plus sign.
- **Invoices:** create invoices with multiple inventory product lines plus manual subtotal and optical-service/additional charges, automatic total/advance/balance calculations, delivery date, customer invoice history, status, printable invoice view and downloadable PDF receipts. Product stock is deducted transactionally when invoiced, with row locks to avoid concurrent overselling.
- **Payments:** accept follow-up/partial payments, prevent zero/negative or over-balance payments, update invoice status and persist individual payment receipts in bill_payments.
- **Inventory:** product CRUD, unique SKU validation, stock adjustments, active/inactive state, selling/purchase prices and low-stock alerts.
- **Dashboard:** customer/invoice counts, billed value, received value, outstanding balance, low-stock alert count and recent invoices.
- **Reports:** date-filtered invoice totals, receipts and balances; invoice CSV export with spreadsheet-formula injection protection, and PDF invoice downloads.
- **Quality checks:** Java CI validates Docker Compose, runs Maven verification (tests plus packaging), builds a Docker image on pushes and pull requests, then publishes the executable Spring Boot JAR as a workflow artifact.
- **Android client:** Android WebView wrapper with server URL configuration; the debug APK is built by GitHub Actions.

## Repository and CI

- Source: [GitHub repository](https://github.com/solankiabhay/opticianbilling)
- Java CI: [workflow runs](https://github.com/solankiabhay/opticianbilling/actions/workflows/java-ci.yml)
- Android build: [workflow runs](https://github.com/solankiabhay/opticianbilling/actions/workflows/android-apk.yml)

The Java CI run for each latest commit should be checked before release. Download the `surya-optics-application-jar` artifact from a successful run to obtain the packaged backend. A passing unit-test build does not replace manual browser/mobile QA against a real MySQL database.

## Setup

Requirements: JDK 8 and Maven, plus MySQL 8 (or a compatible MySQL server).

1. Create a database, for example surya_optics.
2. Copy .env.example values into your process/container environment and replace all placeholders with strong secrets.
3. Set DB_URL, DB_USERNAME, DB_PASSWORD, APP_USER, and APP_PASSWORD. APP_PASSWORD must be at least 12 characters.
4. Start locally with mvn spring-boot:run or package with mvn --batch-mode test package.
5. Open http://localhost:8080; Spring Security presents the login screen. The login username is APP_USER.

Do not commit .env files, production credentials, or real customer data.

## Database changes

The project currently uses spring.jpa.hibernate.ddl-auto=update for convenience. For production, prefer a reviewed migration process and consider switching to validate after applying migrations.

- database/migrations/20261025_add_customer_age.sql
- database/migrations/20261025_add_inventory_and_payments.sql
- database/migrations/20261025_add_invoice_line_items.sql

Back up the database before applying schema changes. Check the existing schema and constraints first; do not run a migration blindly on a production database.

## Container deployment

Docker and Docker Compose are supported for a local or staging deployment.

1. Copy `.env.example` to `.env` and replace the database, MySQL root and application password placeholders with unique strong values.
2. Run `docker compose up --build -d` from the repository root.
3. Open `http://localhost:8080` and sign in with the `APP_USER` and `APP_PASSWORD` values from `.env`.
4. Inspect startup with `docker compose logs -f app`; stop with `docker compose down`. The MySQL data remains in the named `mysql_data` volume.

The Compose setup is for local/staging use. For public production access, put the application behind HTTPS, restrict database network access, configure backups and monitoring, and review the database migration strategy before upgrading.

## Android client

The APK is a WebView client for a deployed backend; it does not bundle Spring Boot or MySQL. Deploy the backend over HTTPS, then configure the backend URL in the Android app using its Server button. A debug APK is for testing only. Play Store or production distribution needs a signed release build and protected release keystore.

## Important limitations before production

- Invoice forms support multiple inventory products; manual subtotal and optical service charges can also be included.
- Payment history records the amount, method, timestamp and an initial-payment note; it is not a payment gateway integration.
- CSV export covers invoice summaries, not a full accounting ledger.
- Unit tests cover billing totals, multi-item stock deduction, payment transitions, validation-error handling and CSV formula-injection protection, but do not verify every rendered Thymeleaf page or live MySQL migration. Run browser QA with a disposable database and test Android against a real HTTPS deployment.
- Configure backups, HTTPS, database least-privilege access, log retention and monitoring before handling production customer data.
