import api.UserApi;
import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import model.UserData;
import model.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UpdateUserTest {
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

        ValidatableResponse registerResponse = userApi.registerUser(new UserData(email, password, name));
        assertEquals(200, registerResponse.extract().statusCode());
        accessToken = registerResponse.extract().jsonPath().getString("accessToken");

        ValidatableResponse loginResponse = userApi.loginUser(email, password);
        assertEquals(200, loginResponse.extract().statusCode());
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
    @Description("Обновление поля email пользователя с авторизацией")
    public void testUpdateUserEmailWithAuthorization() {
        String updatedEmail = UserGenerator.generateUniqueEmail();
        ValidatableResponse updateResponse = userApi.updateUserData(accessToken, new UserData(updatedEmail, null, null));

        assertEquals(200, updateResponse.extract().statusCode());
        assertTrue(updateResponse.extract().jsonPath().getBoolean("success"));

        assertEquals(updatedEmail.toLowerCase(),
                updateResponse.extract().jsonPath().getString("user.email").toLowerCase());
        assertEquals(name, updateResponse.extract().jsonPath().getString("user.name"));
    }

    @Test
    @Description("Обновление поля name пользователя с авторизацией")
    public void testUpdateUserNameWithAuthorization() {
        String updatedName = UserGenerator.generateRandomName();
        ValidatableResponse updateResponse = userApi.updateUserData(accessToken, new UserData(null, null, updatedName)); // Обновляем только имя

        assertEquals(200, updateResponse.extract().statusCode());
        assertTrue(updateResponse.extract().jsonPath().getBoolean("success"));
        assertEquals(updatedName, updateResponse.extract().jsonPath().getString("user.name"));
    }

    @Test
    @Description("Ошибка при обновлении поля email пользователя без авторизации")
    public void testUpdateUserEmailWithoutAuthorization() {
        String updatedEmail = UserGenerator.generateUniqueEmail();
        ValidatableResponse response = userApi.updateUserData("", new UserData(updatedEmail, null, null));

        assertEquals(401, response.extract().statusCode());
        assertFalse(response.extract().jsonPath().getBoolean("success"));
        assertEquals("You should be authorised", response.extract().jsonPath().getString("message"));
    }

    @Test
    @Description("Ошибка при обновлении поля name пользователя без авторизации")
    public void testUpdateUserNameWithoutAuthorization() {
        String updatedName = UserGenerator.generateRandomName();
        ValidatableResponse response = userApi.updateUserData("", new UserData(null, null, updatedName));

        assertEquals(401, response.extract().statusCode());
        assertFalse(response.extract().jsonPath().getBoolean("success"));
        assertEquals("You should be authorised", response.extract().jsonPath().getString("message"));
    }
}
