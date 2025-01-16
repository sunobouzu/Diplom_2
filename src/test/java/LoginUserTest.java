import api.UserApi;
import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import model.UserData;
import model.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginUserTest {
    private UserApi userApi;
    private String email;
    private String password;
    private String name;
    private String accessToken;

    @Before
    public void setUp() {
        userApi = new UserApi();
        email = UserGenerator.generateUniqueEmail();
        password = UserGenerator.generateRandomPassword();
        name = UserGenerator.generateRandomName();

        UserData user = new UserData(email, password, name);
        ValidatableResponse registerResponse = userApi.registerUser(user);
        assertEquals(200, registerResponse.extract().statusCode());
        accessToken = registerResponse.extract().jsonPath().getString("accessToken");
    }

    @After
    public void cleanUp() {
        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                userApi.deleteUser(accessToken).statusCode(202);
                System.out.println("Пользователь успешно удалён.");
            } catch (AssertionError e) {
                System.err.println("Ошибка при удалении пользователя: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Непредвиденная ошибка при удалении пользователя: " + e.getMessage());
            }
        }
    }

    @Test
    @Description("Логин существующего пользователя")
    public void testLoginExistingUser() {
        ValidatableResponse loginResponse = userApi.loginUser(email, password);
        assertEquals(200, loginResponse.extract().statusCode());
        assertNotNull(loginResponse.extract().jsonPath().getString("accessToken"));
    }

    @Test
    @Description("Логин с неверным логином и паролем")
    public void testLoginWithInvalidCredentials() {
        String invalidPassword = password + "1";
        ValidatableResponse response = userApi.loginUser(email, invalidPassword);
        assertEquals(401, response.extract().statusCode());
        assertEquals("email or password are incorrect", response.extract().jsonPath().getString("message"));
    }
}
