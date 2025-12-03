package azrou.app.service;

import azrou.app.dao.GroupDAO;
import azrou.app.dao.SubjectDAO;
import azrou.app.model.dto.SubjectDto;
import azrou.app.model.entity.Group;
import azrou.app.model.entity.Subject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubjectService {
    private static final Logger logger = LoggerFactory.getLogger(SubjectService.class);
    private final SubjectDAO subjectDAO;
    private final GroupDAO groupDAO;

    public SubjectService() {
        this.subjectDAO = new SubjectDAO();
        this.groupDAO = new GroupDAO();
    }

    public List<SubjectDto> getSubjectsByGroup(Integer groupId) {
        return subjectDAO.findByGroupId(groupId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SubjectDto> getAllSubjects() {
        return subjectDAO.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SubjectDto createSubject(Integer groupId, String name, String code) {
        if (subjectDAO.findByGroupAndName(groupId, name).isPresent()) {
            throw new IllegalArgumentException("Subject with name " + name + " already exists in this group.");
        }

        Group group = groupDAO.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));

        Subject subject = new Subject();
        subject.setGroup(group);
        subject.setName(name);
        subject.setCode(code);
        subject.setCreatedAt(java.time.LocalDateTime.now());

        subjectDAO.save(subject);
        logger.info("Created subject: {} for group {}", name, groupId);
        return toDto(subject);
    }

    public void updateSubject(Integer id, Integer groupId, String name, String code) {
        Subject subject = subjectDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found with ID: " + id));

        if (!subject.getName().equals(name) && subjectDAO.findByGroupAndName(groupId, name).isPresent()) {
            throw new IllegalArgumentException("Subject with name " + name + " already exists in this group.");
        }

        if (!subject.getGroup().getId().equals(groupId)) {
            Group group = groupDAO.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));
            subject.setGroup(group);
        }

        subject.setName(name);
        subject.setCode(code);

        subjectDAO.update(subject);
        logger.info("Updated subject: {}", name);
    }

    public void deleteSubject(Integer id) {
        Optional<Subject> subjectOpt = subjectDAO.findById(id);
        if (subjectOpt.isPresent()) {
            subjectDAO.delete(id);
            logger.info("Deleted subject with ID: {}", id);
        }
    }

    private SubjectDto toDto(Subject subject) {
        return new SubjectDto(
                subject.getId(),
                subject.getGroup().getId(),
                subject.getGroup().getName(),
                subject.getName(),
                subject.getCode(),
                subject.getCreatedAt());
    }
}
