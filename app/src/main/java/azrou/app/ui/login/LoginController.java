package azrou.app.ui.login;

import azrou.app.i18n.I18n;
import azrou.app.service.AuthService;
import azrou.app.service.ServiceLocator;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class LoginController {
    @FXML
    private Label titleLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private Label passwordLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;
    @FXML
    private Label forgotPasswordLabel;
    @FXML
    private ToggleButton enToggle;
    @FXML
    private ToggleButton frToggle;

    private final AuthService authService;

    public LoginController() {
        this.authService = ServiceLocator.getInstance().get(AuthService.class);
    }

    @FXML
    public void initialize() {
        bindI18n();
        setupLanguageToggle();

        loginButton.setOnAction(e -> handleLogin());

        // Reset error on type
        usernameField.textProperty().addListener((obs, o, n) -> errorLabel.setText(""));
        passwordField.textProperty().addListener((obs, o, n) -> errorLabel.setText(""));
    }

    private void bindI18n() {
        titleLabel.textProperty().bind(I18n.createStringBinding("login.title"));
        usernameLabel.textProperty().bind(I18n.createStringBinding("login.username"));
        passwordLabel.textProperty().bind(I18n.createStringBinding("login.password"));
        loginButton.textProperty().bind(I18n.createStringBinding("login.button"));
        forgotPasswordLabel.textProperty().bind(I18n.createStringBinding("login.forgot_password"));
    }

    // Helper for I18n binding since I didn't implement createStringBinding in I18n
    // yet
    // I will implement a simple update method for now to avoid complex bindings if
    // I18n doesn't support it yet.
    // Actually, I18n class I wrote uses static methods. I should add a listener to
    // locale property.

    private void setupLanguageToggle() {
        ToggleGroup group = new ToggleGroup();
        enToggle.setToggleGroup(group);
        frToggle.setToggleGroup(group);

        if (I18n.getLocale().equals(Locale.ENGLISH)) {
            enToggle.setSelected(true);
        } else {
            frToggle.setSelected(true);
        }

        group.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == enToggle) {
                I18n.setLocale(Locale.ENGLISH);
            } else if (newVal == frToggle) {
                I18n.setLocale(Locale.FRENCH);
            }
        });

        I18n.localeProperty().addListener((obs, o, n) -> updateTexts());
        updateTexts();
    }

    private void updateTexts() {
        titleLabel.setText(I18n.get("login.title"));
        usernameLabel.setText(I18n.get("login.username"));
        passwordLabel.setText(I18n.get("login.password"));
        loginButton.setText(I18n.get("login.button"));
        forgotPasswordLabel.setText(I18n.get("login.forgot_password"));
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (authService.login(username, password)) {
            errorLabel.setText("");
            try {
                javafx.scene.Parent mainView = javafx.fxml.FXMLLoader
                        .load(getClass().getResource("/azrou/app/ui/main/main.fxml"));
                titleLabel.getScene().setRoot(mainView);
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Failed to load dashboard: " + e.getMessage());
            }
        } else {
            errorLabel.setText(I18n.get("login.error"));
        }
    }
}
