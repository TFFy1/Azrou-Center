package azrou.app.repo;

import azrou.app.config.DatabaseManager;
import azrou.app.model.entity.Absence;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbsenceRepository {
    private static final Logger logger = LoggerFactory.getLogger(AbsenceRepository.class);

    public List<Absence> findByStudentId(Integer studentId) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Query<Absence> query = session
                    .createQuery("FROM Absence a JOIN FETCH a.subject WHERE a.student.id = :studentId", Absence.class);
            query.setParameter("studentId", studentId);
            return query.list();
        }
    }

    public List<Absence> findByGroupId(Integer groupId) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Query<Absence> query = session.createQuery(
                    "FROM Absence a JOIN FETCH a.student s JOIN FETCH a.subject WHERE s.group.id = :groupId",
                    Absence.class);
            query.setParameter("groupId", groupId);
            return query.list();
        }
    }

    public Optional<Absence> findById(Integer id) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Absence.class, id));
        }
    }

    public void save(Absence absence) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(absence);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error saving absence", e);
            throw e;
        }
    }

    public void update(Absence absence) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(absence);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error updating absence", e);
            throw e;
        }
    }

    public void delete(Absence absence) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(absence);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error deleting absence", e);
            throw e;
        }
    }
}
