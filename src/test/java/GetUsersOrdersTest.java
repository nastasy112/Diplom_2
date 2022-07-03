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

public class GetUsersOrdersTest {
    private OrderClient orderClient;
    private User user;
    private UserClient userClient;
    private String accessToken;
    private Ingredients ingredients;

    @Before
    public void setUp() {
        user = User.getRandom();
        userClient = new UserClient();
        orderClient = new OrderClient();
        UserCredentials creds = UserCredentials.from(user);
        // Создание пользователя
        userClient.create(user);
        // Логин
        accessToken = userClient.login(creds).then().extract().path("accessToken");
        // Получение ингредиентов
        ingredients = Ingredients.getIngredients();
        orderClient.create(ingredients, accessToken);
    }

    @After
    public void deleteUser(){
        if(accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    //авторизованный пользователь
    @Test
    @DisplayName("Check status code and body of /orders")
    @Description("Basic test for get /orders endpoint")
    public void getUsersOrderAndCheckResponse(){
        orderClient = new OrderClient();
        Response response = orderClient.get(accessToken);
        response.then().log().all()
                .statusCode(200)
                .assertThat().body("success", equalTo(true))
                .assertThat().body("total", notNullValue())
                .assertThat().body("totalToday", notNullValue())
                .assertThat().body("orders", notNullValue())
                .assertThat().body("orders.ingredients", notNullValue())
                .assertThat().body("orders._id", notNullValue())
                .assertThat().body("orders.status", notNullValue())
                .assertThat().body("orders.number", notNullValue())
                .assertThat().body("orders.ingredients", notNullValue())
                .assertThat().body("orders.createdAt", notNullValue())
                .assertThat().body("orders.updatedAt", notNullValue());
    }

    //неавторизованный пользователь
    @Test
    @DisplayName("Check 401 status code and body of /orders")
    @Description("Get user's order without authorization for /orders endpoint")
    public void getUsersOrderWithoutAuthAndCheckError(){
        orderClient = new OrderClient();
        Response response = orderClient.getWithoutAuth();
        response.then().log().all()
                .statusCode(401)
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("You should be authorised"));
    }
}
