package net.stawrul.controllers;

import net.stawrul.model.Book;
import net.stawrul.services.BooksService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;


/**
 * Kontroler zawierający akcje związane z książkami w sklepie.
 * <p>
 * Parametr "/books" w adnotacji @RequestMapping określa prefix dla adresów wszystkich akcji kontrolera.
 */
@RestController
@RequestMapping("/books")
public class BooksController {

    //Komponent realizujący logikę biznesową operacji na książkach
    private final BooksService booksService;

    //Instancja klasy BooksService zostanie dostarczona przez framework Spring
    //(wstrzykiwanie zależności przez konstruktor).
    public BooksController(BooksService booksService) {
        this.booksService = booksService;
    }

    /**
     * Pobieranie listy wszystkich książek.
     * <p>
     * Żądanie:
     * GET /books
     *
     * @return lista książek
     */
    @GetMapping
    public List<Book> listBooks() {
        return booksService.findAll();
    }

    /**
     * Dodawanie nowej książki.
     * <p>
     * Żądanie:
     * POST /books
     *
     * @param book       obiekt zawierający dane nowej książki, zostanie zbudowany na podstawie danych
     *                   przesłanych w ciele żądania (automatyczne mapowanie z formatu JSON na obiekt
     *                   klasy Book)
     * @param uriBuilder pomocniczy obiekt do budowania adresu wskazującego na nowo dodaną książkę,
     *                   zostanie wstrzyknięty przez framework Spring
     * @return odpowiedź HTTP dla klienta
     */
    @PostMapping
    public ResponseEntity<Void> addBook(@RequestBody Book book, UriComponentsBuilder uriBuilder) {

        if (booksService.find(book.getId()) == null) {
            //Identyfikator nie istnieje w bazie danych - nowa książka zostaje zapisana
            booksService.save(book);

            //Jeśli zapisywanie się powiodło zwracana jest odpowiedź 201 Created z nagłówkiem Location, który zawiera
            //adres nowo dodanej książki
            URI location = uriBuilder.path("/books/{id}").buildAndExpand(book.getId()).toUri();
            return ResponseEntity.created(location).build();

        } else {
            //Identyfikator książki już istnieje w bazie danych. Żądanie POST służy do dodawania nowych elementów,
            //więc zwracana jest odpowiedź z kodem błędu 409 Conflict
            return ResponseEntity.status(CONFLICT).build();
        }
    }

    /**
     * Pobieranie informacji o pojedynczej książce.
     * <p>
     * Żądanie:
     * GET /books/{id}
     *
     * @param id identyfikator książki
     * @return odpowiedź 200 zawierająca dane książki lub odpowiedź 404, jeśli książka o podanym identyfikatorze nie
     * istnieje w bazie danych
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable UUID id) {
        //wyszukanie książki w bazie danych
        Book book = booksService.find(id);

        //W warstwie biznesowej brak książki o podanym id jest sygnalizowany wartością null. Jeśli książka nie została
        //znaleziona zwracana jest odpowiedź 404 Not Found. W przeciwnym razie klient otrzymuje odpowiedź 200 OK
        //zawierającą dane książki w domyślnym formacie JSON
        return book != null ? ResponseEntity.ok(book) : ResponseEntity.notFound().build();
    }

    /**
     * Aktualizacja danych książki.
     * <p>
     * Żądanie:
     * PUT /books/{id}
     *
     * @param book
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBook(@RequestBody Book book) {
        if (booksService.find(book.getId()) != null) {
            Book newBook = booksService.find(book.getId());
            if (book.getAuthor() != null) {
                newBook.setAuthor(book.getAuthor());
            }
            if (book.getPrice() != null) {
                newBook.setPrice(book.getPrice());
            }
            if (book.getPublisher() != null) {
                newBook.setPublisher(book.getPublisher());
            }
            if (book.getNumberOfPages() != null) {
                newBook.setNumberOfPages(book.getNumberOfPages());
            }
            if (book.getAmount() != null) {
                newBook.setAmount(book.getAmount());
            }
            if (book.getTitle() != null) {
                newBook.setTitle(book.getTitle());
            }

            //aktualizacja danych jest możliwa o ile książka o podanym id istnieje w bazie danych
            booksService.save(newBook);
            return ResponseEntity.ok().build();

        } else {
            //nie odnaleziono książki o podanym id - odpowiedź 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

}
