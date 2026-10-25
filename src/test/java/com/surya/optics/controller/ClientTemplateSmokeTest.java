package com.surya.optics.controller;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientTemplateSmokeTest {
    private String template(String path) throws IOException {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("templates/" + path)) {
            if (stream == null) throw new AssertionError("Missing client template: " + path);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int count;
            while ((count = stream.read(buffer)) != -1) output.write(buffer, 0, count);
            return new String(output.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void sharedLayoutSupportsMobileNavigationAndAccessibility() throws Exception {
        String header = template("layout/header.html");
        String navbar = template("layout/navbar.html");
        String sidebar = template("layout/sidebar.html");
        String footer = template("layout/footer.html");

        assertTrue(header.contains("name=\"viewport\""), "Responsive viewport metadata must be present");
        assertTrue(header.contains("@media(max-width:991.98px)"), "Tablet/mobile layout rules must be present");
        assertTrue(navbar.contains("id=\"mobileMenuToggle\""), "Mobile menu toggle must be present");
        assertTrue(navbar.contains("aria-expanded=\"false\""), "Menu state must be accessible");
        assertTrue(sidebar.contains("id=\"appSidebar\""), "Sidebar must be addressable by the menu control");
        assertTrue(sidebar.contains("id=\"mobileMenuClose\""), "Mobile sidebar needs a close control");
        assertTrue(footer.contains("event.key === 'Escape'"), "Escape key must close the mobile menu");
        assertTrue(footer.contains("sidebar-backdrop"), "Mobile navigation must support a backdrop");
    }

    @Test
    void mainClientPagesExposeExpectedNavigationAndFormControls() throws Exception {
        String login = template("login.html");
        String dashboard = template("dashboard.html");
        String bills = template("bills/list.html");
        String customers = template("customers/list.html");
        String products = template("products/list.html");
        String reports = template("reports/index.html");

        assertTrue(login.contains("autocomplete=\"username\""));
        assertTrue(login.contains("autocomplete=\"current-password\""));
        assertTrue(dashboard.contains("@{/bills/add}"));
        assertTrue(bills.contains("@{/bills/export.csv}"));
        assertTrue(customers.contains("name=\"keyword\""));
        assertTrue(products.contains("name=\"delta\""));
        assertTrue(reports.contains("name=\"start\"") && reports.contains("name=\"end\""));
    }
}
