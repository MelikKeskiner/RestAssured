import POJOClasses.Location;
import POJOClasses.User;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ZippoAPITest {
    @Test
    void test1() {
        given()

                .when()

                .then();
    }

    @Test
    void statusCodeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210") // Set up request method and url
                .then()
                .log().body() //prints the response body
                .log().status()
                .statusCode(200) // tests if the status code is 200
        ;
    }

    @Test
    void contentTypeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .contentType(ContentType.JSON); // tests if the response is in correct form.
    }

    @Test
    void countryInformationTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("country", equalTo("United States"));
        // tests if the country value is correct
    }

    @Test
    void stateInformationTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("places[0].state", equalTo("California"));
    }

    @Test
    void stateInfoTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("places[0].'state abbreviation'", equalTo("CA"));
    }

    @Test
    void bodyHasItem() {
        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .log().body()
                .body("places.'place name'", hasItem("Büyükdikili Köyü"));
        // When we don't use index it gets all place names from the response and creates an array with them.
    }

    @Test
    void arrayHasSizeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .log().body()
                .body("places.'place name'", hasSize(71)); // tests if the size of list is correct.
    }

    @Test
    void multipleTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("places", hasSize(71))
                .body("places.'place name'", hasItem("Büyükdikili Köyü"))
                .body("country", equalTo("Turkey"));

    }

    //Parameters
    // There are 2 types of parameters
    //    1) Path parameters -> http://api.zippopotam.us/tr/01000 -> They are parts of the url
    //    2) Query parameters -> https://gorest.co.in/public/v1/users?page=3 -> They are separated by a ? mark
    @Test
    void pathParametersTest1() {
        String countryCode = "us";
        String zipCode = "90210";

        given()
                .pathParam("country", countryCode)
                .pathParam("zip", zipCode)
                .when()
                .get("http://api.zippopotam.us/{country}/{zip}")
                .then()
                .statusCode(200);

    }

    @Test
    void pathParameterTest2() {

        for (int i = 90210; i <= 90213; i++) {
            given()
                    .pathParam("Zip", i)
                    .when()
                    .get("http://api.zippopotam.us/us/{Zip}")
                    .then()
                    .log().body()
                    .body("places", hasSize(1));
        }
    }

    @Test
    void queryParametersTest1() {
        given()
                .param("page", 2)
                .log().uri()
                .when()
                .get("https://gorest.co.in/public/v1/users")
                .then()
                .log().body()
                .statusCode(200);
    }


    @Test
    void queryParametersTest2() {
        for (int i = 1; i < 10; i++) {

            given()
                    .param("page", i)
                    .log().uri()
                    .when()
                    .get("https://gorest.co.in/public/v1/users")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("meta.pagination.page", equalTo(i));
        }
    }

    @DataProvider(name = "pageNumbers")
    public Object[][] pageNumbers() {
        return new Object[][]{
                {1, "users", "v1"},
                {2, "users", "v1"},
                {3, "users", "v1"},
                {4, "users", "v1"},
                {5, "users", "v1"},
                {6, "users", "v1"},
                {7, "users", "v1"},
                {8, "users", "v1"},
                {9, "users", "v1"},
                {10, "users", "v1"}
        };
    }

    @Test(dataProvider = "pageNumbers")
    @Parameters("pageNumber")
    public void testPageNumber(int pageNumber, String apiName, String version) {
        given()
                .param("page", pageNumber)
                .pathParam("apiName", apiName)
                .pathParam("version", version)
                .log().uri()
                .when()
                .get("https://gorest.co.in/public/{version}/{apiName}")
                .then()
                .log().body()
                .statusCode(200)
                .body("meta.pagination.page", equalTo(pageNumber));
    }

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;

    @BeforeClass
    public void setUp() {
        baseURI = "https://gorest.co.in/public/";


        requestSpecification = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .addPathParam("apiName", "users")
                .addPathParam("version", "v1")
                .addParam("page", 3)
                .build();
        responseSpecification = new ResponseSpecBuilder()
                .log(LogDetail.BODY)
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectBody("meta.pagination.page", equalTo(3))
                .build();
    }

    @Test
    void baseURITest() {
        given()
                .param("page", 3)
                .log().uri()
                .when()
                .get()
                .then()
                .log().body()
                .statusCode(200);
    }

    @Test
    void specificationTest() {
        given()
                .spec(requestSpecification)
                .when()
                .get("{version}/{apiName}")
                .then()
                .spec(responseSpecification);
    }

    @Test
    void extractStringTest() {
        String placeName = given()
                .pathParam("country", "us")
                .pathParam("zip", "90210")
                .when()
                .get("http://api.zippopotam.us/{country}/{zip}")
                .then()
                .log().body()
                .extract().path("places[0].'place name'");
        // With extract method our request returns a value(not an object)
        //extract returns only one part of the response(the part that we specified in the path method)
        // or list of that value
        // we can assign it to a variable and use it however we want


        System.out.println("placeName = " + placeName);
    }
    @Test
    void extractIntTest(){
       int pageNumber = given()
                .spec(requestSpecification)
                .when()
                .get("{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().path("meta.pagination.page");
        System.out.println("pageNumber = " + pageNumber);
        Assert.assertEquals(pageNumber, 3);
    }
    @Test
    void extractListTest1(){
       List<String> name =  given()
                .spec(requestSpecification)
                .when()
                .get("{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().path("data.name");

        System.out.println("name = " + name.size());
        System.out.println("name.get(2) = " + name.get(2));
        System.out.println("name.contains(\"Ravi\") = " + name.contains("Ravi"));

    }

    @Test
    void extractEmail(){
       List<String > emails = given()
                .spec(requestSpecification)
                .when()
                .get("{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().path("data.email");
        System.out.println("emails.size() = " + emails.size());
        System.out.println("emails = " + emails);
        Assert.assertTrue(emails.contains("patel_atreyee_jr@gottlieb.test"));
    }
    @Test
    void extractNext(){
        String next = given()
                .spec(requestSpecification)
                .when()
                .get("{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().path("meta.pagination.links.next");
        System.out.println("next = " + next);
        Assert.assertTrue(next.contains("page=4"));
    }
    @Test
    void extractResponse(){
      Response response =  given()
                .spec(requestSpecification)
                .when()
                .get("{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().response();
        // The entire request returns the entire response as a response object
        // By using this object we are able to extract multiple values with one request.
        int page = response.path("meta.pagination.page");
        System.out.println("page = " + page);

        String currentURL = response.path("meta.pagination.links.current");
        System.out.println("currentURL = " + currentURL);

        String name = response.path("data[1].name");
        System.out.println("name = " + name);

        List<String > emailList = response.path("data.email");
        System.out.println("emailList = " + emailList);
    }

    //extract.path() => we can extract only one value (String int ...) or list of that value (List<String>)
    // String name = extract.path(data[0].name);
    // List<String> nameList = extract.path(data.name)

    // extract.as => We can extract the entire response body as POJO classes.But we cannot extract one part of the body separately
    // We need to create a class structure for the entire body
    // extract.as(Location.class)
    // extract.as(Place.class)
    @Test
    void extractJsonPOJO(){
        Location location = given()
                .pathParam("countryCode", "us")
                .pathParam("zipCode", "90210")
                .when()
                .get("http://api.zippopotam.us/{countryCode}/{zipCode}")
                .then()
                .log().body()
                .extract().as(Location.class);

        System.out.println("location.getPostCode() = " + location.getPostCode());
        System.out.println("location.getCountry() = " + location.getCountry());
        System.out.println("location.getPlaces().get(0).getPlaceName() = " + location.getPlaces().get(0).getPlaceName());
        System.out.println("location.getPlaces().get(0).getState() = " + location.getPlaces().get(0).getState());
    }

    @Test
    void extractWithJsonPath1(){
        User user = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().jsonPath().getObject("data[0]", User.class);

        System.out.println("user.getId() = " + user.getId());
        System.out.println("user.getName() = " + user.getName());
        System.out.println("user.getEmail() = " + user.getEmail());

    }
    @Test
    void extractWithJsonPath2(){
       List<User> userList = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().jsonPath().getList("data",User.class);
        System.out.println("userList.size() = " + userList.size());
        System.out.println("userList.get(2).getName() = " + userList.get(2).getName());
        System.out.println("userList.get(8).getId() = " + userList.get(8).getId());
    }
    @Test
    void extractWithJsonPath3(){
      String name =  given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().jsonPath().getString("data[1].name");
        System.out.println("name = " + name);

    }
    @Test
    void extractWithJsonPath4(){
      Response response =  given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().response();
       int page = response.jsonPath().getInt("meta.pagination.page");
        System.out.println("page = " + page);

       String currentLine = response.jsonPath().getString("meta.pagination.links.current");
        System.out.println("currentLine = " + currentLine);
       User user = response.jsonPath().getObject("data[2]", User.class);
        System.out.println("user = " + user);
    }

}
