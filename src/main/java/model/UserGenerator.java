package model;

import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;

public class UserGenerator {

    private static final String EMAIL_DOMAIN = "@yandex.ru";

    @Step("Генерация уникального email")
    public static String generateUniqueEmail() {
        String randomEmailPart = RandomStringUtils.randomAlphabetic(8);
        return randomEmailPart + EMAIL_DOMAIN;
    }

    @Step("Генерация рандомного пароля")
    public static String generateRandomPassword() {
        return RandomStringUtils.randomAlphabetic(8);
    }

    @Step("Генерация рандомного имени")
    public static String generateRandomName() {
        return RandomStringUtils.randomAlphabetic(8);
    }
}



