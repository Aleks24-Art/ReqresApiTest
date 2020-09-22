package reqres.api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ApiTest {

    private final static String BASE_URL = "https://reqres.in/api/";

    private final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @Test
    public void testGetAvailablePage_shouldReturnPageOfUsers() {
        given()
                .spec(requestSpec)
                .get("users?page=1")
                .then()
                .statusCode(200)
                .body("page", equalTo(1))
                .body("per_page", equalTo(6))
                .body("data.id[0]", equalTo(1))
                .body("data.id[5]", equalTo(6))
                .body("data.last_name", hasItems(
                        "Weaver",
                        "Holt",
                        "Wong",
                        "Ramos"
                ))
                .body("data.email", hasItems(
                        "george.bluth@reqres.in",
                        "charles.morris@reqres.in",
                        "tracey.ramos@reqres.in"
                ));
    }

    @Test
    public void testGetUnavailablePage_shouldReturnEmpty() {
        given().spec(requestSpec)
                .get("/users?page=999")
                .then()
                .statusCode(200)
                .body("data", empty());
    }

    @Test
    public void testPostCreateUser_shouldReturnSomeUserCreatingInfo() {
        JSONObject userData = new JSONObject();
        userData.put("name", "Billq");
        userData.put("job", "Developer1");

        Response response = given()
                .spec(requestSpec)
                .body(userData)
                .when()
                .post("users")
                .then()
                .statusCode(201).extract().response();

        System.out.println(response.getBody().toString());
        JSONObject createdUser = new JSONObject(response.getBody().print());

        assertNotNull(createdUser.get("id"));
        assertNotNull(createdUser.get("createdAt"));
        assertTrue(Integer.parseInt(createdUser.get("id").toString()) > 0);
    }

    @Test
    public void testPutUpdateUser_shouldReturnSomeUserUpdatingInfo() {
        JSONObject updatedUserData = new JSONObject();
        updatedUserData.put("name", "Bill");
        updatedUserData.put("job", "Senior developer");

        Response response = given()
                .spec(requestSpec)
                .body(updatedUserData)
                .when()
                .put("users/2")
                .then()
                .statusCode(200).extract().response();

        JSONObject updatedUser = new JSONObject(response.getBody().print());

        assertNotNull(updatedUser.get("updatedAt"));
    }

    @Test
    public void testDelete_shouldReturnStatusCode() {
        given()
                .spec(requestSpec)
                .when()
                .delete("users/2")
                .then()
                .statusCode(204);
    }
}
