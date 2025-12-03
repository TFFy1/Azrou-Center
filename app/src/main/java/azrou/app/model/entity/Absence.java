package azrou.app.model.entity;

import java.time.LocalDate;

public class Absence extends BaseEntity {
    private Student student;

    private Subject subject;

    private LocalDate date;

    private Boolean justified = false;

    private String reason;

    private Admin recordedBy;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getJustified() {
        return justified;
    }

    public void setJustified(Boolean justified) {
        this.justified = justified;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Admin getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(Admin recordedBy) {
        this.recordedBy = recordedBy;
    }
}
