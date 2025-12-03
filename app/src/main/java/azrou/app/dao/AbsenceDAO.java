package azrou.app.dao;

import azrou.app.db.DataAccessException;
import azrou.app.db.DatabaseManager;
import azrou.app.model.entity.Absence;
import azrou.app.model.entity.Student;
import azrou.app.model.entity.Subject;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AbsenceDAO implements GenericDAO<Absence, Integer> {

    private final StudentDAO studentDAO = new StudentDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();
    // AdminDAO is not strictly required yet as we might not be fully implementing
    // Admin management in this step,
    // but we need to handle the recordedBy field. For now, we can leave it null or
    // implement a basic AdminDAO if needed.
    // Given the instructions, I will focus on the core entities. If Admin is
    // needed, I'll add it.
    // For now, I'll just store the ID and maybe fetch it if I implement AdminDAO.
    // Let's assume AdminDAO exists or we just handle the ID.
    // Actually, the user asked for "AdminDAO (if needed for Auth)".
    // I'll implement a basic AdminDAO later if required. For now, I'll just set
    // recordedBy to null or fetch if I add AdminDAO.

    // To keep it clean, I will add a placeholder for Admin fetching or just ignore
    // it if not critical.
    // However, the model has Admin recordedBy.
    // I'll implement a simple AdminDAO to be safe.

    @Override
    public Optional<Absence> findById(Integer id) {
        String sql = "SELECT * FROM absences WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding absence by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Absence> findAll() {
        List<Absence> absences = new ArrayList<>();
        String sql = "SELECT * FROM absences ORDER BY date DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                absences.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all absences", e);
        }
        return absences;
    }

    public List<Absence> findByStudentId(Integer studentId) {
        List<Absence> absences = new ArrayList<>();
        String sql = "SELECT * FROM absences WHERE student_id = ? ORDER BY date DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    absences.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding absences by student ID: " + studentId, e);
        }
        return absences;
    }

    public List<Absence> findByGroupId(Integer groupId) {
        List<Absence> absences = new ArrayList<>();
        String sql = "SELECT a.* FROM absences a JOIN students s ON a.student_id = s.id WHERE s.group_id = ? ORDER BY a.date DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    absences.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding absences by group ID: " + groupId, e);
        }
        return absences;
    }

    public List<Absence> findBySubjectId(Integer subjectId) {
        List<Absence> absences = new ArrayList<>();
        String sql = "SELECT * FROM absences WHERE subject_id = ? ORDER BY date DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, subjectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    absences.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding absences by subject ID: " + subjectId, e);
        }
        return absences;
    }

    @Override
    public Absence save(Absence entity) {
        String sql = "INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, entity.getStudent().getId());
            pstmt.setInt(2, entity.getSubject().getId());
            pstmt.setDate(3, Date.valueOf(entity.getDate()));
            pstmt.setBoolean(4, entity.getJustified());
            pstmt.setString(5, entity.getReason());
            if (entity.getRecordedBy() != null) {
                pstmt.setInt(6, entity.getRecordedBy().getId());
            } else {
                pstmt.setObject(6, null);
            }
            if (entity.getCreatedAt() != null) {
                pstmt.setTimestamp(7, java.sql.Timestamp.valueOf(entity.getCreatedAt()));
            } else {
                pstmt.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis()));
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Creating absence failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                } else {
                    throw new DataAccessException("Creating absence failed, no ID obtained.");
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new DataAccessException("Error saving absence", e);
        }
    }

    @Override
    public void update(Absence entity) {
        String sql = "UPDATE absences SET student_id = ?, subject_id = ?, date = ?, justified = ?, reason = ?, recorded_by = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, entity.getStudent().getId());
            pstmt.setInt(2, entity.getSubject().getId());
            pstmt.setDate(3, Date.valueOf(entity.getDate()));
            pstmt.setBoolean(4, entity.getJustified());
            pstmt.setString(5, entity.getReason());
            if (entity.getRecordedBy() != null) {
                pstmt.setInt(6, entity.getRecordedBy().getId());
            } else {
                pstmt.setObject(6, null);
            }
            pstmt.setInt(7, entity.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating absence: " + entity.getId(), e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM absences WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting absence: " + id, e);
        }
    }

    private Absence mapResultSetToEntity(ResultSet rs) throws SQLException {
        Absence absence = new Absence();
        absence.setId(rs.getInt("id"));
        absence.setDate(rs.getDate("date").toLocalDate());
        absence.setJustified(rs.getBoolean("justified"));
        absence.setReason(rs.getString("reason"));
        try {
            java.sql.Timestamp ts = rs.getTimestamp("created_at");
            if (ts != null) {
                absence.setCreatedAt(ts.toLocalDateTime());
            }
        } catch (SQLException e) {
            String tsStr = rs.getString("created_at");
            if (tsStr != null) {
                try {
                    tsStr = tsStr.replace("T", " ");
                    if (!tsStr.contains(".")) {
                        tsStr += ".0";
                    }
                    absence.setCreatedAt(java.sql.Timestamp.valueOf(tsStr).toLocalDateTime());
                } catch (IllegalArgumentException ex) {
                    // Ignore
                }
            }
        }

        int studentId = rs.getInt("student_id");
        Optional<Student> student = studentDAO.findById(studentId);
        student.ifPresent(absence::setStudent);

        int subjectId = rs.getInt("subject_id");
        Optional<Subject> subject = subjectDAO.findById(subjectId);
        subject.ifPresent(absence::setSubject);

        // Skipping Admin fetching for now as AdminDAO is not yet implemented.
        // If needed, I will implement AdminDAO and fetch it here.

        return absence;
    }
}
