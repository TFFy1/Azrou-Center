package azrou.app.service;

import azrou.app.model.entity.Admin;
import azrou.app.repo.AdminRepository;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final AdminRepository adminRepository;
    private Admin currentUser;

    public AuthService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public boolean login(String username, String password) {
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (BCrypt.checkpw(password, admin.getPasswordHash())) {
                currentUser = admin;
                logger.info("User logged in: {}", username);
                return true;
            }
        }
        logger.warn("Failed login attempt for user: {}", username);
        return false;
    }

    public void logout() {
        if (currentUser != null) {
            logger.info("User logged out: {}", currentUser.getUsername());
            currentUser = null;
        }
    }

    public Admin getCurrentUser() {
        return currentUser;
    }

    public void createInitialAdminIfNotExists() {
        if (adminRepository.count() == 0) {
            logger.info("No admins found. Creating default admin.");
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPasswordHash(BCrypt.hashpw("admin", BCrypt.gensalt()));
            admin.setFullName("Administrator");
            adminRepository.save(admin);
        }
    }
}
