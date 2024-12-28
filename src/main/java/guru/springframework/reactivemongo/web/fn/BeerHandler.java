package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.model.BeerDTO;
import guru.springframework.reactivemongo.services.BeerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class BeerHandler {
    private final BeerService beerService;
    private final Validator validator;

    private void validateBeer(BeerDTO beerDTO) {
        Errors errors = new BeanPropertyBindingResult(beerDTO, "beerDTO");
        validator.validate(beerDTO, errors);
        if (errors.hasErrors()) {
            throw new ServerWebInputException(errors.getAllErrors().toString());
        }
    }

    public Mono<ServerResponse> listBeers(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(beerService.getBeers(), BeerDTO.class);
    }

    public Mono<ServerResponse> getBeerById(ServerRequest request) {
        return beerService.getBeerById(request.pathVariable("beerId"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(beerDTO -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(beerDTO), BeerDTO.class));
    }

    public Mono<ServerResponse> createBeer(ServerRequest request) {
        return beerService.createBeer(request.bodyToMono(BeerDTO.class).doOnNext(this::validateBeer))
                .flatMap(beerDTO -> ServerResponse.created(
                        UriComponentsBuilder.fromPath(BeerRouterConfig.BEERS_PATH_ID).build(beerDTO.getId())
                ).build());
    }

    public Mono<ServerResponse> updateBeer(ServerRequest request) {
        return request.bodyToMono(BeerDTO.class)
                .doOnNext(this::validateBeer)
                .flatMap(beerDTO -> beerService.updateBeer(request.pathVariable("beerId"), beerDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(beerDTO -> ServerResponse.noContent().build());

    }

    public Mono<ServerResponse> patchBeer(ServerRequest request) {
        return request.bodyToMono(BeerDTO.class)
                .doOnNext(this::validateBeer)
                .flatMap(beerDTO -> beerService.patchBeer(request.pathVariable("beerId"), beerDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(beerDTO -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> deleteBeer(ServerRequest request) {
        return beerService.getBeerById(request.pathVariable("beerId"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(beerDTO -> beerService.deleteBeerById(beerDTO.getId()))
                .then(ServerResponse.noContent().build());
    }
}
