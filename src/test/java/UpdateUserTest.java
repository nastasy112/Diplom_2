import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserCredentials;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UpdateUserTest {
    private User user;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void createUserAndLogin() {
        user = User.getRandom();
        userClient = new UserClient();
        UserCredentials creds = UserCredentials.from(user);
        userClient.create(user);
        accessToken = userClient.login(creds).then().extract().path("accessToken");
    }

    @After
    public void deleteUser(){
        if(accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    // с авторизацией: изменение любого поля
    @Test
    @DisplayName("Check status code and body of /user")
    @Description("Basic test for update user /user endpoint")
    public void UpdateUserAndCheckResponse() {
        User user = User.getRandom();
        Response response = userClient.patch(user, accessToken);
        response.then().log().all()
                .statusCode(200)
                .assertThat().body("success", equalTo(true))
                .assertThat().body("user", notNullValue())
                .assertThat().body("user.name", equalTo(user.getName()))
                .assertThat().body("user.email", equalTo(user.getEmail().toLowerCase(Locale.ROOT)));
    }

    // без авторизации: изменение любого поля, ошибка
    @Test
    @DisplayName("Check 401 status code and body of /user")
    @Description("Update user without authorization for /user endpoint")
    public void UpdateUserWithoutAuthAndCheckError() {
        User user = User.getRandom();
        Response response = userClient.patchWithoutAuth(user);
        response.then().log().all()
                .statusCode(401)
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("You should be authorised"));
    }
}
