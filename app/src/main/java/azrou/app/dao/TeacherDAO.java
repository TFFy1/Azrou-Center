package azrou.app.dao;

import azrou.app.db.DataAccessException;
import azrou.app.db.DatabaseManager;
import azrou.app.model.entity.Teacher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherDAO implements GenericDAO<Teacher, Integer> {
    private final DatabaseManager dbManager;

    public TeacherDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public Optional<Teacher> findById(Integer id) {
        String sql = "SELECT * FROM teachers WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding teacher by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Teacher> findAll() {
        String sql = "SELECT * FROM teachers ORDER BY full_name";
        List<Teacher> teachers = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                teachers.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all teachers", e);
        }
        return teachers;
    }

    @Override
    public Teacher save(Teacher entity) {
        String sql = "INSERT INTO teachers (full_name, email, phone) VALUES (?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, entity.getFullName());
            stmt.setString(2, entity.getEmail());
            stmt.setString(3, entity.getPhone());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Creating teacher failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                    
                    // We can optionally query again to get full data, but for now just returning the entity with new ID
                    // is sufficient since timestamp is auto-generated usually.
                    return findById(entity.getId()).orElse(entity);
                } else {
                    throw new DataAccessException("Creating teacher failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error saving teacher", e);
        }
    }

    @Override
    public void update(Teacher entity) {
        String sql = "UPDATE teachers SET full_name = ?, email = ?, phone = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getFullName());
            stmt.setString(2, entity.getEmail());
            stmt.setString(3, entity.getPhone());
            stmt.setInt(4, entity.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Updating teacher failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating teacher", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM teachers WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Deleting teacher failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting teacher by id: " + id, e);
        }
    }

    private Teacher mapResultSetToEntity(ResultSet rs) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setId(rs.getInt("id"));
        teacher.setFullName(rs.getString("full_name"));
        teacher.setEmail(rs.getString("email"));
        teacher.setPhone(rs.getString("phone"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            teacher.setCreatedAt(createdAt.toLocalDateTime());
        }

        return teacher;
    }
}
