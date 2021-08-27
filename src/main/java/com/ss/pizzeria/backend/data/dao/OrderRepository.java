package com.ss.pizzeria.backend.data.dao;

import com.ss.pizzeria.backend.data.model.Order;
import com.ss.pizzeria.backend.data.model.Person;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * finds all orders for given customer, sorted by timestamp
     */
    @NotNull
    List<Order> findAllByCustomer(@NotNull final Person customer, @NotNull final Sort timestamp);

}
