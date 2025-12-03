package azrou.app.dao;

import azrou.app.db.DataAccessException;
import azrou.app.db.DatabaseManager;
import azrou.app.model.entity.Group;
import azrou.app.model.entity.Subject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubjectDAO implements GenericDAO<Subject, Integer> {

    private final GroupDAO groupDAO = new GroupDAO();

    @Override
    public Optional<Subject> findById(Integer id) {
        String sql = "SELECT * FROM subjects WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding subject by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Subject> findAll() {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects ORDER BY name";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                subjects.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all subjects", e);
        }
        return subjects;
    }

    public List<Subject> findByGroupId(Integer groupId) {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects WHERE group_id = ? ORDER BY name";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    subjects.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding subjects by group ID: " + groupId, e);
        }
        return subjects;
    }

    public Optional<Subject> findByGroupAndName(Integer groupId, String name) {
        String sql = "SELECT * FROM subjects WHERE group_id = ? AND name = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setString(2, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding subject by group and name", e);
        }
        return Optional.empty();
    }

    @Override
    public Subject save(Subject entity) {
        String sql = "INSERT INTO subjects (group_id, name, code, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, entity.getGroup().getId());
            pstmt.setString(2, entity.getName());
            pstmt.setString(3, entity.getCode());
            if (entity.getCreatedAt() != null) {
                pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(entity.getCreatedAt()));
            } else {
                pstmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Creating subject failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                } else {
                    throw new DataAccessException("Creating subject failed, no ID obtained.");
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new DataAccessException("Error saving subject: " + entity.getName(), e);
        }
    }

    @Override
    public void update(Subject entity) {
        String sql = "UPDATE subjects SET group_id = ?, name = ?, code = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, entity.getGroup().getId());
            pstmt.setString(2, entity.getName());
            pstmt.setString(3, entity.getCode());
            pstmt.setInt(4, entity.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating subject: " + entity.getId(), e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM subjects WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting subject: " + id, e);
        }
    }

    private Subject mapResultSetToEntity(ResultSet rs) throws SQLException {
        Subject subject = new Subject();
        subject.setId(rs.getInt("id"));
        subject.setName(rs.getString("name"));
        subject.setCode(rs.getString("code"));
        try {
            java.sql.Timestamp ts = rs.getTimestamp("created_at");
            if (ts != null) {
                subject.setCreatedAt(ts.toLocalDateTime());
            }
        } catch (SQLException e) {
            // Fallback for string parsing if getTimestamp fails
            String tsStr = rs.getString("created_at");
            if (tsStr != null) {
                try {
                    // Handle ISO format with 'T' or space
                    tsStr = tsStr.replace("T", " ");
                    // If it doesn't have milliseconds, append them or parse loosely
                    if (!tsStr.contains(".")) {
                        tsStr += ".0";
                    }
                    subject.setCreatedAt(java.sql.Timestamp.valueOf(tsStr).toLocalDateTime());
                } catch (IllegalArgumentException ex) {
                    // Ignore invalid dates
                }
            }
        }

        int groupId = rs.getInt("group_id");
        Optional<Group> group = groupDAO.findById(groupId);
        group.ifPresent(subject::setGroup);

        return subject;
    }
}
