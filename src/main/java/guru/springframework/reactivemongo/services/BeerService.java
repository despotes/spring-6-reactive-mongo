package guru.springframework.reactivemongo.services;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.model.BeerDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface BeerService {
    Flux<BeerDTO> getBeers();
    Mono<BeerDTO> getBeerById(String id);
    Mono<BeerDTO> createBeer(Mono<BeerDTO> beerDTO);
}
