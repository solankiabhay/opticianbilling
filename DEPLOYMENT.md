# Deploy Surya Optics Billing from GitHub

The repository is prepared to deploy the Spring Boot application as a Docker web service. The Android APK is a client and does not contain the backend or database. Deploy the backend first, then enter its public HTTPS URL in the APK.

## Recommended path: Railway + MySQL

GitHub stores the source code; Railway (or another Docker host) runs the application. A persistent MySQL database is required. Do not use GitHub Pages for this application because Pages cannot run Spring Boot or MySQL.

### 1. Push/choose the repository

1. Sign in to Railway and create a project.
2. Choose **Deploy from GitHub repo** and select `solankiabhay/opticianbilling`.
3. Let Railway build the repository's `Dockerfile`. The included `railway.json` sets the Docker build and `/login` health check.
4. Add a MySQL service to the same Railway project. Keep the database private; do not expose its port publicly.

### 2. Configure application variables

In the Spring Boot application's service, add the following variables. Use Railway's variable-reference picker to select the values exposed by your MySQL service; exact names may vary by provider.

- `PORT=8080` (Railway may inject PORT automatically; the app reads it)
- `DB_URL=jdbc:mysql://<MYSQL_PRIVATE_HOST>:<MYSQL_PORT>/<MYSQL_DATABASE>?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
- `DB_USERNAME=<MYSQL_USERNAME>`
- `DB_PASSWORD=<MYSQL_PASSWORD>`
- `APP_USER=admin` (or another unique login name)
- `APP_PASSWORD=<unique password of at least 12 characters>`
- `LOG_FILE=/tmp/surya-optics.log`

Replace every placeholder with the private connection details from the database service. Do not commit actual credentials into GitHub, README files, or this deployment guide. Prefer the provider's private network connection string for the database.

### 3. Deploy and verify

1. Redeploy the application after saving variables.
2. Wait for the service to report healthy.
3. Generate/enable the provider's public HTTPS domain.
4. Open `https://<your-generated-domain>/login` in a browser. Sign in using `APP_USER` and `APP_PASSWORD`.
5. Test creating a disposable customer, product, invoice and payment before entering real customer data.
6. Install the Android APK, press **Server**, and enter the exact HTTPS base URL (for example, `https://your-service-domain.up.railway.app` without `/login`). Sign in from the app.

## Local Docker test

1. Install Docker Engine/Desktop and the Docker Compose plugin.
2. Copy `.env.example` to `.env`.
3. Replace `DB_PASSWORD`, `MYSQL_ROOT_PASSWORD` and `APP_PASSWORD` with unique strong values. Ensure the app/database `DB_PASSWORD` matches.
4. Run `docker compose up --build -d`.
5. Open `http://localhost:8080/login`.
6. Inspect logs with `docker compose logs -f app`; stop with `docker compose down`. The named `mysql_data` volume persists database data.

## Before using real customer data

- Configure automated database backups and test restoring one.
- Keep MySQL private and use a unique database user/password.
- Use HTTPS only for public access.
- Set strong, unique `APP_PASSWORD` and database credentials in the host's secret/environment settings.
- Verify schema migrations and restore procedures before upgrades. Hibernate currently uses `ddl-auto=update`; review the existing schema and migration SQL before production upgrades.
- Test login, invoices, inventory stock changes, payments, reports, PDF downloads and Android uploads/downloads on the deployed service.
- A successful CI build is not the same as a completed production security review or a backup/restore test.

## Troubleshooting

- **Container fails to start:** check service logs and confirm `APP_PASSWORD` is at least 12 characters.
- **Database connection failure:** confirm `DB_URL` uses the database's private hostname, correct port/database name, and that username/password belong to that database.
- **Health check fails:** open the deployment logs and confirm the app starts and can connect to MySQL; the configured health check is `/login`.
- **Android says it needs an HTTPS address:** the backend must be deployed and have a public HTTPS domain first. Enter the base domain, not a database URL or a GitHub repository URL.
