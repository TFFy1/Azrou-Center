module azrou.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;
    requires ch.qos.logback.classic;

    requires jbcrypt;
    requires net.coobird.thumbnailator;
    requires org.apache.pdfbox;
    requires java.desktop;

    opens azrou.app to javafx.graphics, javafx.fxml;
    opens azrou.app.ui.login to javafx.fxml;
    opens azrou.app.ui.groups to javafx.fxml;
    opens azrou.app.ui.students to javafx.fxml;
    opens azrou.app.ui.subjects to javafx.fxml;
    opens azrou.app.ui.assessments to javafx.fxml;
    opens azrou.app.ui.grades to javafx.fxml;
    opens azrou.app.ui.absences to javafx.fxml;
    opens azrou.app.ui.imports to javafx.fxml;
    opens azrou.app.ui.main to javafx.fxml;
    opens azrou.app.ui.backup to javafx.fxml;
    opens azrou.app.ui.reports to javafx.fxml;
    opens azrou.app.ui.teachers to javafx.fxml;

    exports azrou.app;
}
