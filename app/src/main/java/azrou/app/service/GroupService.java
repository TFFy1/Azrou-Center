package azrou.app.service;

import azrou.app.dao.GroupDAO;
import azrou.app.model.dto.GroupDto;
import azrou.app.model.entity.Group;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupService {
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final GroupDAO groupDAO;

    public GroupService() {
        this.groupDAO = new GroupDAO();
    }

    public List<GroupDto> getAllGroups() {
        return groupDAO.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public GroupDto createGroup(String name, String description, Integer capacity) {
        if (groupDAO.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Group with name " + name + " already exists.");
        }

        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setCapacity(capacity);

        groupDAO.save(group);
        logger.info("Created group: {}", name);
        return toDto(group);
    }

    public void updateGroup(Integer id, String name, String description, Integer capacity) {
        Optional<Group> groupOpt = groupDAO.findById(id);
        if (groupOpt.isEmpty()) {
            throw new IllegalArgumentException("Group not found with ID: " + id);
        }

        Group group = groupOpt.get();

        // Check name uniqueness if changed
        if (!group.getName().equals(name) && groupDAO.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Group with name " + name + " already exists.");
        }

        group.setName(name);
        group.setDescription(description);
        group.setCapacity(capacity);

        groupDAO.update(group);
        logger.info("Updated group: {}", name);
    }

    public void deleteGroup(Integer id) {
        Optional<Group> groupOpt = groupDAO.findById(id);
        if (groupOpt.isPresent()) {
            groupDAO.delete(id);
            logger.info("Deleted group with ID: {}", id);
        } else {
            logger.warn("Attempted to delete non-existent group ID: {}", id);
        }
    }

    private GroupDto toDto(Group group) {
        return new GroupDto(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCapacity(),
                group.getCreatedAt());
    }
}
