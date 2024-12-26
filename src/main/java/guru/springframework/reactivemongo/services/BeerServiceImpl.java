package guru.springframework.reactivemongo.services;

import guru.springframework.reactivemongo.mappers.BeerMapper;
import guru.springframework.reactivemongo.model.BeerDTO;
import guru.springframework.reactivemongo.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public Flux<BeerDTO> getBeers() {
        return null;
    }

    @Override
    public Mono<BeerDTO> getBeerById(String id) {
        return null;
    }

    @Override
    public Mono<BeerDTO> createBeer(Mono<BeerDTO> beerDTO) {
        return beerDTO.map(beerMapper::toEntity)
                .flatMap(beerRepository::save)
                .map(beerMapper::toDTO);
    }
}
