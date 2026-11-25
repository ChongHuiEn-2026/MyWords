package mywords.ui;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import mywords.model.Phrase;

/** Creates and writes a report from only the Phrase objects passed as user-selected confirmed phrases. */
public class ReportGenerator {
    public String buildReport(Phrase[] selectedPhrases) {
        StringBuilder report = new StringBuilder();
        report.append("MyWords Conversation Summary\n");
        report.append("Confirmed phrases selected by the communicator\n\n");
        int included = 0;
        if (selectedPhrases != null) {
            for (int i = 0; i < selectedPhrases.length; i++) {
                Phrase phrase = selectedPhrases[i];
                if (phrase == null || phrase.getConfirmedText() == null
                        || phrase.getConfirmedText().trim().isEmpty()) continue;
                included++;
                report.append(included).append(". ")
                        .append(safeField(phrase.getConfirmedText())).append('\n');
            }
        }
        if (included == 0) report.append("(No phrases selected for export.)\n");
        report.append("\nAI suggestions and rejected or unconfirmed content are not included.\n");
        return report.toString();
    }

    /** Writes UTF-8 text through a temporary file so an existing report is preserved on failure. */
    public void writeReport(String reportText, String filePath) throws IOException {
        if (reportText == null) throw new IOException("There is no report text to save.");
        if (filePath == null || filePath.trim().isEmpty()) throw new IOException("Choose a report file location.");
        Path destination = Paths.get(filePath).toAbsolutePath().normalize();
        Path parent = destination.getParent();
        if (parent == null || !Files.isDirectory(parent)) throw new IOException("The selected folder does not exist.");

        Path temporary = Files.createTempFile(parent, "mywords-report-", ".tmp");
        boolean moved = false;
        try {
            Files.write(temporary, reportText.getBytes(StandardCharsets.UTF_8));
            try {
                Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            moved = true;
        } finally {
            if (!moved) Files.deleteIfExists(temporary);
        }
    }

    private String safeField(String value) {
        return value.replace("\r", " ").replace("\n", " ").replace("\t", " ");
    }
}
