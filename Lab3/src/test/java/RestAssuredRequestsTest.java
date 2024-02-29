import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestAssuredRequestsTest {
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

    }


    @Test
    public void testStatusCodeAndBodyAndHeader() {
        String requestBody = """
                {
                 "firstname" : "Nazar",
                 "lastname" : "Beshta",
                 "totalprice" : 111,
                 "depositpaid" : true,
                 "bookingdates" : {
                     "checkin" : "2018-01-01",
                     "checkout" : "2019-01-01"
                }
                }""";
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/booking")
                .then()
                .extract().response();
        assertEquals(200, response.statusCode());
        assertEquals("application/json; charset=utf-8", response.getHeader("Content-type"));
        assertEquals("Nazar", response.jsonPath().getString("booking.firstname"));
    }

    @Test
    public void testAuth() {
        String requestBody = """
                {
                "username" : "Nazar",
                "password" : "Beshta"
                }
                """;
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/auth")
                .then()
                .extract().response();
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testSearch(){
        Response response = given()
                .when()
                .get("/booking/1")
                .then()
                .extract().response();
        assertEquals(200, response.statusCode());
        assertEquals("Sally",response.jsonPath().getString("firstname"));
        assertEquals("Jackson",response.jsonPath().getString("lastname"));
        assertEquals("443",response.jsonPath().getString("totalprice"));
        assertEquals("false",response.jsonPath().getString("depositpaid"));
        assertEquals("2017-12-04",response.jsonPath().getString("bookingdates.checkin"));
        assertEquals("2019-12-30",response.jsonPath().getString("bookingdates.checkout"));
    }

    @Test
    public void testAccessToProtectedRes(){
        String requestBody = """
                {
                 "firstname" : "Nazar",
                 "lastname" : "Beshta",
                 "totalprice" : 111,
                 "depositpaid" : true,
                 "bookingdates" : {
                     "checkin" : "2018-01-01",
                     "checkout" : "2019-01-01"
                }
                }""";
        String token = "a2123";
        Response response = given()
                .headers("Content-type", "application/json","Authorization","Bearer"+token)
                .and()
                .body(requestBody)
                .when()
                .put("/booking/2")
                .then()
                .extract().response();
        assertEquals(403,response.getStatusCode());
    }

}
