package azrou.app.service;

import azrou.app.config.AppConfig;
import azrou.app.model.dto.GroupDto;
import azrou.app.model.dto.StudentDto;
import azrou.app.repo.GroupRepository;
import azrou.app.repo.StudentRepository;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;

    public ReportService(StudentRepository studentRepository, GroupRepository groupRepository) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
    }

    public File generateGroupListReport(Integer groupId) throws IOException {
        GroupDto group = groupRepository.findById(groupId)
                .map(g -> new GroupDto(g.getId(), g.getName(), g.getDescription(), g.getCapacity(), g.getCreatedAt()))
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId));

        List<StudentDto> students = studentRepository.findByGroupId(groupId).stream()
                .map(s -> new StudentDto(s.getId(), s.getGroup().getId(), s.getGroup().getName(),
                        s.getFullName(), s.getCin(), s.getQualifications(), s.getDateOfBirth(), s.getPhone(),
                        s.getPhotoPath(), s.getCreatedAt()))
                .toList();

        File reportFile = createReportFile("Group_" + group.name() + "_List");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Title
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Group List: " + group.name());
                contentStream.endText();

                // Date
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(50, 730);
                contentStream.showText(
                        "Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                contentStream.endText();

                // Table Header
                float yPosition = 700;
                float margin = 50;
                float yStart = yPosition;
                float bottomMargin = 70;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float rowHeight = 20;

                drawTableHeader(contentStream, margin, yPosition, tableWidth, rowHeight);
                yPosition -= rowHeight;

                // Table Content
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                for (StudentDto student : students) {
                    if (yPosition < bottomMargin) {
                        // New Page (Simplified for now, just stop or create new page logic needed for
                        // robust app)
                        // For MVP, let's just break or we need to close stream and create new page
                        break;
                    }

                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 5, yPosition - 15);
                    contentStream.showText(student.cin());
                    contentStream.newLineAtOffset(100, 0);
                    contentStream.showText(student.fullName());
                    contentStream.newLineAtOffset(200, 0);
                    contentStream.showText(student.phone() != null ? student.phone() : "");
                    contentStream.endText();

                    yPosition -= rowHeight;
                }
            }

            document.save(reportFile);
        }

        logger.info("Generated report: {}", reportFile.getAbsolutePath());
        return reportFile;
    }

    private void drawTableHeader(PDPageContentStream contentStream, float x, float y, float width, float height)
            throws IOException {
        contentStream.setLineWidth(1f);
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + width, y);
        contentStream.stroke();

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(x + 5, y - 15);
        contentStream.showText("CIN");
        contentStream.newLineAtOffset(100, 0);
        contentStream.showText("Full Name");
        contentStream.newLineAtOffset(200, 0);
        contentStream.showText("Phone");
        contentStream.endText();

        contentStream.moveTo(x, y - height);
        contentStream.lineTo(x + width, y - height);
        contentStream.stroke();
    }

    private File createReportFile(String baseName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = baseName + "_" + timestamp + ".pdf";
        return AppConfig.EXPORTS_DIR.resolve(filename).toFile();
    }
}
