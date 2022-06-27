import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserCredentials;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

//Логин пользователя:
public class LoginUserTest {
    private User user;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void createUser() {
        user = User.getRandom();
        userClient = new UserClient();
        userClient.create(user).then().extract().path("accessToken");
    }

    @After
    public void deleteUser(){
        if(accessToken != null) {
            userClient.delete(user, accessToken);
        }
    }

    //логин под существующим пользователем
    @Test
    @DisplayName("Check status code and body of /login")
    @Description("Basic test for user /login endpoint")
    public void loginUserAndCheckResponse() {
        UserCredentials creds = UserCredentials.from(user);

        Response response = userClient.login(creds);
        accessToken = response.then().log().all()
                .statusCode(200)
                .assertThat().body("success", equalTo(true))
                .assertThat().body("user", notNullValue())
                .assertThat().body("user.name", equalTo(user.getName()))
                .assertThat().body("user.email", equalTo(user.getEmail().toLowerCase(Locale.ROOT)))
                .assertThat().body("accessToken", notNullValue())
                .assertThat().body("refreshToken", notNullValue())
                .extract()
                .path("accessToken");
    }

        //логин с неверным логином и паролем.
        @Test
        @DisplayName("Check 401 status code and error message of /login")
        @Description("Login with invalid email for /login endpoint")
        public void loginUserWithInvalidEmailAndCheckError() {
            UserCredentials creds = UserCredentials.from(user);
            creds.setEmail(RandomStringUtils.randomAlphanumeric(8)+"@yandex.ru");
            Response response = userClient.login(creds);
            response.then().log().all()
                    .statusCode(401)
                    .assertThat().body("message", notNullValue())
                    .assertThat().body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Check 401 status code and error message of /login")
    @Description("Login with invalid password for /login endpoint")
    public void loginUserWithInvalidPasswordAndCheckError() {
        UserCredentials creds = UserCredentials.from(user);
        creds.setPassword(RandomStringUtils.randomAlphanumeric(8));
        Response response = userClient.login(creds);
        response.then().log().all()
                .statusCode(401)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Check 401 status code and error message of /login")
    @Description("Login without email for /login endpoint")
    public void loginUserWithoutEmailAndCheckError() {
        UserCredentials creds = UserCredentials.from(user);
        creds.setEmail(null);
        Response response = userClient.login(creds);
        response.then().log().all()
                .statusCode(401)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Check 401 status code and error message of /login")
    @Description("Login without password for /login endpoint")
    public void loginUserWithoutPasswordAndCheckError() {
        UserCredentials creds = UserCredentials.from(user);
        creds.setPassword(null);
        Response response = userClient.login(creds);
        response.then().log().all()
                .statusCode(401)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("email or password are incorrect"));
    }
}
