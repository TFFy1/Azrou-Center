package azrou.app.service;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceLocator {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLocator.class);
    private static final ServiceLocator instance = new ServiceLocator();
    private final Map<Class<?>, Object> services = new HashMap<>();

    private ServiceLocator() {
    }

    public static ServiceLocator getInstance() {
        return instance;
    }

    public <T> void register(Class<T> clazz, T service) {
        services.put(clazz, service);
        logger.info("Registered service: {}", clazz.getName());
    }

    public <T> T get(Class<T> clazz) {
        return clazz.cast(services.get(clazz));
    }

    public void clear() {
        services.clear();
    }
}
