package order;

import clients.OrderClient;
import io.restassured.response.ValidatableResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.nextInt;

public class Ingredients {
    static OrderClient orderClient = new OrderClient();
    List<Object> ingredients = new ArrayList<>();

    public Ingredients(List<Object> ingredients){
        this.ingredients = ingredients;
    }

    public static Ingredients getIngredients() {
        ValidatableResponse response = orderClient.getIngredients().then().log().all();

        List<Object> bunIngredients = response.extract().jsonPath().getList("data.findAll{it.type == 'bun'}._id");
        List<Object> mainIngredients = response.extract().jsonPath().getList("data.findAll{it.type == 'main'}._id");
        List<Object> sauceIngredients = response.extract().jsonPath().getList("data.findAll{it.type == 'sauce'}._id");

        List<Object> ingredients = new ArrayList<>();
        ingredients.add(bunIngredients.get(nextInt(0, 1)));
        ingredients.add(mainIngredients.get(nextInt(0,9)));
        ingredients.add(sauceIngredients.get(nextInt(0, 3)));

        return new Ingredients(ingredients);
    }

    public static Ingredients getWithoutIngredients() {
        ArrayList<Object> ingredients = new ArrayList<>();
        return new Ingredients(ingredients);
    }

    public static Ingredients getWitInvalidIngredients() {
        ArrayList<Object> ingredients = new ArrayList<>();
        ingredients.add(UUID.randomUUID());
        return new Ingredients(ingredients);
    }
}
