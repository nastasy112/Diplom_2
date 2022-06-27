package clients;

import io.restassured.response.Response;
import order.Ingredients;

public class OrderClient extends RestAssuredClient {
    private final String INGREDIENTS = "/ingredients";
    private final String ORDERS = "/orders";

    public Response getIngredients() {
        return reqSpec
                .when()
                .get(INGREDIENTS);
    }

    public Response create(Ingredients ingredients, String accessToken) {
        return reqSpec
                .header("Authorization", accessToken)
                .body(ingredients)
                .when()
                .post(ORDERS);
    }

    public Response createWithoutAuth(Ingredients ingredients) {
        return reqSpec
                .body(ingredients)
                .when()
                .post(ORDERS);
    }

    public Response get(String accessToken) {
        return reqSpec
                .header("Authorization", accessToken)
                .when()
                .get(ORDERS);
    }

    public Response getWithoutAuth() {
        return reqSpec
                .when()
                .get(ORDERS);
    }
}
