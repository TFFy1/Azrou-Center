package azrou.app.repo;

import azrou.app.config.DatabaseManager;
import azrou.app.model.entity.Assessment;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssessmentRepository {
    private static final Logger logger = LoggerFactory.getLogger(AssessmentRepository.class);

    public List<Assessment> findAll() {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            return session.createQuery("FROM Assessment a JOIN FETCH a.subject s JOIN FETCH s.group", Assessment.class)
                    .list();
        }
    }

    public List<Assessment> findBySubjectId(Integer subjectId) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Query<Assessment> query = session.createQuery(
                    "FROM Assessment a JOIN FETCH a.subject s JOIN FETCH s.group WHERE a.subject.id = :subjectId",
                    Assessment.class);
            query.setParameter("subjectId", subjectId);
            return query.list();
        }
    }

    public Optional<Assessment> findById(Integer id) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Assessment.class, id));
        }
    }

    public void save(Assessment assessment) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(assessment);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error saving assessment: {}", assessment.getName(), e);
            throw e;
        }
    }

    public void update(Assessment assessment) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(assessment);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error updating assessment: {}", assessment.getName(), e);
            throw e;
        }
    }

    public void delete(Assessment assessment) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(assessment);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error deleting assessment: {}", assessment.getName(), e);
            throw e;
        }
    }
}
