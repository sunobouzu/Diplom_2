import api.OrderApi;
import api.UserApi;
import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import model.OrderData;
import model.OrderIdGenerator;
import model.UserData;
import model.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OrderTest {
    private OrderApi orderApi;
    private UserApi userApi;
    private String email;
    private String password;
    private String name;
    private String accessToken;
    private List<String> ingredients;

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
    @Description("Создание заказа с авторизацией и ингредиентами")
    public void testCreateOrderWithAuthorization() {

        OrderIdGenerator ingredientGenerator = new OrderIdGenerator();
        ingredients = ingredientGenerator.generateIngredients(orderApi.getListOfIngredients());

        OrderData orderData = new OrderData(ingredients);
        ValidatableResponse orderResponse = orderApi.createOrder(accessToken, orderData);
        orderResponse.statusCode(200);
        assertTrue(orderResponse.extract().jsonPath().getBoolean("success"));
        assertNotNull(orderResponse.extract().jsonPath().getString("order._id"));
    }



    @Test
    @Description("Создание заказа с авторизацией без ингредиентов")
    public void testCreateOrderWithAuthorizationWithoutIngredients() {
        OrderData orderData = new OrderData(new ArrayList<>());
        ValidatableResponse orderResponse = orderApi.createOrder(accessToken, orderData);
        orderResponse.statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Description("Создание заказа с авторизацией и некорректным ID ингредиента")
    public void testCreateOrderWithAuthorizationAndInvalidIngredientId() {
        OrderIdGenerator ingredientGenerator = new OrderIdGenerator();
        List<String> ingredients = ingredientGenerator.generateIngredients(orderApi.getListOfIngredients());
        String invalidIngredientId = ingredients.get(0) + "1";
        List<String> invalidIngredients = new ArrayList<>(ingredients);
        invalidIngredients.add(invalidIngredientId);
        OrderData orderData = new OrderData(invalidIngredients);
        ValidatableResponse orderResponse = orderApi.createOrder(accessToken, orderData);
        orderResponse.statusCode(500);
    }
}
