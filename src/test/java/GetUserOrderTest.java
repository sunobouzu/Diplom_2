import api.OrderApi;
import api.UserApi;
import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import model.UserData;
import model.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.hamcrest.Matchers;


public class GetUserOrderTest {
    private OrderApi orderApi;
    private UserApi userApi;
    private String email;
    private String password;
    private String name;
    private String accessToken;


    @Before
    public void setUp() {
        orderApi = new OrderApi();
        userApi = new UserApi();
        email = UserGenerator.generateUniqueEmail();
        password = UserGenerator.generateRandomPassword();
        name = UserGenerator.generateRandomName();

        userApi.registerUser(new UserData(email, password, name)).statusCode(200);
        accessToken = userApi.loginUser(email, password).statusCode(200).extract().jsonPath().getString("accessToken");
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
    @Description("Получение заказов авторизованного пользователя")
    public void testGetOrdersAuthorizedUser() {
        ValidatableResponse response = orderApi.getOrders(accessToken);
        response.statusCode(200)
                .body("success", Matchers.is(true))
                .body("orders", Matchers.notNullValue());
    }

    @Test
    @Description("Получение заказов неавторизованного пользователя")
    public void testGetOrdersUnauthorizedUser() {
        ValidatableResponse response = orderApi.getOrders("");
        response.statusCode(401)
                .body("success", Matchers.is(false))
                .body("message", Matchers.equalTo("You should be authorised"));
    }
}
