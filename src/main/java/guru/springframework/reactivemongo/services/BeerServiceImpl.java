package guru.springframework.reactivemongo.services;

import guru.springframework.reactivemongo.mappers.BeerMapper;
import guru.springframework.reactivemongo.model.BeerDTO;
import guru.springframework.reactivemongo.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public Mono<BeerDTO> findFirstByBeerName(String beerName) {
        return beerRepository.findFirstByBeerName(beerName)
                .map(beerMapper::toDTO);
    }

    @Override
    public Flux<BeerDTO> findByBeerStyle(String beerStyle) {
        return beerRepository.findByBeerStyle(beerStyle)
                .map(beerMapper::toDTO);
    }

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
    public Mono<BeerDTO> patchBeer(String beerId, BeerDTO beerDTO) {
        return beerRepository.findById(beerId)
                .map(foundBeer -> {
                    if (StringUtils.hasText(beerDTO.getBeerName())) {
                        foundBeer.setBeerName(beerDTO.getBeerName());
                    }
                    if (StringUtils.hasText(beerDTO.getBeerStyle())) {
                        foundBeer.setBeerStyle(beerDTO.getBeerStyle());
                    }
                    if (beerDTO.getPrice() != null && beerDTO.getPrice().doubleValue() > 0) {
                        foundBeer.setPrice(beerDTO.getPrice());
                    }
                    if (StringUtils.hasText(beerDTO.getUpc())) {
                        foundBeer.setUpc(beerDTO.getUpc());
                    }
                    if (beerDTO.getQuantityOnHand() != null && beerDTO.getQuantityOnHand() > 0) {
                        foundBeer.setQuantityOnHand(beerDTO.getQuantityOnHand());
                    }
                    return foundBeer;
                })
                .flatMap(beerRepository::save)
                .map(beerMapper::toDTO);
    }

    @Override
    public Mono<Void> deleteBeerById(String id) {
        return beerRepository.deleteById(id);
    }
}
