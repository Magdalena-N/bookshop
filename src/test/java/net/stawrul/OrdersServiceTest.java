package net.stawrul;

import net.stawrul.model.Book;
import net.stawrul.model.Order;
import net.stawrul.model.OrderedBooks;
import net.stawrul.services.OrdersService;
import net.stawrul.services.exceptions.EmptyOrderException;
import net.stawrul.services.exceptions.OutOfStockException;
import net.stawrul.services.exceptions.TooLargeOrderException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.Ordered;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class OrdersServiceTest {

    @Mock
    EntityManager em;

    @Test(expected = OutOfStockException.class)
    public void whenOrderedBookNotAvailable_placeOrderThrowsOutOfStockEx() {

        //Arrange
        Order order = new Order();
        Book book = new Book();
        OrderedBooks ordered = new OrderedBooks();
        book.setAmount(0);
        ordered.setBook(book);
        ordered.setAmount(1);
        order.getOrderedBooks().add(ordered);

        Mockito.when(em.find(Book.class, ordered.getBook().getId())).thenReturn(book);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - OutOfStockException expected
    }

    @Test(expected = EmptyOrderException.class)
    public void whenOrderIsEmpty_placeOrderThrowsEmptyOrderEx() {
        //Arrange
        Order order = new Order();

        OrdersService ordersService = new OrdersService(em);
        //Act
        ordersService.placeOrder(order);
        //Assert - EmptyOrderException expected
    }
    @Test(expected = TooLargeOrderException.class)
    public void whenOrderIsGreaterThanMaxSizeOfOrder_placeOrderThrowsTooLargeOrderEx() {

        //Arrange
        Order order = new Order();
        Book book = new Book();
        OrderedBooks ordered = new OrderedBooks();
        book.setAmount(4);
        ordered.setBook(book);
        ordered.setAmount(4);
        order.getOrderedBooks().add(ordered);

        Mockito.when(em.find(Book.class, ordered.getBook().getId())).thenReturn(book);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - TooLargeOrderException expected
    }
    @Test(expected = TooLargeOrderException.class)
    public void whenOrderWithVariousBooksIsGreaterThanMaxSizeOfOrder_placeOrderThrowsTooLargeOrderEx() {

        //Arrange
        Order order = new Order();

        // #1 book
        OrderedBooks ordered1 = new OrderedBooks();
        Book book1 = new Book();
        book1.setAmount(2);
        ordered1.setBook(book1);
        ordered1.setAmount(2);
        order.getOrderedBooks().add(ordered1);

        // #2 book
        OrderedBooks ordered2 = new OrderedBooks();
        Book book2 = new Book();
        book2.setAmount(2);
        ordered2.setBook(book2);
        ordered2.setAmount(2);
        order.getOrderedBooks().add(ordered2);

        Mockito.when(em.find(Book.class, ordered1.getBook().getId())).thenReturn(book1);
        Mockito.when(em.find(Book.class, ordered2.getBook().getId())).thenReturn(book2);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - TooLargeOrderException expected
    }
    @Test
    public void whenOrderedBookAvailable_placeOrderDecreasesAmountByOne() {

        //Arrange
        Order order = new Order();
        Book book = new Book();
        OrderedBooks ordered = new OrderedBooks();
        book.setAmount(1);
        ordered.setBook(book);
        ordered.setAmount(1);
        order.getOrderedBooks().add(ordered);

        Mockito.when(em.find(Book.class, ordered.getBook().getId())).thenReturn(book);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert
        //dostępna liczba książek zmniejszyła się:
        assertEquals(0, (int) book.getAmount());
        //nastąpiło dokładnie jedno wywołanie em.persist(order) w celu zapisania zamówienia:
        Mockito.verify(em, times(1)).persist(order);
    }
    @Test
    public void whenOrderedBooksAvailable_placeOrderDecreasesAmountByOrderedBooksAmount(){
        //Arrange
        Order order = new Order();
        Book book = new Book();
        OrderedBooks ordered = new OrderedBooks();
        book.setAmount(3);
        ordered.setBook(book);
        ordered.setAmount(2);
        order.getOrderedBooks().add(ordered);

        Mockito.when(em.find(Book.class, ordered.getBook().getId())).thenReturn(book);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert
        //dostępna liczba książek zmniejszyła się:
        assertEquals(1, (int) book.getAmount());
        //nastąpiło dokładnie jedno wywołanie em.persist(order) w celu zapisania zamówienia:
        Mockito.verify(em, times(1)).persist(order);

    }
    @Test
    public void whenOrderedVariousBooksAllAvailable_placeOrderDecreaseAmountOfEveryBookByOrderedBookAmount(){
        //Arrange
        Order order = new Order();


        // #1 book
        OrderedBooks ordered1 = new OrderedBooks();
        Book book1 = new Book();
        book1.setAmount(3);
        ordered1.setBook(book1);
        ordered1.setAmount(1);
        order.getOrderedBooks().add(ordered1);

        // #2 book
        OrderedBooks ordered2 = new OrderedBooks();
        Book book2 = new Book();
        book2.setAmount(1);
        ordered2.setBook(book2);
        ordered2.setAmount(1);
        order.getOrderedBooks().add(ordered2);

        // #3 book
        OrderedBooks ordered3 = new OrderedBooks();
        Book book3 = new Book();
        book3.setAmount(2);
        ordered3.setBook(book3);
        ordered3.setAmount(1);
        order.getOrderedBooks().add(ordered3);

        Mockito.when(em.find(Book.class, ordered1.getBook().getId())).thenReturn(book1);
        Mockito.when(em.find(Book.class, ordered2.getBook().getId())).thenReturn(book2);
        Mockito.when(em.find(Book.class, ordered3.getBook().getId())).thenReturn(book3);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert
        assertEquals(2,(int) book1.getAmount());
        assertEquals(0,(int) book2.getAmount());
        assertEquals(1,(int) book3.getAmount());

        //nastąpiło dokładnie jedno wywołanie em.persist(order) w celu zapisania zamówienia:
        Mockito.verify(em, times(1)).persist(order);
    }

    @Test(expected = OutOfStockException.class)
    public void whenAtLeastOneBookInOrderedBooksNotAvailable_placeOrderThrowsOutOfStockEx() {
        //Arrange
        Order order = new Order();


        // #1 book
        OrderedBooks ordered1 = new OrderedBooks();
        Book book1 = new Book();
        book1.setAmount(3);
        ordered1.setBook(book1);
        ordered1.setAmount(1);
        order.getOrderedBooks().add(ordered1);

        // #2 book
        OrderedBooks ordered2 = new OrderedBooks();
        Book book2 = new Book();
        book2.setAmount(1);
        ordered2.setBook(book2);
        ordered2.setAmount(1);
        order.getOrderedBooks().add(ordered2);

        // #3 book
        OrderedBooks ordered3 = new OrderedBooks();
        Book book3 = new Book();
        book3.setAmount(0);
        ordered3.setBook(book3);
        ordered3.setAmount(1);
        order.getOrderedBooks().add(ordered3);

        Mockito.when(em.find(Book.class, ordered1.getBook().getId())).thenReturn(book1);
        Mockito.when(em.find(Book.class, ordered2.getBook().getId())).thenReturn(book2);
        Mockito.when(em.find(Book.class, ordered3.getBook().getId())).thenReturn(book3);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - OutOfStockException expected
    }

    @Test(expected = EmptyOrderException.class)
    public void whenOrderIsNull_placeOrderThrowsEmptyOrderEx() {
        //Arrange
        Order order = null;
        OrdersService ordersService = new OrdersService(em);
        //Act
        ordersService.placeOrder(order);

        //Assert - EmptyOrderException exception
    }
    @Test(expected = OutOfStockException.class)
    public void whenBookIsNotAvailableAfterFirstPositionInOrder_placeOrderThrowsOutOfStockEx(){
        //Arrange
        Order order = new Order();
        OrdersService ordersService = new OrdersService(em);
        Book book = new Book();
        book.setAmount(2);

        // #1 orderedBook
        OrderedBooks ordered1 = new OrderedBooks();
        ordered1.setBook(book);
        ordered1.setAmount(2);
        order.getOrderedBooks().add(ordered1);

        // #2 orderedBook
        OrderedBooks ordered2 = new OrderedBooks();
        ordered2.setBook(book);
        ordered2.setAmount(1);
        order.getOrderedBooks().add(ordered2);

        Mockito.when(em.find(Book.class, ordered1.getBook().getId())).thenReturn(book);
        Mockito.when(em.find(Book.class, ordered2.getBook().getId())).thenReturn(book);

        //Act
        ordersService.placeOrder(order);

        //Assert - OutOfStockException expected
    }
}
