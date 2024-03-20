import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class APIKey {
    /**
     * Use https://www.weatherapi.com/docs/ as a reference.
     * First, You need to signup to weatherapi.com, and then you can find your API key under your account
     * after that, you can use Java to request: http://api.weatherapi.com/v1/current.json?key=[YOUR-APIKEY]&q=Indianapolis&aqi=no
     * Parse the json and print the current temperature in F and C.
     **/

    @Test
    void weatherAPI() {
        Response response = given()
                .param("key", "eb1e1163d8ec439982251544241503")
                .param("q", "'Garden Grove'")
                .log().uri()
                .when()
                .get("http://api.weatherapi.com/v1/current.json")
                .then()
                .log().body()
                .extract().response();

        double temp_c = response.jsonPath().getDouble("current.temp_c");
        double temp_f = response.jsonPath().getDouble("current.temp_f");
        System.out.println("temp_f = " + temp_f);
        System.out.println("temp_c = " + temp_c);

    }

}
