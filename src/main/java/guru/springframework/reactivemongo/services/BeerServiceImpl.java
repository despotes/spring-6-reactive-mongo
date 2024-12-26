package guru.springframework.reactivemongo.services;

import guru.springframework.reactivemongo.model.BeerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BeerServiceImpl implements BeerService {
    @Override
    public Flux<BeerDTO> getBeers() {
        return null;
    }

    @Override
    public Mono<BeerDTO> getBeerById(String id) {
        return null;
    }

    @Override
    public Mono<BeerDTO> createBeer(BeerDTO beerDTO) {
        return null;
    }
}
