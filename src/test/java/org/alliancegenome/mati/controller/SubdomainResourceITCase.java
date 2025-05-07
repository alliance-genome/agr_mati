package org.alliancegenome.mati.controller;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.common.mapper.TypeRef;
import org.alliancegenome.mati.configuration.PostgresResource;
import org.alliancegenome.mati.entity.SubdomainEntity;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
@WithTestResource(value = PostgresResource.class)
@Order(1)
class SubdomainResourceITCase {

	@Test
	public void getAll() {
		given()
			.when()
			.get("/api/subdomain")
			.then()
			.statusCode(200);
	}

	@Test
	public void getOne() {
		List<SubdomainEntity> result = given()
			.when()
			.get("/api/subdomain?id=1")
			.then()
			.statusCode(200).extract().body().as(getSubdomainEntityTypeRef());

		assertThat(result.size(), is(1));
		assertThat(result.getFirst().getCode(), is("100"));
	}

	private TypeRef<List<SubdomainEntity>> getSubdomainEntityTypeRef() {
		return new TypeRef<>() {
		};
	}
}
