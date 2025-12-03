package azrou.app.repo;

import azrou.app.config.DatabaseManager;
import azrou.app.model.entity.Subject;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubjectRepository {
    private static final Logger logger = LoggerFactory.getLogger(SubjectRepository.class);

    public List<Subject> findAll() {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            return session.createQuery("FROM Subject s JOIN FETCH s.group", Subject.class).list();
        }
    }

    public List<Subject> findByGroupId(Integer groupId) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Query<Subject> query = session.createQuery("FROM Subject s JOIN FETCH s.group WHERE s.group.id = :groupId",
                    Subject.class);
            query.setParameter("groupId", groupId);
            return query.list();
        }
    }

    public Optional<Subject> findById(Integer id) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Subject.class, id));
        }
    }

    public Optional<Subject> findByGroupAndName(Integer groupId, String name) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Query<Subject> query = session.createQuery("FROM Subject s WHERE s.group.id = :groupId AND s.name = :name",
                    Subject.class);
            query.setParameter("groupId", groupId);
            query.setParameter("name", name);
            return query.uniqueResultOptional();
        }
    }

    public void save(Subject subject) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(subject);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error saving subject: {}", subject.getName(), e);
            throw e;
        }
    }

    public void update(Subject subject) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(subject);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error updating subject: {}", subject.getName(), e);
            throw e;
        }
    }

    public void delete(Subject subject) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(subject);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error deleting subject: {}", subject.getName(), e);
            throw e;
        }
    }
}
