package cinema.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import cinema.dao.OrderDao;
import cinema.model.CinemaHall;
import cinema.model.Movie;
import cinema.model.MovieSession;
import cinema.model.Order;
import cinema.model.Role;
import cinema.model.ShoppingCart;
import cinema.model.Ticket;
import cinema.model.User;
import cinema.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    private OrderService orderService;
    @Mock
    private OrderDao orderDao;
    @Mock
    private ShoppingCartService shoppingCartService;
    private User user;
    private Ticket ticket;
    private ShoppingCart shoppingCart;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderDao, shoppingCartService);
        Role userRole = new Role(Role.RoleName.USER);

        user = new User();
        user.setEmail("bob@gmail.com");
        user.setPassword("12345678");
        user.setRoles(Set.of(userRole));

        Movie movie = new Movie();
        movie.setTitle("Fast and Furious");
        movie.setDescription("Great movie!");

        CinemaHall cinemaHall = new CinemaHall();
        cinemaHall.setCapacity(500);
        cinemaHall.setDescription("Really big hall");

        MovieSession movieSession = new MovieSession();
        movieSession.setMovie(movie);
        movieSession.setCinemaHall(cinemaHall);
        movieSession.setShowTime(LocalDateTime.now());

        ticket = new Ticket();
        ticket.setUser(user);
        ticket.setMovieSession(movieSession);

        shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setTickets(List.of(ticket));
    }

    @Test
    void completeOrder_successComplete_ok() {
        Order expected = new Order();
        expected.setId(1L);
        expected.setUser(user);
        expected.setTickets(List.of(ticket));
        expected.setOrderTime(LocalDateTime.MAX);

        Mockito.when(orderDao.add(Mockito.any())).thenReturn(expected);
        Mockito.doNothing().when(shoppingCartService).clear(Mockito.any());

        Order actual = orderService.completeOrder(shoppingCart);

        Assertions.assertNotNull(actual, "There was no one Order added to DB");
        Assertions.assertEquals(expected.getId(), actual.getId(), "ID doesn't equal");
        Assertions.assertEquals(expected.getUser().getId(), actual.getUser().getId(),
                "User doesn't equal");
        Assertions.assertEquals(expected.getTickets().get(0), actual.getTickets().get(0),
                "Tickets don't equal");
    }

    @Test
    void getOrdersHistory_notEmptyOrderHistory_ok() {
        List<Order> orders = new ArrayList<>();
        Order order = new Order();
        order.setUser(user);
        order.setTickets(List.of(ticket));
        LocalDateTime dateTime = LocalDateTime.of(2023, 5, 13, 12, 30);
        order.setOrderTime(dateTime);
        orders.add(order);
        order.setOrderTime(dateTime.plusHours(2));
        orders.add(order);
        order.setOrderTime(dateTime.plusHours(5));
        orders.add(order);

        Mockito.when(orderDao.getOrdersHistory(user)).thenReturn(orders);
        List<Order> ordersHistory = orderService.getOrdersHistory(user);

        Assertions.assertNotNull(ordersHistory, "List of orders must be not null");
        Assertions.assertEquals(ordersHistory.size(), orders.size(),
                "Incorrect size of orders. Actual is "
                + ordersHistory.size() + " but must be " + orders.size());
    }

    @Test
    void getOrderHistory_emptyHistory_ok() {
        Mockito.when(orderDao.getOrdersHistory(user)).thenReturn(Collections.emptyList());
        List<Order> ordersHistory = orderService.getOrdersHistory(user);

        Assertions.assertNotNull(ordersHistory, "List of orders must be not null");
        Assertions.assertEquals(ordersHistory.size(), 0,
                "Incorrect size of orders. Actual is "
                        + ordersHistory.size() + " but must be 0");
    }
}