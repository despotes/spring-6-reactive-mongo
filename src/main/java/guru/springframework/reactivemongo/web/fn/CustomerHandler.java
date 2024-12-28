package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.domain.Customer;
import guru.springframework.reactivemongo.model.CustomerDTO;
import guru.springframework.reactivemongo.services.CustomerService;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerHandler {

    private final CustomerService customerService;
    private final Validator validator;

    public void validate(CustomerDTO customerDTO) {
        Errors errors = new BeanPropertyBindingResult(customerDTO, "customerDTO");
        validator.validate(customerDTO, errors);
        if (errors.hasErrors()) {
            throw new ServerWebInputException(errors.getAllErrors().toString());
        }
    }


    public Mono<ServerResponse> listCustomers(ServerRequest request) {
        Flux<CustomerDTO> flux;
        if (request.queryParam("customerName").isPresent()) {
            flux = customerService.findAllByCustomerName(request.queryParam("customerName").get());
        } else {
            flux = customerService.findAll();
        }
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(flux, Customer.class);
    }

    public Mono<ServerResponse> getCustomerById(ServerRequest request) {
        return customerService.findById(request.pathVariable("customerId"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(customerDTO -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(customerDTO), Customer.class));
    }

    public Mono<ServerResponse> createCustomer(ServerRequest request) {
        return customerService.save(request.bodyToMono(CustomerDTO.class).doOnNext(this::validate))
                .flatMap(customerDTO ->
                        ServerResponse.created(
                                UriComponentsBuilder
                                        .fromPath(CustomerRouterConfig.CUSTOMER_PATH)
                                        .build(customerDTO.getId())
                        ).build()
                );
    }

    public Mono<ServerResponse> updateCustomerById(ServerRequest request) {
        return request.bodyToMono(CustomerDTO.class)
                .doOnNext(this::validate)
                .flatMap(customerDTO -> customerService.updateById(request.pathVariable("customerId"), customerDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(savedCustomer -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> patchCustomerById(ServerRequest request) {
        return request.bodyToMono(CustomerDTO.class)
                .doOnNext(this::validate)
                .flatMap(foundCustomer -> customerService.patchById(request.pathVariable("customerId"), foundCustomer))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(savedCustomer -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> deleteCustomerById(ServerRequest request) {
        return customerService.findById(request.pathVariable("customerId"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(foundCustomer -> customerService.deleteById(foundCustomer.getId()))
                .then(ServerResponse.noContent().build());
    }
}
