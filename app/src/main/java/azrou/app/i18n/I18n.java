package azrou.app.i18n;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class I18n {
    private static final Logger logger = LoggerFactory.getLogger(I18n.class);
    private static final ObjectProperty<Locale> locale = new SimpleObjectProperty<>(Locale.ENGLISH);
    private static ResourceBundle bundle;

    static {
        locale.addListener((obs, oldLocale, newLocale) -> loadBundle(newLocale));
        loadBundle(Locale.ENGLISH);
    }

    private static void loadBundle(Locale loc) {
        try {
            bundle = ResourceBundle.getBundle("messages", loc);
            logger.info("Loaded resource bundle for locale: {}", loc);
        } catch (Exception e) {
            logger.error("Failed to load resource bundle for locale: {}", loc, e);
        }
    }

    public static String get(String key) {
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getString(key);
        }
        return key; // Fallback to key if not found
    }

    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    public static void setLocale(Locale loc) {
        locale.set(loc);
    }

    public static Locale getLocale() {
        return locale.get();
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static javafx.beans.binding.StringBinding createStringBinding(String key) {
        return javafx.beans.binding.Bindings.createStringBinding(() -> get(key), locale);
    }
}
