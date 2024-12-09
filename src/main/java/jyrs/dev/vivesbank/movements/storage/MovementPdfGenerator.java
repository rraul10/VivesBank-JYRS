package jyrs.dev.vivesbank.movements.storage;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.users.clients.models.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MovementPdfGenerator {

    private static final Logger logger = LoggerFactory.getLogger(MovementPdfGenerator.class);
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private static final String NA = "N/A";

    /**
     * Genera un archivo PDF con los detalles de un movimiento bancario.
     * @param movement El objeto movimiento con los detalles a incluir en el PDF.
     */

    public File generateMovementPdf(Movement movement) {
        File pdfFile = null;
        try {
            File directory = new File("./movements");

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = "movement_" + movement.getSenderClient().getId() + "_" + System.currentTimeMillis() + ".pdf";
            String filePath = directory.getAbsolutePath() + File.separator + fileName;

            pdfFile = new File(filePath);
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Detalles del Movimiento").setFontSize(14));
            addMovementDetailsToDocument(document, movement);

            document.close();

            logger.info("PDF generado exitosamente: {}", filePath);
        } catch (IOException e) {
            logger.error("Error al generar el PDF: {}", e.getMessage(), e);
        }

        return pdfFile;
    }


    /**
     * Genera un archivo PDF con una lista de movimientos bancarios.
     * @param client por si es la lista de un cliente.
     * @param movements La lista de movimientos a incluir en el PDF.
     */

    public File generateMovementsPdf(List<Movement> movements, Optional<Client> client) {
        File pdfFile = null;

        try {
            File directory = new File("./movements");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = "movements_list_" + System.currentTimeMillis() + ".pdf";
            if (client.isPresent()) {
                fileName = "movements_list_" + client.get().getId() + "_" + System.currentTimeMillis() + ".pdf";
            }

            String filePath = directory.getAbsolutePath() + File.separator + fileName;
            pdfFile = new File(filePath);

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            if (client.isPresent()) {
                document.add(new Paragraph("Lista de Mis Movimientos").setFontSize(14));
            } else {
                document.add(new Paragraph("Lista de Movimientos").setFontSize(14));
            }

            Table table = new Table(6);
            table.addCell("ID");
            table.addCell("Tipo");
            table.addCell("Fecha");
            table.addCell("Cantidad");

            for (Movement movement : movements) {
                table.addCell(movement.getId());
                table.addCell(movement.getTypeMovement());
                table.addCell(formatDateTime(movement.getDate()));
                table.addCell(String.valueOf(movement.getAmount()));
            }

            document.add(table);

            document.close();

            logger.info("PDF generado exitosamente: {}", filePath);
        } catch (IOException e) {
            logger.error("Error al generar el PDF: {}", e.getMessage(), e);
        }

        return pdfFile;
    }

    private static void addMovementDetailsToDocument(Document document, Movement movement) {
        document.add(new Paragraph("ID: " + movement.getId()));
        document.add(new Paragraph("Tipo de Movimiento: " + movement.getTypeMovement()));
        document.add(new Paragraph("Fecha: " + formatDateTime(movement.getDate())));
        document.add(new Paragraph("Cantidad: " + movement.getAmount()));
        document.add(new Paragraph("Cuenta Origen: " + (movement.getBankAccountOrigin() != null ? movement.getBankAccountOrigin().getId() : NA)));
        document.add(new Paragraph("Cuenta Destino: " + (movement.getBankAccountDestination() != null ? movement.getBankAccountDestination().getId() : NA)));
        document.add(new Paragraph("Cliente Remitente: " + (movement.getSenderClient() != null ? movement.getSenderClient().getId() : NA)));
        document.add(new Paragraph("Cliente Destinatario: " + (movement.getRecipientClient() != null ? movement.getRecipientClient().getId() : NA)));
    }

    /**
     * Formatea la fecha y hora en el formato dd/MM/yyyy HH:mm:ss.
     * @param dateTime El objeto LocalDateTime que se formateará.
     * @return La fecha formateada como cadena.
     */

    private static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return NA;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return dateTime.format(formatter);
    }
}

