package azrou.app.dao;

import azrou.app.db.DataAccessException;
import azrou.app.db.DatabaseManager;
import azrou.app.model.entity.Assessment;
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

public class AssessmentDAO implements GenericDAO<Assessment, Integer> {

    private final SubjectDAO subjectDAO = new SubjectDAO();

    @Override
    public Optional<Assessment> findById(Integer id) {
        String sql = "SELECT * FROM assessments WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding assessment by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Assessment> findAll() {
        List<Assessment> assessments = new ArrayList<>();
        String sql = "SELECT * FROM assessments ORDER BY date DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                assessments.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all assessments", e);
        }
        return assessments;
    }

    public List<Assessment> findBySubjectId(Integer subjectId) {
        List<Assessment> assessments = new ArrayList<>();
        String sql = "SELECT * FROM assessments WHERE subject_id = ? ORDER BY date DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, subjectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    assessments.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding assessments by subject ID: " + subjectId, e);
        }
        return assessments;
    }

    @Override
    public Assessment save(Assessment entity) {
        String sql = "INSERT INTO assessments (subject_id, name, date, max_score, weight, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, entity.getSubject().getId());
            pstmt.setString(2, entity.getName());
            pstmt.setDate(3, entity.getDate() != null ? Date.valueOf(entity.getDate()) : null);
            pstmt.setDouble(4, entity.getMaxScore());
            pstmt.setDouble(5, entity.getWeight());
            if (entity.getCreatedAt() != null) {
                pstmt.setTimestamp(6, java.sql.Timestamp.valueOf(entity.getCreatedAt()));
            } else {
                pstmt.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Creating assessment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                } else {
                    throw new DataAccessException("Creating assessment failed, no ID obtained.");
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new DataAccessException("Error saving assessment: " + entity.getName(), e);
        }
    }

    @Override
    public void update(Assessment entity) {
        String sql = "UPDATE assessments SET subject_id = ?, name = ?, date = ?, max_score = ?, weight = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, entity.getSubject().getId());
            pstmt.setString(2, entity.getName());
            pstmt.setDate(3, entity.getDate() != null ? Date.valueOf(entity.getDate()) : null);
            pstmt.setDouble(4, entity.getMaxScore());
            pstmt.setDouble(5, entity.getWeight());
            pstmt.setInt(6, entity.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating assessment: " + entity.getId(), e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM assessments WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting assessment: " + id, e);
        }
    }

    private Assessment mapResultSetToEntity(ResultSet rs) throws SQLException {
        Assessment assessment = new Assessment();
        assessment.setId(rs.getInt("id"));
        assessment.setName(rs.getString("name"));
        Date date = rs.getDate("date");
        if (date != null) {
            assessment.setDate(date.toLocalDate());
        }
        assessment.setMaxScore(rs.getDouble("max_score"));
        assessment.setWeight(rs.getDouble("weight"));
        try {
            java.sql.Timestamp ts = rs.getTimestamp("created_at");
            if (ts != null) {
                assessment.setCreatedAt(ts.toLocalDateTime());
            }
        } catch (SQLException e) {
            String tsStr = rs.getString("created_at");
            if (tsStr != null) {
                try {
                    tsStr = tsStr.replace("T", " ");
                    if (!tsStr.contains(".")) {
                        tsStr += ".0";
                    }
                    assessment.setCreatedAt(java.sql.Timestamp.valueOf(tsStr).toLocalDateTime());
                } catch (IllegalArgumentException ex) {
                    // Ignore
                }
            }
        }

        int subjectId = rs.getInt("subject_id");
        Optional<Subject> subject = subjectDAO.findById(subjectId);
        subject.ifPresent(assessment::setSubject);

        return assessment;
    }
}
