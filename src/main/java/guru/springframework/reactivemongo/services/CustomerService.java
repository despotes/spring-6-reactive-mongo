package guru.springframework.reactivemongo.services;

import guru.springframework.reactivemongo.domain.Customer;
import guru.springframework.reactivemongo.model.CustomerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Flux<CustomerDTO> findAll();

    Flux<CustomerDTO> findAllByCustomerName(String customerName);

    Mono<CustomerDTO> findById(String id);

    Mono<CustomerDTO> save(CustomerDTO customerDTO);

    Mono<CustomerDTO> save(Mono<CustomerDTO> customerDTO);

    Mono<CustomerDTO> updateById(String id, CustomerDTO customerDTO);

    Mono<CustomerDTO> patchById(String id, CustomerDTO customerDTO);

    Mono<Void> deleteById(String id);
}
