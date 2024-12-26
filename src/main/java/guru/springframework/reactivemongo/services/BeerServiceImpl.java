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
        return beerRepository.findAll().map(beerMapper::toDTO);
    }

    @Override
    public Mono<BeerDTO> getBeerById(String id) {
        return beerRepository.findById(id).map(beerMapper::toDTO);
    }

    @Override
    public Mono<BeerDTO> createBeer(Mono<BeerDTO> beerDTO) {
        return beerDTO.map(beerMapper::toEntity)
                .flatMap(beerRepository::save)
                .map(beerMapper::toDTO);
    }

    @Override
    public Mono<BeerDTO> createBeer(BeerDTO beerDTO) {
        return createBeer(Mono.just(beerDTO));
    }

    @Override
    public Mono<BeerDTO> updateBeer(String id, BeerDTO beerDTO) {
        return getBeerById(id)
                .map(foundBeer -> {
                    foundBeer.setBeerName(beerDTO.getBeerName());
                    foundBeer.setBeerStyle(beerDTO.getBeerStyle());
                    foundBeer.setPrice(beerDTO.getPrice());
                    foundBeer.setUpc(beerDTO.getUpc());
                    foundBeer.setQuantityOnHand(beerDTO.getQuantityOnHand());
                    return foundBeer;
                })
                .map(beerMapper::toEntity)
                .flatMap(beerRepository::save)
                .map(beerMapper::toDTO);
    }

    @Override
    public Mono<Void> deleteBeerById(String id) {
        return beerRepository.deleteById(id);
    }
}
