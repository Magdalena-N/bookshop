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
@Table(name = "books")
@EqualsAndHashCode(of = "id")
@NamedQueries(value = {
        @NamedQuery(name = Book.FIND_ALL, query = "SELECT b FROM Book b")
})
public class Book {
    public static final String FIND_ALL = "Book.FIND_ALL";

    @Getter
    @Id
    private UUID id = UUID.randomUUID();

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private Integer amount;

    @Getter
    @Setter
    private String author;

    @Getter
    @Setter
    private Float price;

    @Getter
    @Setter
    private Integer numberOfPages;

    @Getter
    @Setter
    private String publisher;

}
