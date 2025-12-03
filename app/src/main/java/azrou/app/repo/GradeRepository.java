package azrou.app.repo;

import azrou.app.config.DatabaseManager;
import azrou.app.model.entity.Grade;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradeRepository {
    private static final Logger logger = LoggerFactory.getLogger(GradeRepository.class);

    public List<Grade> findByAssessmentId(Integer assessmentId) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Query<Grade> query = session.createQuery(
                    "FROM Grade g JOIN FETCH g.student WHERE g.assessment.id = :assessmentId", Grade.class);
            query.setParameter("assessmentId", assessmentId);
            return query.list();
        }
    }

    public List<Grade> findByStudentId(Integer studentId) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Query<Grade> query = session
                    .createQuery("FROM Grade g JOIN FETCH g.assessment WHERE g.student.id = :studentId", Grade.class);
            query.setParameter("studentId", studentId);
            return query.list();
        }
    }

    public Optional<Grade> findByStudentAndAssessment(Integer studentId, Integer assessmentId) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Query<Grade> query = session.createQuery(
                    "FROM Grade g WHERE g.student.id = :studentId AND g.assessment.id = :assessmentId", Grade.class);
            query.setParameter("studentId", studentId);
            query.setParameter("assessmentId", assessmentId);
            return query.uniqueResultOptional();
        }
    }

    public void save(Grade grade) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(grade);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error saving grade", e);
            throw e;
        }
    }

    public void update(Grade grade) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(grade);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error updating grade", e);
            throw e;
        }
    }

    public void saveOrUpdate(Grade grade) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(grade); // merge handles both save and update
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error saving/updating grade", e);
            throw e;
        }
    }
}
