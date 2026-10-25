package com.surya.optics.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.surya.optics.entity.Bill;
import com.surya.optics.entity.BillItem;
import com.surya.optics.entity.Payment;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InvoicePdfService {
    private static final Font TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font HEADING = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
    private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 9);
    private static final Font BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);

    public void write(Bill bill, List<BillItem> items, List<Payment> payments, OutputStream output)
            throws DocumentException {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, output);
        document.open();
        Paragraph title = new Paragraph("SURYA OPTICS — INVOICE", TITLE);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));
        PdfPTable meta = new PdfPTable(new float[]{1, 1});
        meta.setWidthPercentage(100);
        meta.addCell(cell("Invoice: " + safe(bill.getBillNumber()), BOLD));
        meta.addCell(cell("Date: " + (bill.getBillDate() == null ? "—" : bill.getBillDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))), NORMAL));
        meta.addCell(cell("Customer: " + (bill.getCustomer() == null ? "—" : safe(bill.getCustomer().getName())), NORMAL));
        meta.addCell(cell("Phone: " + (bill.getCustomer() == null ? "—" : safe(bill.getCustomer().getPhone())), NORMAL));
        document.add(meta);
        document.add(new Paragraph(" "));
        PdfPTable lines = new PdfPTable(new float[]{4, 1, 1.5f, 1.5f});
        lines.setWidthPercentage(100);
        lines.setHeaderRows(1);
        lines.addCell(cell("Item / SKU", HEADING));
        lines.addCell(cell("Qty", HEADING));
        lines.addCell(cell("Unit price", HEADING));
        lines.addCell(cell("Line total", HEADING));
        for (BillItem item : items) {
            lines.addCell(cell(safe(item.getProductName()) + (item.getProductSku() == null ? "" : "\nSKU: " + safe(item.getProductSku())), NORMAL));
            lines.addCell(cell(String.valueOf(item.getQuantity()), NORMAL));
            lines.addCell(cell(money(item.getUnitPrice()), NORMAL));
            lines.addCell(cell(money(item.getLineTotal()), NORMAL));
        }
        document.add(lines);
        document.add(new Paragraph(" "));
        PdfPTable totals = new PdfPTable(new float[]{3, 1});
        totals.setWidthPercentage(55);
        totals.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row(totals, "Manual item subtotal", bill.getSubtotal());
        row(totals, "Contact lens charges", bill.getContactLensCharges());
        row(totals, "Sunglasses charges", bill.getSunglassCharges());
        row(totals, "Eye testing", bill.getEyeTestingCharges());
        row(totals, "Other charges", bill.getOtherCharges());
        row(totals, "INVOICE TOTAL", bill.getTotal());
        row(totals, "Total received", bill.getAdvance());
        row(totals, "BALANCE DUE", bill.getBalance());
        document.add(totals);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Status: " + safe(bill.getStatus()), BOLD));
        if (bill.getDeliveryDate() != null) document.add(new Paragraph("Expected delivery: " + bill.getDeliveryDate(), NORMAL));
        if (bill.getNotes() != null && !bill.getNotes().trim().isEmpty()) document.add(new Paragraph("Notes: " + safe(bill.getNotes()), NORMAL));
        if (payments != null && !payments.isEmpty()) {
            document.add(new Paragraph("Payment receipts", HEADING));
            PdfPTable receiptTable = new PdfPTable(new float[]{2, 2, 3});
            receiptTable.setWidthPercentage(100);
            receiptTable.addCell(cell("Paid at", HEADING));
            receiptTable.addCell(cell("Method", HEADING));
            receiptTable.addCell(cell("Amount", HEADING));
            for (Payment payment : payments) {
                receiptTable.addCell(cell(payment.getPaidAt() == null ? "—" : payment.getPaidAt().toString(), NORMAL));
                receiptTable.addCell(cell(safe(payment.getPaymentMode()), NORMAL));
                receiptTable.addCell(cell(money(payment.getAmount()), NORMAL));
            }
            document.add(receiptTable);
        }
        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph("Thank you for choosing Surya Optics.", NORMAL);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
        document.close();
    }

    private void row(PdfPTable table, String label, BigDecimal amount) {
        table.addCell(cell(label, BOLD));
        table.addCell(cell(money(amount), NORMAL));
    }

    private PdfPCell cell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "—" : text, font));
        cell.setPadding(6);
        return cell;
    }

    private String money(BigDecimal amount) {
        return "INR " + (amount == null ? "0.00" : amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
    }

    private String safe(String value) {
        if (value == null || value.trim().isEmpty()) return "—";
        return value.replace('\r', ' ').replace('\n', ' ');
    }
}
