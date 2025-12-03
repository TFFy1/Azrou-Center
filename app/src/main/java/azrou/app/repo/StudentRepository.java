package azrou.app.repo;

import azrou.app.config.DatabaseManager;
import azrou.app.model.entity.Student;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudentRepository {
    private static final Logger logger = LoggerFactory.getLogger(StudentRepository.class);

    public List<Student> findAll() {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            return session.createQuery("FROM Student s JOIN FETCH s.group", Student.class).list();
        }
    }

    public List<Student> findByGroupId(Integer groupId) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Query<Student> query = session.createQuery("FROM Student s JOIN FETCH s.group WHERE s.group.id = :groupId",
                    Student.class);
            query.setParameter("groupId", groupId);
            return query.list();
        }
    }

    public Optional<Student> findById(Integer id) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Student student = session.get(Student.class, id);
            if (student != null) {
                // Initialize lazy collection if needed, or join fetch above
                // For single entity get, Hibernate usually handles eager fetching if configured
                // or accessed
                // But here we might need to ensure group is loaded if we access it outside
                // session
                // However, our DTO mapper will likely trigger it.
                // Better to use a query with fetch if we want to be safe for DTO mapping
                // outside session
                return Optional.of(student);
            }
            return Optional.empty();
        }
    }

    public Optional<Student> findByCin(String cin) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Query<Student> query = session.createQuery("FROM Student s JOIN FETCH s.group WHERE s.cin = :cin",
                    Student.class);
            query.setParameter("cin", cin);
            return query.uniqueResultOptional();
        }
    }

    public void save(Student student) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(student);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error saving student: {}", student.getCin(), e);
            throw e;
        }
    }

    public void update(Student student) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(student);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error updating student: {}", student.getCin(), e);
            throw e;
        }
    }

    public void delete(Student student) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(student);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error deleting student: {}", student.getCin(), e);
            throw e;
        }
    }
}
