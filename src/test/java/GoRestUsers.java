import POJOClasses.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class GoRestUsers {
    public String randomName(){
        return RandomStringUtils.randomAlphabetic(10);
    }
    public String randomEmail(){
        return RandomStringUtils.randomAlphanumeric(7)+"@techno.com";
    }

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;
    @BeforeClass
    public void setUp(){
        baseURI = "https://gorest.co.in/public/v2/users";
        requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization","Bearer e2fed85f49be5538909f80686d43aeac7352d0cb14df75206cdc9a8907163473")
                .setContentType(ContentType.JSON)
                .build();
        responseSpecification = new ResponseSpecBuilder()
                .log(LogDetail.BODY)
                .expectContentType(ContentType.JSON)
                .build();
    }

    @Test
    void getUsersList(){
        given()
                .when()
                .get() // Since the entire url is out baseURI we don't need to use anything in request method
                .then()
                .log().body()
                .statusCode(200)
                .spec(responseSpecification)
                .body("",hasSize(10));
    }
//    @Test
//    void createNewUser(){
//        given()
//                .header("Authorization","Bearer e2fed85f49be5538909f80686d43aeac7352d0cb14df75206cdc9a8907163473")
//                .body("{\n" +
//                        "    \"name\":\""+randomName()+"\", \n" +
//                        "    \"gender\":\"male\", \n" +
//                        "    \"email\":\""+randomEmail()+"\", \n" +
//                        "    \"status\":\"active\"\n" +
//                        "}")
//                .contentType(ContentType.JSON)
//                .when()
//                .post()
//                .then()
//                .statusCode(201)
//                .spec(responseSpecification);
//
//    }
    @Test
    void createNewUserWithMaps(){
        Map<String ,String> user = new HashMap<>();
        user.put("name",randomName());
        user.put("gender","male");
        user.put("email",randomEmail());
        user.put("status","active");
        given()
                .spec(requestSpecification)
                .body(user)
                .when()
                .post()
                .then()
//                .statusCode(201)
                .spec(responseSpecification)
                .body("email",equalTo(user.get("email")))
                .body("name",equalTo(user.get("name")));
    }
    User user;
    User userFromResponse;
    @Test
    void createNewUserWithObject(){
     user = new User(randomName(),randomEmail(),"female","active");
//        User user = new User();
//        user.setName(randomName());
//        user.setEmail(randomEmail());
//        user.setGender("female");
//        user.setStatus("active");
      userFromResponse = given()
                .spec(requestSpecification)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(201)
                .spec(responseSpecification)
                .body("email",equalTo(user.getEmail()))
                .body("name",equalTo(user.getName()))
                .extract().as(User.class);
    }

    @Test(dependsOnMethods = "createNewUserWithObject")
    void CreateNewUserNegative(){
        User userNegative = new User(randomName(), user.getEmail(), "female", "active");

        given()
                .spec(requestSpecification)
                .body(userNegative)
                .when()
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(422)
                .body("[0].message",equalTo("has already been taken"));
    }
    @Test(dependsOnMethods = "createNewUserWithObject")
    void getUserById(){
        given()
                .pathParam("userId",userFromResponse.getId())
                .spec(requestSpecification)
                .when()
                .get("/{userId}")
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .body("id",equalTo(userFromResponse.getId()))
                .body("name",equalTo(userFromResponse.getName()))
                .body("email",equalTo(userFromResponse.getEmail()));


    }

    @Test(dependsOnMethods = "createNewUserWithObject")
    void updateUser(){
        User updatedUser = new User(randomName(),randomEmail(),"male","active");

//        userFromResponse.setEmail(randomEmail());
//        userFromResponse.setName(randomName());

        given()
                .spec(requestSpecification)
                .pathParam("userId",userFromResponse.getId())
                .body(updatedUser)
                .when()
                .put("/{userId}")
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .body("id",equalTo(userFromResponse.getId()))
                .body("name",equalTo(updatedUser.getName()))
                .body("email",equalTo(updatedUser.getEmail()));
    }
    @Test(dependsOnMethods = "createNewUserWithObject")
    void deleteUser(){
        given()
                .spec(requestSpecification)
                .pathParam("userId",userFromResponse.getId())
                .when()
                .delete("/{userId}")
                .then()
                .statusCode(204);
    }
    @Test(dependsOnMethods = {"createNewUserWithObject","deleteUser"})
    void deleteUserNegative(){
        given()
                .spec(requestSpecification)
                .pathParam("userId",userFromResponse.getId())
                .when()
                .delete("/{userId}")
                .then()
                .statusCode(404);
    }
    @Test(dependsOnMethods = "createNewUserWithObject")
    void getUserByIdNegativeTest(){
        given()
                .pathParam("userId",userFromResponse.getId())
                .spec(requestSpecification)
                .when()
                .get("/{userId}")
                .then()
                .spec(responseSpecification)
                .statusCode(404);
    }

}
