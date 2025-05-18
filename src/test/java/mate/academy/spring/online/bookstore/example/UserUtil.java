package mate.academy.spring.online.bookstore.example;

import mate.academy.spring.online.bookstore.dto.user.UserLoginRequestDto;
import mate.academy.spring.online.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.spring.online.bookstore.dto.user.UserResponseDto;
import mate.academy.spring.online.bookstore.model.Role;
import mate.academy.spring.online.bookstore.model.User;

import java.util.Set;

public class UserUtil {
    public static User getUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user111@example.com");
        user.setPassword("Password111");
        user.setFirstName("User111");
        user.setLastName("User111");
        user.setShippingAddress("145 Main St, City, Country");
        user.setRoles(Set.of(getRoleUser()));

        return user;
    }

    public static User getUserWithHashedPassword() {
        User user = new User();
        user.setId(1L);
        user.setEmail("juser111@example.com");
        user.setPassword("$2a$10$WlHqEbjYKo0FKQUYC1ppUOGaDj7dRW6USZ7gIzt9UXkajr4X2wXC2");
        user.setFirstName("User111");
        user.setLastName("User111");
        user.setShippingAddress("145 Main St, City, Country");
        user.setRoles(Set.of(getRoleUser()));

        return user;
    }

    public static Role getRoleUser() {
        Role role = new Role();
        role.setId(1L);
        role.setRole(Role.RoleName.ROLE_USER);
        return role;
    }

    public static Role getRoleAdmin() {
        Role role = new Role();
        role.setId(1L);
        role.setRole(Role.RoleName.ROLE_USER);
        return role;
    }

    public static User getAdmin() {
        User user = new User();
        user.setId(2L);
        user.setEmail("admin@example.com");
        user.setPassword("Password999");
        user.setFirstName("Admin");
        user.setLastName("Admin");
        user.setShippingAddress("admin shipping address");
        user.setRoles(Set.of(getRoleAdmin()));

        return user;
    }

    public static User getUserBeforeSaveIntoDb() {
        User user = new User();
        user.setEmail("testUser@ukr.net");
        user.setPassword("12345678");
        user.setPassword("12345678");
        user.setFirstName("TestUserName1");
        user.setLastName("TestLastName1");
        user.setShippingAddress("TesShippingAddress 1");
        user.setRoles(Set.of(getRoleUser()));

        return user;
    }

    public static User getUserAfterSaveIntoDb() {
        User user = new User();
        user.setId(1L);
        user.setEmail("testUser@ukr.net");
        user.setPassword("12345678");
        user.setPassword("12345678");
        user.setFirstName("TestUserName1");
        user.setLastName("TestLastName1");
        user.setShippingAddress("TesShippingAddress 1");
        user.setRoles(Set.of(getRoleUser()));

        return user;
    }

    public static UserRegistrationRequestDto getUserRequestDto() {
        UserRegistrationRequestDto user = new UserRegistrationRequestDto();
        user.setEmail("testUser@ukr.net");
        user.setPassword("12345678");
        user.setRepeatPassword("12345678");
        user.setFirstName("TestUserName1");
        user.setLastName("TestLastName1");
        user.setShippingAddress("TesShippingAddress 1");
        return user;
    }

    public static UserResponseDto getUserResponseDto() {
        UserResponseDto user = new UserResponseDto();
        user.setId(3L);
        user.setEmail("testUser@ukr.net");
        user.setFirstName("TestUserName1");
        user.setLastName("TestLastName1");
        user.setShippingAddress("TesShippingAddress 1");
        return user;
    }

    public static UserLoginRequestDto getUserLoginRequestDto() {
        return new UserLoginRequestDto("user111@example.com", "Password111");
    }
}
