package jyrs.dev.vivesbank.movements.storage;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import jyrs.dev.vivesbank.movements.models.Movement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MovementPdfGenerator {

    public static void generateMovementPdf(String filePath, Movement movement) {
        try {
            ensureDirectoryExists(filePath);

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Detalles del Movimiento").setFontSize(14));

            document.add(new Paragraph("ID: " + movement.getId()));
            document.add(new Paragraph("Tipo de Movimiento: " + movement.getTypeMovement()));
            document.add(new Paragraph("Fecha: " + formatDateTime(movement.getDate())));
            document.add(new Paragraph("Cantidad: " + movement.getAmount()));
            document.add(new Paragraph("Saldo: " + movement.getBalance()));
            document.add(new Paragraph("Reversible: " + movement.getIsReversible()));
            document.add(new Paragraph("Fecha Límite: " + formatDateTime(movement.getTransferDeadlineDate())));
            document.add(new Paragraph("Cuenta Origen: " + (movement.getOrigin() != null ? movement.getOrigin().getId() : "N/A")));
            document.add(new Paragraph("Cuenta Destino: " + (movement.getDestination() != null ? movement.getDestination().getId() : "N/A")));
            document.add(new Paragraph("Cliente Remitente: " + (movement.getSenderClient() != null ? movement.getSenderClient().getId() : "N/A")));
            document.add(new Paragraph("Cliente Destinatario: " + (movement.getRecipientClient() != null ? movement.getRecipientClient().getId() : "N/A")));

            document.close();
            System.out.println("PDF generado exitosamente: " + filePath);
        } catch (IOException e) {
            System.err.println("Error al generar el PDF: " + e.getMessage());
        }
    }

    public static void generateMovementsPdf(String filePath, List<Movement> movements) {
        try {
            ensureDirectoryExists(filePath);

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Lista de Movimientos").setFontSize(14));

            Table table = new Table(6); // Número de columnas
            table.addCell("ID");
            table.addCell("Tipo");
            table.addCell("Fecha");
            table.addCell("Cantidad");
            table.addCell("Saldo");
            table.addCell("Reversible");

            for (Movement movement : movements) {
                table.addCell(movement.getId());
                table.addCell(movement.getTypeMovement());
                table.addCell(formatDateTime(movement.getDate()));
                table.addCell(String.valueOf(movement.getAmount()));
                table.addCell(String.valueOf(movement.getBalance()));
                table.addCell(String.valueOf(movement.getIsReversible()));
            }

            document.add(table);
            document.close();
            System.out.println("PDF generado exitosamente: " + filePath);
        } catch (IOException e) {
            System.err.println("Error al generar el PDF: " + e.getMessage());
        }
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }

    private static void ensureDirectoryExists(String filePath) throws IOException {
        Path directory = Path.of(filePath).getParent();
        if (directory != null && !Files.exists(directory)) {
            Files.createDirectories(directory);
            System.out.println("Directorio creado: " + directory);
        }
    }
}

