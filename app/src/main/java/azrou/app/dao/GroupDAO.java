package azrou.app.dao;

import azrou.app.db.DataAccessException;
import azrou.app.db.DatabaseManager;
import azrou.app.model.entity.Group;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroupDAO implements GenericDAO<Group, Integer> {

    @Override
    public Optional<Group> findById(Integer id) {
        String sql = "SELECT * FROM groups WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding group by ID: " + id, e);
        }
        return Optional.empty();
    }

    public Optional<Group> findByName(String name) {
        String sql = "SELECT * FROM groups WHERE name = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding group by name: " + name, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Group> findAll() {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT * FROM groups ORDER BY name";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                groups.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all groups", e);
        }
        return groups;
    }

    @Override
    public Group save(Group entity) {
        String sql = "INSERT INTO groups (name, description, capacity, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getDescription());
            pstmt.setInt(3, entity.getCapacity());
            if (entity.getCreatedAt() != null) {
                pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(entity.getCreatedAt()));
            } else {
                pstmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Creating group failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                } else {
                    throw new DataAccessException("Creating group failed, no ID obtained.");
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new DataAccessException("Error saving group: " + entity.getName(), e);
        }
    }

    @Override
    public void update(Group entity) {
        String sql = "UPDATE groups SET name = ?, description = ?, capacity = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getDescription());
            pstmt.setInt(3, entity.getCapacity());
            pstmt.setInt(4, entity.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating group: " + entity.getId(), e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM groups WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting group: " + id, e);
        }
    }

    private Group mapResultSetToEntity(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setId(rs.getInt("id"));
        group.setName(rs.getString("name"));
        group.setDescription(rs.getString("description"));
        group.setCapacity(rs.getInt("capacity"));
        try {
            java.sql.Timestamp ts = rs.getTimestamp("created_at");
            if (ts != null) {
                group.setCreatedAt(ts.toLocalDateTime());
            }
        } catch (SQLException e) {
            String tsStr = rs.getString("created_at");
            if (tsStr != null) {
                try {
                    tsStr = tsStr.replace("T", " ");
                    if (!tsStr.contains(".")) {
                        tsStr += ".0";
                    }
                    group.setCreatedAt(java.sql.Timestamp.valueOf(tsStr).toLocalDateTime());
                } catch (IllegalArgumentException ex) {
                    // Ignore
                }
            }
        }
        return group;
    }
}
