package com.ss.pizzeria.backend.data;

import com.ss.pizzeria.backend.data.dao.OrderRepository;
import com.ss.pizzeria.backend.data.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    public CommandLineRunner createOrders(OrderRepository orderRepos) {
        return dataset -> {
            if (orderRepos.count() != 0L) {
                log.info("Using already existing dataset !!");
                return;
            }
            log.info("Generating dataset...");
            orderRepos.saveAllAndFlush(createOrders());
            log.info("Data Generation is successful !!");
        };
    }

    private List<Order> createOrders() {

        return List.of(
                newOrder("NORMAL", "BEEF_NORMAL", "M", 1),
                newOrder("THIN", "CHEESE", "S", 5),
                newOrder("NORMAL", "CHICKEN-FAJITA", "L", 3)
        );
    }

    private Order newOrder(String crust, String flavor, String size, int tableNo) {
        Order myOrder = new Order();
        myOrder.setCrust(crust);
        myOrder.setFlavour(flavor);
        myOrder.setSize(size);
        myOrder.setTableNo(tableNo);
        return myOrder;
    }

}
