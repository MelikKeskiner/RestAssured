import POJOClasses.practiceLocation1;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class Practice {
    @Test
    void test1() {
        given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .log().body()
                .statusCode(200);
    }

    @Test
    void test2() {
        practiceLocation1 pr = given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .log().body()
                .statusCode(200)
                .extract().as(practiceLocation1.class);

        System.out.println("pr.getTitle() = " + pr.getTitle());
        System.out.println("pr.getId() = " + pr.getId());
        System.out.println("pr.getUserId() = " + pr.getUserId());
        System.out.println("pr.isCompleted() = " + pr.isCompleted());
    }

    @Test
    void test3() {
        practiceLocation1 pr = given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .log().body()
                .statusCode(200)
                .extract().as(practiceLocation1.class);
        System.out.println("pr.getTitle().contains(\"quis ut nam facilis et officia qui\") = " + pr.getTitle().contains("quis ut nam facilis et officia qui"));
    }

    @Test
    void test4() {
//        given()
//                .when()
//                .get("https://jsonplaceholder.typicode.com/todos/2")
//                .then()
//                .statusCode(200)
//                .contentType(ContentType.JSON)
//                .log().body()
//                .body("completed", equalTo(false));

        boolean completed = given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().path("completed");
        Assert.assertFalse(completed);

        given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(practiceLocation1.class);
        Assert.assertFalse(completed);

    }
}
