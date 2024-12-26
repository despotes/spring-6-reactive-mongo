package guru.springframework.reactivemongo.services;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.mappers.BeerMapper;
import guru.springframework.reactivemongo.mappers.BeerMapperImpl;
import guru.springframework.reactivemongo.model.BeerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;


@Testcontainers
@SpringBootTest
class BeerServiceImplTest {

    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    private BeerService beerService;

    @Autowired
    private BeerMapper beerMapper;

    BeerDTO beerDTO;

    @BeforeEach
    void setUp() {
        beerDTO = beerMapper.toDTO(getTestBeer());
    }

    @Test
    void testFindByBeerStyle() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        BeerDTO savedDto = getSavedBeerDto();
        beerService.findByBeerStyle(savedDto.getBeerStyle())
                .subscribe(dto -> {
                    System.out.println(dto.toString());
                    atomicBoolean.set(true);
                });

        await().untilTrue(atomicBoolean);
    }

    @Test
    void testFindFirstByName() {
        BeerDTO beerDTO = getSavedBeerDto();
        AtomicReference<BeerDTO> beerRef = new AtomicReference<>();
        Mono<BeerDTO> foundBeer = beerService.findFirstByBeerName(beerDTO.getBeerName());

        foundBeer.subscribe(dto -> {
            beerRef.set(dto);
        });

        await().until(() -> beerRef.get() != null);
        assertThat(beerRef.get().getBeerName()).isEqualTo(beerDTO.getBeerName());
    }

    @Test
    @DisplayName("Test Save Beer Using Subscriber")
    void saveBeerUseSubscriber() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        AtomicReference<BeerDTO> beerRef = new AtomicReference<>();

        Mono<BeerDTO> savedMonod = beerService.createBeer(Mono.just(beerDTO));

        savedMonod.subscribe(beerDTO -> {
            System.out.println(beerDTO.getId());
            atomicBoolean.set(true);
            beerRef.set(beerDTO);
        });

        await().untilTrue(atomicBoolean);
        BeerDTO persistedBeer = beerRef.get();
        assertThat(persistedBeer).isNotNull();
        assertThat(persistedBeer.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Save Beer Using Block")
    void testSaveBeerUseBlock() {
        BeerDTO savedDto = beerService.createBeer(Mono.just(getTestBeerDto())).block();
        assertThat(savedDto).isNotNull();
        assertThat(savedDto.getId()).isNotNull();
    }

    @Test
    void testUpdateBlocking() {
        final String newName = "New Beer Name";
        BeerDTO savedDto = getSavedBeerDto();
        savedDto.setBeerName(newName);

        BeerDTO updatedDto = beerService.updateBeer(savedDto.getId(), savedDto).block();

        // verify exits in db
        BeerDTO fetchedDto = beerService.getBeerById(updatedDto.getId()).block();
        assertThat(fetchedDto.getBeerName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Test Update Using Reactive Streams")
    void testUpdateStreaming() {
        final String newName = "New Beer Name";
        AtomicReference<BeerDTO> beerRef = new AtomicReference<>();
        beerService.createBeer(Mono.just(getTestBeerDto()))
                .map(savedBeerDto -> {
                    savedBeerDto.setBeerName(newName);
                    return savedBeerDto;
                })
                .flatMap(beerService::createBeer)
                .flatMap(savedUpdatedBeerDto -> beerService.getBeerById(savedUpdatedBeerDto.getId()))
                .subscribe(beerDTO -> {
                    beerRef.set(beerDTO);
                });
        await().until(() -> beerRef.get() != null);
        assertThat(beerRef.get().getBeerName()).isEqualTo(newName);
    }

    @Test
    void testDeleteBeer() {
        BeerDTO savedDto = getSavedBeerDto();
        beerService.deleteBeerById(savedDto.getId()).block();

        Mono<BeerDTO> expectedEmptyBeerMono = beerService.getBeerById(savedDto.getId());

        BeerDTO emptyBeer = expectedEmptyBeerMono.block();
        assertThat(emptyBeer).isNull();
    }

    @Test
    void createBeer() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Mono<BeerDTO> beerMono = beerService.createBeer(Mono.just(beerDTO));


        beerMono.subscribe(savedDto -> {
            System.out.println(savedDto.getId());
            atomicBoolean.set(true);
        });

        await().until(atomicBoolean::get);
    }


    public BeerDTO getSavedBeerDto() {
        return beerService.createBeer(getTestBeerDto()).block();
    }

    public static BeerDTO getTestBeerDto() {
        return new BeerMapperImpl().toDTO(getTestBeer());
    }

    public static Beer getTestBeer() {
        return Beer.builder()
                .beerName("Space Dust")
                .beerStyle("IPA")
                .price(BigDecimal.TEN)
                .upc("123456789")
                .build();
    }
}