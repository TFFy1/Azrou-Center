package azrou.app.dao;

import azrou.app.db.DataAccessException;
import azrou.app.db.DatabaseManager;
import azrou.app.model.entity.Group;
import azrou.app.model.entity.Student;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAO implements GenericDAO<Student, Integer> {

    private final GroupDAO groupDAO = new GroupDAO();

    @Override
    public Optional<Student> findById(Integer id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding student by ID: " + id, e);
        }
        return Optional.empty();
    }

    public Optional<Student> findByCin(String cin) {
        String sql = "SELECT * FROM students WHERE cin = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cin);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding student by CIN: " + cin, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY full_name";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all students", e);
        }
        return students;
    }

    public List<Student> findByGroupId(Integer groupId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE group_id = ? ORDER BY full_name";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding students by group ID: " + groupId, e);
        }
        return students;
    }

    @Override
    public Student save(Student entity) {
        String sql = "INSERT INTO students (group_id, full_name, cin, qualifications, date_of_birth, phone, photo_path, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, entity.getGroup().getId());
            pstmt.setString(2, entity.getFullName());
            pstmt.setString(3, entity.getCin());
            pstmt.setString(4, entity.getQualifications());
            pstmt.setDate(5, entity.getDateOfBirth() != null ? Date.valueOf(entity.getDateOfBirth()) : null);
            pstmt.setString(6, entity.getPhone());
            pstmt.setString(7, entity.getPhotoPath());
            if (entity.getCreatedAt() != null) {
                pstmt.setTimestamp(8, java.sql.Timestamp.valueOf(entity.getCreatedAt()));
            } else {
                pstmt.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis()));
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Creating student failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                } else {
                    throw new DataAccessException("Creating student failed, no ID obtained.");
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new DataAccessException("Error saving student: " + entity.getFullName(), e);
        }
    }

    @Override
    public void update(Student entity) {
        String sql = "UPDATE students SET group_id = ?, full_name = ?, cin = ?, qualifications = ?, date_of_birth = ?, phone = ?, photo_path = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, entity.getGroup().getId());
            pstmt.setString(2, entity.getFullName());
            pstmt.setString(3, entity.getCin());
            pstmt.setString(4, entity.getQualifications());
            pstmt.setDate(5, entity.getDateOfBirth() != null ? Date.valueOf(entity.getDateOfBirth()) : null);
            pstmt.setString(6, entity.getPhone());
            pstmt.setString(7, entity.getPhotoPath());
            pstmt.setInt(8, entity.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating student: " + entity.getId(), e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting student: " + id, e);
        }
    }

    private Student mapResultSetToEntity(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setFullName(rs.getString("full_name"));
        student.setCin(rs.getString("cin"));
        student.setQualifications(rs.getString("qualifications"));
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            student.setDateOfBirth(dob.toLocalDate());
        }
        student.setPhone(rs.getString("phone"));
        student.setPhotoPath(rs.getString("photo_path"));
        try {
            java.sql.Timestamp ts = rs.getTimestamp("created_at");
            if (ts != null) {
                student.setCreatedAt(ts.toLocalDateTime());
            }
        } catch (SQLException e) {
            String tsStr = rs.getString("created_at");
            if (tsStr != null) {
                try {
                    tsStr = tsStr.replace("T", " ");
                    if (!tsStr.contains(".")) {
                        tsStr += ".0";
                    }
                    student.setCreatedAt(java.sql.Timestamp.valueOf(tsStr).toLocalDateTime());
                } catch (IllegalArgumentException ex) {
                    // Ignore
                }
            }
        }

        // Eager fetch Group for simplicity, or could be lazy if needed.
        // For now, fetching the group to ensure the object is complete.
        int groupId = rs.getInt("group_id");
        Optional<Group> group = groupDAO.findById(groupId);
        group.ifPresent(student::setGroup);

        return student;
    }
}
