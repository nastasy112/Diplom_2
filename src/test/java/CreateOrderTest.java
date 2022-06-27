import clients.OrderClient;
import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import order.Ingredients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserCredentials;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateOrderTest {
    private OrderClient orderClient;
    private User user;
    private UserClient userClient;
    private String accessToken;
    private Ingredients ingredients;

    @Before
    public void createUserAndLoginAndGetIngredients() {
        orderClient = new OrderClient();
        user = User.getRandom();
        userClient = new UserClient();
        UserCredentials creds = UserCredentials.from(user);
        // Создание пользователя
        userClient.create(user);
        // Логин
        accessToken = userClient.login(creds).then().extract().path("accessToken");
        // Получение ингредиентов
        ingredients = Ingredients.getIngredients();
    }

    @After
    public void deleteUser(){
        if(accessToken != null) {
            userClient.delete(user, accessToken);
        }
    }

    //с авторизацией
    //с ингредиентами
    @Test
    @DisplayName("Check status code and body of /orders")
    @Description("Basic test for /orders endpoint")
    public void createNewOrderAndCheckResponse(){
        Response response = orderClient.create(ingredients, accessToken);
        response.then().log().all()
                .statusCode(200)
                .assertThat().body("success", equalTo(true))
                .assertThat().body("name", notNullValue())
                .assertThat().body("order", notNullValue())
                .assertThat().body("order.number", notNullValue());
    }

    //без авторизации
    @Test
    @DisplayName("Check 200 status code and body of /orders")
    @Description("Update user without authorization for /orders endpoint")
    public void createNewOrderWithoutAuthAndCheckResponse(){
        Response response = orderClient.createWithoutAuth(ingredients);
        response.then().log().all()
                .statusCode(200)
                .assertThat().body("success", equalTo(true))
                .assertThat().body("name", notNullValue())
                .assertThat().body("order", notNullValue())
                .assertThat().body("order.number", notNullValue());
    }

    //без ингредиентов
    @Test
    @DisplayName("Check 400 status code and body of /orders")
    @Description("Update user without ingredients for /orders endpoint")
    public void createNewOrderWithoutIngredientsAndCheckError(){
        ingredients = Ingredients.getWithoutIngredients();
        Response response = orderClient.create(ingredients, accessToken);
        response.then().log().all()
                .statusCode(400)
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("Ingredient ids must be provided"));
    }

    //с неверным хешем ингредиентов
    @Test
    @DisplayName("Check 500 status code and body of /orders")
    @Description("Update user with invalid ingredients for /orders endpoint")
    public void createNewOrderWithInvalidIngredientsAndCheckError(){
        ingredients = Ingredients.getWitInvalidIngredients();
        Response response = orderClient.create(ingredients, accessToken);
        response.then().log().all()
                .statusCode(500);
    }
}
