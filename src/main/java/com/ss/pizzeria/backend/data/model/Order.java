package com.ss.pizzeria.backend.data.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * DB Table for Order.
 *
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

    // marked as NonNull to validate with the REST request, but should not be initialized
    @NotNull
    private Pizza.Crust crust;

    @NotNull
    private Pizza.Size size;

    @Column(name = "table_no")
    @NotNull
    private int tableNo;

    @NotNull
    private Pizza.Flavour flavour;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "person_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_order_person_id", value = ConstraintMode.CONSTRAINT))
    @NotNull
    private Person customer;

    @NotNull
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
