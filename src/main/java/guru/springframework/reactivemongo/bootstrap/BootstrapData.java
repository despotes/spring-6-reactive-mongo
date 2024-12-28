package guru.springframework.reactivemongo.bootstrap;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.domain.Customer;
import guru.springframework.reactivemongo.repositories.BeerRepository;
import guru.springframework.reactivemongo.repositories.CustomerRepository;
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
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        beerRepository.deleteAll()
                .then(loadBeerData())
                .subscribe(null,
                        throwable -> System.out.println("Error: " + throwable.getMessage()),
                        () -> System.out.println("Loaded Beer Data Successfully"));

        customerRepository.deleteAll()
                .then(loadCustomerData())
                .subscribe(null,
                        throwable -> System.out.println("Error: " + throwable.getMessage()),
                        () -> System.out.println("Loaded Customer Data Successfully"));
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

    private Mono<Void> loadCustomerData() {
        return customerRepository.count()
                .filter(count -> count == 0)
                .flatMapMany(count -> {
                    Customer customer1 = Customer.builder()
                            .customerName("Alice")
                            .build();

                    Customer customer2 = Customer.builder()
                            .customerName("Bob")
                            .build();

                    Customer customer3 = Customer.builder()
                            .customerName("Charlie")
                            .build();

                    return customerRepository.saveAll(List.of(customer1, customer2, customer3));
                }).then();

    }
}
