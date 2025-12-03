package azrou.app.repo;

import azrou.app.config.DatabaseManager;
import azrou.app.model.entity.Group;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupRepository {
    private static final Logger logger = LoggerFactory.getLogger(GroupRepository.class);

    public List<Group> findAll() {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            return session.createQuery("FROM Group", Group.class).list();
        }
    }

    public Optional<Group> findById(Integer id) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Group.class, id));
        }
    }

    public Optional<Group> findByName(String name) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Query<Group> query = session.createQuery("FROM Group WHERE name = :name", Group.class);
            query.setParameter("name", name);
            return query.uniqueResultOptional();
        }
    }

    public void save(Group group) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(group);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error saving group: {}", group.getName(), e);
            throw e;
        }
    }

    public void update(Group group) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(group);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error updating group: {}", group.getName(), e);
            throw e;
        }
    }

    public void delete(Group group) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(group);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error deleting group: {}", group.getName(), e);
            throw e;
        }
    }
}
