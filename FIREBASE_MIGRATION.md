# Surya Optics Billing — Firebase migration

## Goal

Move the Android billing experience toward Firebase Authentication + Cloud Firestore so multiple Android phones can share the same shop data, with Firestore's Android offline cache enabled. The target is Firebase Spark where possible; Spark quotas are limits, not a promise that every feature or usage level will remain free.

## Important current-state note

The current Android app is a WebView wrapper around the Spring Boot + MySQL web application. It does not contain the server or database. Adding Firebase configuration alone does **not** migrate existing customers, prescriptions, invoices, stock, or payments. This migration requires implementing Firebase-backed screens/data access and importing existing data deliberately.

## Target data layout

- `shops/{shopId}` — shop profile/settings
- `shops/{shopId}/members/{uid}` — membership record; fields include `active: true` and `role: "owner" | "staff"`
- `shops/{shopId}/customers/{customerId}`
- `shops/{shopId}/prescriptions/{prescriptionId}`
- `shops/{shopId}/invoices/{invoiceId}`
- `shops/{shopId}/inventory/{productId}`
- `shops/{shopId}/payments/{paymentId}`
- `shops/{shopId}/settings/{settingId}`

Store money as integer paise, not floating-point values. Keep invoice line items and totals together in the invoice document where practical. Invoice creation must use Firestore transactions/batched writes to ensure stock deduction and payment/balance updates are consistent across devices. Never generate invoice numbers using a simple local counter; use a transaction-backed counter document.

## Security

- Use Firebase Authentication email/password (not SMS OTP).
- Deploy `firestore.rules` before using real data. Rules require a signed-in user with an active membership document for that shop.
- Membership/shop documents cannot be created or modified by the mobile client in these initial rules. Create the shop and first owner membership through Firebase Console for initial testing, then implement a trusted owner-only member-management flow.
- Do not place service-account private keys or admin SDK credentials in the APK or repository.
- Do not use permissive rules such as `allow read, write: if true`.
- Prescription data is sensitive; use minimum necessary data and test access control with Firebase Emulator before production.

## Firebase Console setup (manual prerequisite)

1. Create a Firebase project at https://console.firebase.google.com/ and choose the Spark plan.
2. Add an Android app with package name `com.surya.optics.mobile`.
3. Download that app's `google-services.json` and put it at `android-app/app/google-services.json`. Do not commit it if the project policy treats Firebase identifiers as private; Firebase API keys are not server secrets, but security must be enforced by rules and App Check.
4. Enable **Authentication → Email/Password**.
5. Create the Cloud Firestore database in production mode and choose the closest suitable region. Database region cannot be casually changed later.
6. Deploy rules/indexes from this repository using Firebase CLI after installing it and authenticating: `firebase deploy --only firestore:rules,firestore:indexes`.
7. Create the initial `shops/{shopId}` document and `shops/{shopId}/members/{uid}` document for the owner. Set `active: true`, and create an Authentication user with that UID.
8. Only use test data until migration and authorization checks are complete.

## Offline, sync, and backup

The Firestore Android SDK can cache data the app has used and synchronize local changes when online again. Concurrent edits to the same document use last-write-wins unless the application implements stronger conflict handling. The app must show pending/offline status and must not imply that another phone has received an offline change yet.

Firestore managed backups/restore and point-in-time recovery require billing. For a zero-monthly-cost workflow, implement an owner-initiated export to a local file and let Android's share/save UI place it in Google Drive. A Drive copy is a backup, not real-time multi-device sync.

## Migration acceptance checklist

- [ ] Android Firebase SDK and email/password sign-in wired into the app.
- [ ] Customer and prescription screens use Firestore.
- [ ] Inventory, invoices, stock deduction, payments, and reports migrated with transaction-safe logic.
- [ ] Existing MySQL data export/import validated with totals and record counts.
- [ ] Firestore rules tested using Firebase Emulator, including unauthorized-user and cross-shop tests.
- [ ] Offline edits, reconnect, duplicate invoice prevention, and simultaneous billing tested on two real phones.
- [ ] Export/restore tested and backup file stored safely.
- [ ] CI builds APK with the Firebase plugin and project configuration.
- [ ] Production release signed; no credentials committed.

## Free-tier notes

Spark includes free Firestore quotas, currently documented at 1 GiB stored data, 50,000 document reads/day, 20,000 writes/day, 20,000 deletes/day, and 10 GiB outbound transfer/month. If a Spark quota is exceeded, the affected service can stop until the quota resets or the project is upgraded. Managed backups/restore are not included in the free quota. Confirm current pricing before launch.
