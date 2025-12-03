package azrou.app.service;

import azrou.app.model.dto.SubjectDto;
import azrou.app.model.entity.Group;
import azrou.app.model.entity.Subject;
import azrou.app.repo.GroupRepository;
import azrou.app.repo.SubjectRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubjectService {
    private static final Logger logger = LoggerFactory.getLogger(SubjectService.class);
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;

    public SubjectService(SubjectRepository subjectRepository, GroupRepository groupRepository) {
        this.subjectRepository = subjectRepository;
        this.groupRepository = groupRepository;
    }

    public List<SubjectDto> getSubjectsByGroup(Integer groupId) {
        return subjectRepository.findByGroupId(groupId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SubjectDto createSubject(Integer groupId, String name, String code) {
        if (subjectRepository.findByGroupAndName(groupId, name).isPresent()) {
            throw new IllegalArgumentException("Subject with name " + name + " already exists in this group.");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));

        Subject subject = new Subject();
        subject.setGroup(group);
        subject.setName(name);
        subject.setCode(code);

        subjectRepository.save(subject);
        logger.info("Created subject: {} for group {}", name, groupId);
        return toDto(subject);
    }

    public void updateSubject(Integer id, Integer groupId, String name, String code) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found with ID: " + id));

        if (!subject.getName().equals(name) && subjectRepository.findByGroupAndName(groupId, name).isPresent()) {
            throw new IllegalArgumentException("Subject with name " + name + " already exists in this group.");
        }

        if (!subject.getGroup().getId().equals(groupId)) {
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));
            subject.setGroup(group);
        }

        subject.setName(name);
        subject.setCode(code);

        subjectRepository.update(subject);
        logger.info("Updated subject: {}", name);
    }

    public void deleteSubject(Integer id) {
        Optional<Subject> subjectOpt = subjectRepository.findById(id);
        if (subjectOpt.isPresent()) {
            subjectRepository.delete(subjectOpt.get());
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
