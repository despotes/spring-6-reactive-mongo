package guru.springframework.reactivemongo.services;

import guru.springframework.reactivemongo.mappers.CustomerMapper;
import guru.springframework.reactivemongo.model.CustomerDTO;
import guru.springframework.reactivemongo.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Flux<CustomerDTO> findAll() {
        return customerRepository.findAll().map(customerMapper::toDTO);
    }

    @Override
    public Flux<CustomerDTO> findAllByCustomerName(String customerName) {
        return customerRepository.findByCustomerName(customerName).map(customerMapper::toDTO);
    }

    @Override
    public Mono<CustomerDTO> findById(String id) {
        return customerRepository.findById(id).map(customerMapper::toDTO);
    }

    @Override
    public Mono<CustomerDTO> save(CustomerDTO customerDTO) {
        return customerRepository.save(customerMapper.toEntity(customerDTO)).map(customerMapper::toDTO);
    }

    @Override
    public Mono<CustomerDTO> save(Mono<CustomerDTO> customerDTO) {
        return customerDTO.flatMap(this::save);
    }

    @Override
    public Mono<CustomerDTO> updateById(String id, CustomerDTO customerDTO) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setCustomerName(customerDTO.getCustomerName());
                    return customer;
                })
                .flatMap(customerRepository::save)
                .map(customerMapper::toDTO);
    }

    @Override
    public Mono<CustomerDTO> patchById(String id, CustomerDTO customerDTO) {
        return customerRepository.findById(id)
                .map(customer -> {
                    if (StringUtils.hasText(customerDTO.getCustomerName())) {
                        customer.setCustomerName(customerDTO.getCustomerName());
                    }
                    return customer;
                })
                .flatMap(customerRepository::save)
                .map(customerMapper::toDTO);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return customerRepository.deleteById(id);
    }
}
