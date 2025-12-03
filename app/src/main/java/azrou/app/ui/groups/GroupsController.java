package azrou.app.ui.groups;

import azrou.app.i18n.I18n;
import azrou.app.model.dto.GroupDto;
import azrou.app.service.GroupService;
import azrou.app.service.ServiceLocator;
import java.util.Optional;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class GroupsController {
    @FXML
    private Label titleLabel;
    @FXML
    private TableView<GroupDto> groupsTable;
    @FXML
    private TableColumn<GroupDto, String> nameColumn;
    @FXML
    private TableColumn<GroupDto, String> descriptionColumn;
    @FXML
    private TableColumn<GroupDto, Integer> capacityColumn;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField capacityField;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;

    private final GroupService groupService;
    private final ObservableList<GroupDto> groupsList = FXCollections.observableArrayList();
    private GroupDto selectedGroup;

    public GroupsController() {
        this.groupService = ServiceLocator.getInstance().get(GroupService.class);
    }

    @FXML
    public void initialize() {
        setupTable();
        loadGroups();
        setupBindings();

        groupsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectGroup(newVal);
            }
        });
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().description()));
        capacityColumn
                .setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().capacity()).asObject());

        groupsTable.setItems(groupsList);
    }

    private void loadGroups() {
        groupsList.setAll(groupService.getAllGroups());
    }

    private void setupBindings() {
        titleLabel.textProperty().bind(I18n.createStringBinding("menu.groups"));
        addButton.textProperty().bind(I18n.createStringBinding("common.add"));
        updateButton.textProperty().bind(I18n.createStringBinding("common.edit"));
        deleteButton.textProperty().bind(I18n.createStringBinding("common.delete"));
        clearButton.textProperty().bind(I18n.createStringBinding("common.cancel"));

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void selectGroup(GroupDto group) {
        selectedGroup = group;
        nameField.setText(group.name());
        descriptionField.setText(group.description());
        capacityField.setText(String.valueOf(group.capacity()));

        addButton.setDisable(true);
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    @FXML
    private void handleAdd() {
        try {
            String name = nameField.getText();
            String description = descriptionField.getText();
            int capacity = Integer.parseInt(capacityField.getText());

            groupService.createGroup(name, description, capacity);
            loadGroups();
            handleClear();
        } catch (NumberFormatException e) {
            showError("Invalid capacity");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedGroup == null)
            return;
        try {
            String name = nameField.getText();
            String description = descriptionField.getText();
            int capacity = Integer.parseInt(capacityField.getText());

            groupService.updateGroup(selectedGroup.id(), name, description, capacity);
            loadGroups();
            handleClear();
        } catch (NumberFormatException e) {
            showError("Invalid capacity");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedGroup == null)
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(I18n.get("common.confirm"));
        alert.setHeaderText("Delete Group " + selectedGroup.name() + "?");
        alert.setContentText("This will delete all students in this group!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                groupService.deleteGroup(selectedGroup.id());
                loadGroups();
                handleClear();
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        selectedGroup = null;
        nameField.clear();
        descriptionField.clear();
        capacityField.clear();
        groupsTable.getSelectionModel().clearSelection();

        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(I18n.get("common.error"));
        alert.setContentText(message);
        alert.showAndWait();
    }
}
