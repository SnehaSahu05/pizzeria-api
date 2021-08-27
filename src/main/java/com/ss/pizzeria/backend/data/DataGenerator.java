package com.ss.pizzeria.backend.data;

import com.ss.pizzeria.backend.data.dao.PersonRepository;
import com.ss.pizzeria.backend.data.model.Order;
import com.ss.pizzeria.backend.data.model.Person;
import com.ss.pizzeria.backend.data.model.Pizza;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * Add data to the table on startup.
 * Alternatively, can add import.sql in resources directory to populate database.
 * The generator can be disabled by commenting out @Component/@Configuration annotations.
 *
 * @author Sneha
 */
@Slf4j
//@Component
@Configuration
public class DataGenerator {

    @Bean
    public CommandLineRunner createOrders(PersonRepository personRepos) {
        // No need for explicit order repository,
        // because the CASCADE.ALL in Person's OrderList
        // will eventually add orders when adding associated Person
        return dataset -> {
            if (personRepos.count() != 0L) {
                log.info("Using already existing dataset !!");
                return;
            }
            log.info("Generating dataset...");
            personRepos.saveAllAndFlush(createOrders());
            log.info("Data Generation is successful !!");
        };
    }

    @NonNull
    private List<Person> createOrders() {
        // create customers
        final Person p1 = new Person("John");
        final Person p2 = new Person("Joe");
        // create orders linked to customer
        final Order o1 = newOrder(Pizza.Flavour.HAWAII, Pizza.Size.M, 1, p1);
        final Order o2 = newOrder(Pizza.Flavour.QUARTTRO_FORMAGGI, Pizza.Size.L, 5, p2);
        final Order o3 = newOrder(Pizza.Flavour.REGINA, Pizza.Size.L, 3, p1);
        // update customer with order
        p1.setOrderList(List.of(o1, o3));
        p2.setOrderList(List.of(o2));
        return List.of(p1, p2);
    }

    @NonNull
    private Order newOrder(@NonNull final Pizza.Flavour flavor,
                           @NonNull final Pizza.Size size, @NonNull final int tableNo,
                           @NonNull final Person p) {
        Order myOrder = new Order();
        myOrder.setCrust(Pizza.Crust.THIN);
        myOrder.setFlavour(flavor);
        myOrder.setSize(size);
        myOrder.setTableNo(tableNo);
        myOrder.setCustomer(p);
        return myOrder;
    }

}
