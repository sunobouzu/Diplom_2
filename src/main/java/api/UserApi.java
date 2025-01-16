package api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.UserData;

import static io.restassured.RestAssured.given;

public class UserApi extends RestApi {
    private static final String LOGIN_URL = "/api/auth/login";
    private static final String REGISTER_URL = "/api/auth/register";
    private static final String USER_DATA_URL = "/api/auth/user";
    private static final String DELETE_URL = "/api/auth/user";

    @Step("Регистрация пользователя")
    public ValidatableResponse registerUser(UserData user) {
        return given()
                .spec(requestSpecification())
                .body(user)
                .when()
                .post(REGISTER_URL)
                .then();
    }

    @Step("Логин пользователя")
    public ValidatableResponse loginUser(String email, String password) {
        return given()
                .spec(requestSpecification())
                .body(new UserData(email, password, null))
                .when()
                .post(LOGIN_URL)
                .then();
    }

    @Step("Удаление пользователя по токену")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE_URL)
                .then()
                .log().all();
    }

    @Step("Обновление данных пользователя")
    public ValidatableResponse updateUserData(String accessToken, UserData user) {
        return given()
                .spec(requestSpecification())
                .header("Authorization",  accessToken)
                .body(user)
                .when()
                .patch(USER_DATA_URL)
                .then();
    }
}
