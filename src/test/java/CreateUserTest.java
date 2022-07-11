import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;

import java.util.Locale;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;

public class CreateUserTest {
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void deleteUser(){
        if(accessToken != null){
            userClient.delete(accessToken);
        }
    }

    //создать уникального пользователя;
    @Test
    @DisplayName("Check status code and body of /auth/register")
    @Description("Basic test for /auth/register endpoint")
    public void createNewUserAndCheckResponse(){
        user = User.getRandom();

        Response response = userClient.create(user);
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

    //создать пользователя, который уже зарегистрирован;
    @Test
    @DisplayName("Check 403 status code and error message of /auth/register")
    @Description("User is already exist for /auth/register endpoint")
    public void userAlreadyExistsAndCheckError(){
        User user_prescript = User.getRandom();
        userClient.create(user_prescript);

        user = user_prescript;

        Response response = userClient.create(user);
        response.then().log().all()
                .statusCode(403)
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message ", equalTo( "User already exists"));
    }

    //создать пользователя и не заполнить одно из обязательных полей.
    @Test
    @DisplayName("Check 403 status code and error message of /auth/register")
    @Description("User can't created without email for /auth/register endpoint")
    public void createNewUserWithoutRequiredFieldEmailAndCheckError(){
        user = new User(null, UUID.randomUUID().toString(), UUID.randomUUID().toString());

        Response response = userClient.create(user);
        response.then().log().all()
                .statusCode(403)
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message ", equalTo( "Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Check 403 status code and error message of /auth/register")
    @Description("User can't created without password for /auth/register endpoint")
    public void createNewUserWithoutRequiredFieldPasswordAndCheckError(){
        user = new User(UUID.randomUUID()+"@yandex.ru", null, UUID.randomUUID().toString());

        Response response = userClient.create(user);
        response.then().log().all()
                .statusCode(403)
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message ", equalTo( "Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Check 403 status code and error message of /auth/register")
    @Description("User can't created without name for /auth/register endpoint")
    public void createNewUserWithoutRequiredFieldNameAndCheckError(){
        user = new User(UUID.randomUUID()+"@yandex.ru", UUID.randomUUID().toString(), null);

        Response response = userClient.create(user);
        response.then().log().all()
                .statusCode(403)
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message ", equalTo( "Email, password and name are required fields"));
    }
}
