package azrou.app.ui.login;

import azrou.app.App;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxAssert;
import org.testfx.matcher.control.LabeledMatchers;

import static org.testfx.api.FxAssert.verifyThat;

public class LoginTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        new App().start(stage);
    }

    @Test
    public void shouldShowLoginScreen() {
        verifyThat(".button", LabeledMatchers.hasText("Login"));
    }
}
