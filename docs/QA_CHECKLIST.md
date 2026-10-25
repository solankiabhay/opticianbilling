# Release QA checklist

Run against a disposable MySQL database and a test-only account. Do not use real customer data.

## Backend and authentication
- [ ] Start with JDK 8, MySQL credentials and a unique APP_PASSWORD (12+ characters).
- [ ] Confirm unauthenticated browser access redirects to login.
- [ ] Sign in with APP_USER and verify logout ends the session.
- [ ] Confirm startup fails when APP_PASSWORD is blank or too short.

## Customers and prescriptions
- [ ] Create a customer with no age, then with ages 0 and 130.
- [ ] Confirm age -1 and 131 are rejected by validation.
- [ ] Edit a customer and confirm purchase/prescription history still appears.
- [ ] Create and edit a prescription with positive SPH/CYL/ADD; verify the UI displays the leading plus sign.
- [ ] Confirm deleting a customer/prescription uses POST rather than a destructive GET link.

## Inventory and invoice
- [ ] Create a product with a unique SKU; attempt the same SKU with different letter case and confirm rejection.
- [ ] Set stock to 5, invoice quantity 2, and verify invoice total uses the server-side selling price.
- [ ] Confirm product stock changes from 5 to 3 and the invoice snapshot shows name, SKU, quantity and unit price.
- [ ] Try quantity above available stock and verify no invoice or stock change is committed.
- [ ] Try a negative price, negative stock or negative adjustment and verify validation rejects it.
- [ ] Confirm a bill with no inventory product can still use the manual subtotal and optical service charges.

## Payments and reporting
- [ ] Create an invoice with an initial advance and confirm a payment receipt is stored.
- [ ] Record a partial payment, then the remaining balance; verify the status transitions PENDING → PAID.
- [ ] Verify zero, negative and over-balance payments are rejected.
- [ ] Filter reports by date boundaries and verify billed/received/outstanding totals.
- [ ] Export CSV and open it in a spreadsheet; verify commas, quotes and formula-leading cell values are safely escaped.

## Browser and Android client
- [ ] Check dashboard, customer, prescription, inventory, invoice and reports pages at desktop and mobile widths.
- [ ] Verify forms retain user-entered values after validation errors.
- [ ] Build the debug APK from GitHub Actions, configure an HTTPS backend URL, log in, and test navigation.
- [ ] Verify app behavior with slow/unavailable network and server session expiry.

## Release gate
- [ ] Latest Java CI workflow run is green on the exact commit to be released.
- [ ] Database migrations are reviewed and applied to a staging database after backup.
- [ ] HTTPS, least-privilege database credentials, backups, logging and monitoring are configured.
- [ ] Production owner accepts the known limitation: current invoice UI supports one inventory product line plus manual subtotal, not multiple distinct product rows.
