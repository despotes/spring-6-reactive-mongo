package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class BeerRouterConfig {
    public static final String BEERS_PATH = "/api/v3/beer";
    public static final String BEERS_PATH_ID = BEERS_PATH +  "/{beerId}";

    private final BeerHandler beerHandler;

    @Bean
    public RouterFunction<ServerResponse> beerRoutes() {
        return route()
                .GET(BEERS_PATH, accept(APPLICATION_JSON),beerHandler::listBeers)
                .GET(BEERS_PATH_ID, accept(APPLICATION_JSON),beerHandler::getBeerById)
                .build();
    }
}
