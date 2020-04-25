package net.stawrul.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

/**
 * Klasa encyjna reprezentująca towar w sklepie (książkę).
 */
@Entity
@Table(name = "orderedbooks")
@EqualsAndHashCode(of = "id")

public class OrderedBooks {

    @Id
    private UUID id = UUID.randomUUID();

    @Getter
    @Setter
    @ManyToOne
    private Book book;

    @Getter
    @Setter
    private Integer amount;
}
