package org.alliancegenome.mati.controller;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.alliancegenome.mati.configuration.PostgresResource;
import org.junit.jupiter.api.*;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

/**
 * Tests the /api/admin/counters endpoint
 * It runs *after* the IdentifierResourceITcase tests
 * that mint some identifiers
 */
@QuarkusIntegrationTest
@WithTestResource(value = PostgresResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(3)
public class AdminResourceITCase {

	private String authorization;

	@BeforeAll
	void setup() {
		authorization = OktaHelper.createAuthorization();
	}


	@Test
	public void getCounters() {
		Map counters = given()
			.contentType(JSON)
			.header("Accept", "application/json")
			.header("Authorization", authorization)
			.when()
			.get("/api/admin/counters")
			.then()
			.statusCode(200)
			.extract().body().as(Map.class);

		Assertions.assertEquals(5, counters.get("disease_annotation"));
		Assertions.assertEquals(4, counters.get("person"));
		Assertions.assertEquals(6, counters.get("resource"));
		Assertions.assertEquals(3, counters.get("reference"));
	}

	@Test
	public void testCurationRolldown() {
		given()
			.contentType(ContentType.JSON)
			.header("Accept", "application/json")
			.header("Authorization", authorization)
			.when()
			.post("/api/admin/rolldown_for_curation")
			.then()
			.statusCode(200);
	}
}
