package POJOClasses.Homework;

import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Homework {
    public static void main(String[] args) {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

        Response response = given()
                .when()
                .get("/users")
                .then()
                .log().body()
                .extract().response();

        Location.User[] users = response.as(Location.User[].class, ObjectMapperType.GSON);


        Location.User chelsey = null;
        for (Location.User user : users) {
            if (user.getCompany().getName().equals("Keebler LLC")) {
                chelsey = user;
                break;
            }
        }

        if (chelsey != null) {
            Location.Address address = chelsey.getAddress();
            System.out.println("Chelsey Dietrich's details:");
            System.out.println("Email: " + chelsey.getEmail().equals("Lucio_Hettinger@annie.ca"));
            System.out.println("Street: " + address.getStreet().equals("Skiles Walks"));
            System.out.println("Phone: " + chelsey.getPhone().equals("(254)954-1289"));
            System.out.println("Company: " + chelsey.getCompany().getName().equals("Keebler LLC"));
        } else {
            System.out.println("Chelsey Dietrich not found.");
        }
    }
}
