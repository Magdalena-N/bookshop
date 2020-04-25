package net.stawrul.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * Klasa encyjna reprezentująca zamówienie w sklepie.
 */
@Entity
@Table(name = "orders")
@EqualsAndHashCode(of = "id")
public class Order {

    @Getter
    @Id
    private UUID id = UUID.randomUUID();

    @Getter
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderedBooks> orderedBooks = new ArrayList<>();

    @Getter
    @Temporal(TIMESTAMP)
    private Date creationDate;

    /**
     * Ustawienie pola creationDate na aktualny czas w chwili zapisu zamówienia
     * do bazy danych.
     */
    @PrePersist
    public void prePersist() {
        this.creationDate = new Date();
    }

    public int orderSize() {
        int size = 0;
        for (OrderedBooks ordered : orderedBooks) {
            size += ordered.getAmount();
        }
        return size;
    }
}
