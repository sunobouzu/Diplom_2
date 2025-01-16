import api.UserApi;
import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import model.UserData;
import model.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CreateUserTest {
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
    @Description("Тест создания уникального пользователя")
    public void testRegisterUniqueUser() {
        UserData user = new UserData(email, password, name);
        ValidatableResponse response = userApi.registerUser(user);
        assertEquals(200, response.extract().statusCode());
        assertTrue("Регистрация не удалась", response.extract().jsonPath().getBoolean("success"));
        accessToken = response.extract().jsonPath().getString("accessToken");
    }

    @Test
    @Description("Тест создания пользователя, который уже зарегистрирован")
    public void testRegisterExistingUser() {
        UserData user = new UserData(email, password, name);
        userApi.registerUser(user);
        ValidatableResponse response = userApi.registerUser(user);
        assertEquals(403, response.extract().statusCode());
        assertEquals("User already exists", response.extract().jsonPath().getString("message"));
    }

    @Test
    @Description("Тест создания пользователя без заполнения обязательного поля Email")
    public void testRegisterUserWithoutRequiredFieldEmail() {
        UserData user = new UserData("", password, name);
        ValidatableResponse response = userApi.registerUser(user);
        assertEquals(403, response.extract().statusCode());
        assertEquals("Email, password and name are required fields", response.extract().jsonPath().getString("message"));
    }

    @Test
    @Description("Тест создания пользователя без заполнения обязательного поля Password")
    public void testRegisterUserWithoutRequiredFieldPassword() {
        UserData user = new UserData(email, "", name);
        ValidatableResponse response = userApi.registerUser(user);
        assertEquals(403, response.extract().statusCode());
        assertEquals("Email, password and name are required fields", response.extract().jsonPath().getString("message"));
    }
}
