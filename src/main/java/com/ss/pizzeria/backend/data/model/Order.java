package com.ss.pizzeria.backend.data.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * DB Table for Order.
 * @author Sneha
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @NonNull
    private String crust;

    @NonNull
    private String size;

    @Column(name = "table_no")
    @NonNull
    private int tableNo;

    @NonNull
    private String flavour;

    @NonNull
    private String timestamp;

    public Order() {
        this.timestamp = getCurrentTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Order order = (Order) o;

        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return 737800560;
    }

    private String getCurrentTime() {
        return Instant.now().truncatedTo(ChronoUnit.MILLIS).toString();
    }

}
