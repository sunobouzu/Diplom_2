package api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.OrderData;

import static io.restassured.RestAssured.given;

public class OrderApi extends RestApi {
    private static final String CREATE_ORDER_URL = "/api/orders";
    private static final String INGREDIENTS_URL = "/api/ingredients";

    private static final String GET_ORDERS_URL = "/api/orders";

    @Step("Создание заказа с авторизацией")
    public ValidatableResponse createOrder(String accessToken, OrderData orderData) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", accessToken)
                .body(orderData)
                .post(CREATE_ORDER_URL)
                .then();
    }

    @Step("Создание заказа без авторизации")
    public ValidatableResponse createOrderWithoutAuth(OrderData orderData) {
        return given()
                .spec(requestSpecification())
                .body(orderData)
                .post(CREATE_ORDER_URL)
                .then();
    }

    @Step("Получение списка ингредиентов")
    public ValidatableResponse getListOfIngredients() {
        return given()
                .spec(requestSpecification())
                .when()
                .get(INGREDIENTS_URL)
                .then().log().all();
    }

    @Step("Получение заказов пользователя")
    public ValidatableResponse getOrders(String accessToken) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", accessToken)
                .get(GET_ORDERS_URL)
                .then().log().all();
    }
}
