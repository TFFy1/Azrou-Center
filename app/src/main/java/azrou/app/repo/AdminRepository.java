package azrou.app.repo;

import azrou.app.config.DatabaseManager;
import azrou.app.model.entity.Admin;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminRepository {
    private static final Logger logger = LoggerFactory.getLogger(AdminRepository.class);

    public Optional<Admin> findByUsername(String username) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Query<Admin> query = session.createQuery("FROM Admin WHERE username = :username", Admin.class);
            query.setParameter("username", username);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding admin by username: {}", username, e);
            throw e;
        }
    }

    public void save(Admin admin) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(admin);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error saving admin", e);
            throw e;
        }
    }

    public long count() {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            return session.createQuery("SELECT count(a) FROM Admin a", Long.class).uniqueResult();
        }
    }
}
