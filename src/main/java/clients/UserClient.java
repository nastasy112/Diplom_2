package clients;

import io.restassured.response.Response;
import user.User;
import user.UserCredentials;

public class UserClient extends RestAssuredClient{
    private final String AUTH = "/auth";
    private final String CREATE_USER = AUTH + "/register";
    private final String USER = AUTH + "/user";
    private final String LOGIN_USER = AUTH + "/login";

    public Response create(User user) {
        return reqSpec
                .body(user)
                .when()
                .post(CREATE_USER);
    }

    public void delete(User user, String accessToken){
        reqSpecWithoutHeaders
                .header("Authorization", accessToken)
                .when()
                .delete(USER);
    }

    public Response login(UserCredentials creds){
        return reqSpec
                .body(creds)
                .when()
                .post(LOGIN_USER);
    }

    public Response patch(User user, String accessToken){
        return reqSpec
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(USER);
    }

    public Response patchWithoutAuth(User user){
        return reqSpec
                .body(user)
                .when()
                .patch(USER);
    }
}
