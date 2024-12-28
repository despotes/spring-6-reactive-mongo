package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.model.BeerDTO;
import guru.springframework.reactivemongo.services.BeerServiceImplTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest()
@AutoConfigureWebTestClient
class BeerEndpointTest {

    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    WebTestClient webTestClient;

    @Test
    @Order(2)
    void testListBeers() {
        webTestClient.get().uri(BeerRouterConfig.BEERS_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, "application/json")
                .expectBody().jsonPath("$.size()").isEqualTo(3);
    }

    @Test
    @Order(1)
    void testGetById() {
        webTestClient.get().uri(BeerRouterConfig.BEERS_PATH_ID ,+ 1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, "application/json")
                .expectBody(BeerDTO.class);
    }

    @Test
    @Order(3)
    void testCreateBeer() {
        webTestClient.post().uri(BeerRouterConfig.BEERS_PATH)
                .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("http://localhost:8080/api/v2/beer/4");
    }

    @Test
    @Order(3)
    void testCreateBeerBadData() {
        Beer testBeer = BeerServiceImplTest.getTestBeer();
        testBeer.setBeerName("");

        webTestClient.post().uri(BeerRouterConfig.BEERS_PATH)
                .body(Mono.just(testBeer), BeerDTO.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(4)
    void testUpdateBeer() {
        webTestClient.put().uri(BeerRouterConfig.BEERS_PATH_ID, 1)
                .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(4)
    void testUpdateBeerNotFound() {
        webTestClient.put().uri(BeerRouterConfig.BEERS_PATH_ID, 999)
                .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(4)
    void testUpdateBeerBadRequest() {
        Beer testBeer = BeerServiceImplTest.getTestBeer();
        testBeer.setBeerName("");
        webTestClient.put().uri(BeerRouterConfig.BEERS_PATH_ID, 1)
                .body(Mono.just(testBeer), BeerDTO.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(4)
    void testPatchBeer() {
        webTestClient.patch().uri(BeerRouterConfig.BEERS_PATH_ID, 1)
                .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(4)
    void testPatchBeerNotFound() {
        webTestClient.patch().uri(BeerRouterConfig.BEERS_PATH_ID, 999)
                .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(5)
    void testDeleteBeer() {
        webTestClient.delete().uri(BeerRouterConfig.BEERS_PATH_ID, 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(5)
    void testDeleteBeerNotFound() {
        webTestClient.delete().uri(BeerRouterConfig.BEERS_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(6)
    void testGetByIdNotFound() {
        webTestClient.get().uri(BeerRouterConfig.BEERS_PATH_ID ,999)
                .exchange()
                .expectStatus().isNotFound();
    }
    
}