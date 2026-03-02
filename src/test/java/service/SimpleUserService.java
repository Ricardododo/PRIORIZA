package service;

import com.prioriza.model.User;
import com.prioriza.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// Versión simplificada de UserService para pruebas
class SimpleUserService {
    private Map<Integer, User> users = new HashMap<>();
    private Map<String, User> usersByEmail = new HashMap<>();
    private int nextId = 1;

    public User registerUser(String name, String email, String password) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
        if (usersByEmail.containsKey(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        User user = new User(name, email, password);
        user.setId(nextId++);
        user.setRole(UserRole.USER);

        users.put(user.getId(), user);
        usersByEmail.put(email, user);

        return user;
    }

    public User login(String email, String password) {
        User user = usersByEmail.get(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User getUserById(int id) {
        return users.get(id);
    }

    public User getUserByEmail(String email) {
        return usersByEmail.get(email);
    }
}

// Tests del servicio simplificado
class UserServiceSimplifiedTest {

    private SimpleUserService userService;

    @BeforeEach
    void setUp() {
        userService = new SimpleUserService();
    }

    @Test
    @DisplayName("Registro exitoso de usuario")
    void testRegisterSuccess() {
        User user = userService.registerUser("Juan Pérez", "juan@email.com", "password123");

        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("Juan Pérez", user.getName());
        assertEquals("juan@email.com", user.getEmail());
        assertEquals(UserRole.USER, user.getRole());

        // Verificar que se puede recuperar
        User retrieved = userService.getUserByEmail("juan@email.com");
        assertNotNull(retrieved);
        assertEquals(user.getId(), retrieved.getId());
    }

    @Test
    @DisplayName("Registro con email duplicado")
    void testRegisterDuplicateEmail() {
        userService.registerUser("Juan", "juan@email.com", "pass1");

        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser("Otro", "juan@email.com", "pass2");
        });
    }

    @Test
    @DisplayName("Login exitoso")
    void testLoginSuccess() {
        userService.registerUser("Juan", "juan@email.com", "password123");

        User loggedUser = userService.login("juan@email.com", "password123");

        assertNotNull(loggedUser);
        assertEquals("Juan", loggedUser.getName());
    }

    @Test
    @DisplayName("Login fallido - contraseña incorrecta")
    void testLoginWrongPassword() {
        userService.registerUser("Juan", "juan@email.com", "password123");

        User loggedUser = userService.login("juan@email.com", "wrongpass");

        assertNull(loggedUser);
    }
}
