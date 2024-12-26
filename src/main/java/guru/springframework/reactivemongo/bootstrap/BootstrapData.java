package guru.springframework.reactivemongo.bootstrap;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final BeerRepository beerRepository;

    @Override
    public void run(String... args) throws Exception {
        beerRepository.deleteAll()
                .then(loadBeerData())
                .subscribe(null,
                        throwable -> System.out.println("Error: " + throwable.getMessage()),
                        () -> System.out.println("Loaded Beer Data Successfully"));
    }

    private Mono<Void> loadBeerData() {
        return beerRepository.count()
                .filter(count -> count == 0L)
                .flatMapMany(count -> {
                    Beer beer1 = Beer.builder()
                            .beerName("Galaxy Cat")
                            .beerStyle("Pale Ale")
                            .upc("123456")
                            .price(new BigDecimal("12.99"))
                            .quantityOnHand(122)
                            .createdDate(LocalDateTime.now())
                            .lastModifiedDate(LocalDateTime.now())
                            .build();

                    Beer beer2 = Beer.builder()
                            .beerName("Crank")
                            .beerStyle("Pale Ale")
                            .upc("1235622")
                            .price(new BigDecimal("11.99"))
                            .quantityOnHand(392)
                            .createdDate(LocalDateTime.now())
                            .lastModifiedDate(LocalDateTime.now())
                            .build();

                    Beer beer3 = Beer.builder()
                            .beerName("Sunshine City")
                            .beerStyle("IPA")
                            .upc("123456")
                            .price(new BigDecimal("13.99"))
                            .quantityOnHand(144)
                            .createdDate(LocalDateTime.now())
                            .lastModifiedDate(LocalDateTime.now())
                            .build();

                    return beerRepository.saveAll(List.of(beer1, beer2, beer3));
                }).then();

    }
}
