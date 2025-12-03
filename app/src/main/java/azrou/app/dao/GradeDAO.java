package azrou.app.dao;

import azrou.app.db.DataAccessException;
import azrou.app.db.DatabaseManager;
import azrou.app.model.entity.Assessment;
import azrou.app.model.entity.Grade;
import azrou.app.model.entity.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GradeDAO implements GenericDAO<Grade, Integer> {

    private final StudentDAO studentDAO = new StudentDAO();
    private final AssessmentDAO assessmentDAO = new AssessmentDAO();

    @Override
    public Optional<Grade> findById(Integer id) {
        String sql = "SELECT * FROM grades WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding grade by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Grade> findAll() {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                grades.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all grades", e);
        }
        return grades;
    }

    public List<Grade> findByAssessmentId(Integer assessmentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE assessment_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, assessmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding grades by assessment ID: " + assessmentId, e);
        }
        return grades;
    }

    public Optional<Grade> findByStudentAndAssessment(Integer studentId, Integer assessmentId) {
        String sql = "SELECT * FROM grades WHERE student_id = ? AND assessment_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, assessmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding grade by student and assessment", e);
        }
        return Optional.empty();
    }

    @Override
    public Grade save(Grade entity) {
        String sql = "INSERT INTO grades (student_id, assessment_id, score, recorded_at, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, entity.getStudent().getId());
            pstmt.setInt(2, entity.getAssessment().getId());
            pstmt.setObject(3, entity.getScore());
            pstmt.setTimestamp(4, entity.getRecordedAt() != null ? Timestamp.valueOf(entity.getRecordedAt()) : null);
            if (entity.getCreatedAt() != null) {
                pstmt.setTimestamp(5, java.sql.Timestamp.valueOf(entity.getCreatedAt()));
            } else {
                pstmt.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Creating grade failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                } else {
                    throw new DataAccessException("Creating grade failed, no ID obtained.");
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new DataAccessException("Error saving grade", e);
        }
    }

    @Override
    public void update(Grade entity) {
        String sql = "UPDATE grades SET student_id = ?, assessment_id = ?, score = ?, recorded_at = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, entity.getStudent().getId());
            pstmt.setInt(2, entity.getAssessment().getId());
            pstmt.setObject(3, entity.getScore());
            pstmt.setTimestamp(4, entity.getRecordedAt() != null ? Timestamp.valueOf(entity.getRecordedAt()) : null);
            pstmt.setInt(5, entity.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating grade: " + entity.getId(), e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM grades WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting grade: " + id, e);
        }
    }

    private Grade mapResultSetToEntity(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        grade.setId(rs.getInt("id"));
        grade.setScore((Double) rs.getObject("score"));
        Timestamp recordedAt = rs.getTimestamp("recorded_at");
        if (recordedAt != null) {
            grade.setRecordedAt(recordedAt.toLocalDateTime());
        }
        try {
            java.sql.Timestamp ts = rs.getTimestamp("created_at");
            if (ts != null) {
                grade.setCreatedAt(ts.toLocalDateTime());
            }
        } catch (SQLException e) {
            String tsStr = rs.getString("created_at");
            if (tsStr != null) {
                try {
                    tsStr = tsStr.replace("T", " ");
                    if (!tsStr.contains(".")) {
                        tsStr += ".0";
                    }
                    grade.setCreatedAt(java.sql.Timestamp.valueOf(tsStr).toLocalDateTime());
                } catch (IllegalArgumentException ex) {
                    // Ignore
                }
            }
        }

        int studentId = rs.getInt("student_id");
        Optional<Student> student = studentDAO.findById(studentId);
        student.ifPresent(grade::setStudent);

        int assessmentId = rs.getInt("assessment_id");
        Optional<Assessment> assessment = assessmentDAO.findById(assessmentId);
        assessment.ifPresent(grade::setAssessment);

        return grade;
    }
}
