package model;

import io.restassured.response.ValidatableResponse;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class OrderIdGenerator {

    public List<String> generateIngredients(ValidatableResponse ingredientsResponse) {
        ingredientsResponse.statusCode(200);


        List<String> allIngredients = ingredientsResponse.extract().jsonPath().getList("data._id", String.class);


        if (allIngredients == null || allIngredients.isEmpty()) {
            throw new RuntimeException("Список ингредиентов пуст! Проверьте API ответ.");
        }


        Random random = new Random();
        int numIngredients = 3;
        return random.ints(0, allIngredients.size())
                .distinct()
                .limit(numIngredients)
                .mapToObj(allIngredients::get)
                .collect(Collectors.toList());
    }
}
