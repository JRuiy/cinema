package cinema.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import cinema.dao.ShoppingCartDao;
import cinema.dao.TicketDao;
import cinema.model.CinemaHall;
import cinema.model.Movie;
import cinema.model.MovieSession;
import cinema.model.Role;
import cinema.model.ShoppingCart;
import cinema.model.Ticket;
import cinema.model.User;
import cinema.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    private ShoppingCartService shoppingCartService;
    @Mock
    private ShoppingCartDao shoppingCartDao;
    @Mock
    private TicketDao ticketDao;
    private User user;
    private Ticket ticket;
    private ShoppingCart shoppingCart;
    private MovieSession movieSession;

    @BeforeEach
    void setUp() {
        shoppingCartService = new ShoppingCartServiceImpl(shoppingCartDao, ticketDao);
        Role userRole = new Role(Role.RoleName.USER);

        user = new User();
        user.setId(1L);
        user.setEmail("bob@gmail.com");
        user.setPassword("12345678");
        user.setRoles(Set.of(userRole));

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Fast and Furious");
        movie.setDescription("Great movie!");

        CinemaHall cinemaHall = new CinemaHall();
        cinemaHall.setId(1L);
        cinemaHall.setCapacity(500);
        cinemaHall.setDescription("Really big hall");

        movieSession = new MovieSession();
        movieSession.setId(1L);
        movieSession.setMovie(movie);
        movieSession.setCinemaHall(cinemaHall);
        movieSession.setShowTime(LocalDateTime.MAX);

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setUser(user);
        ticket.setMovieSession(movieSession);

        shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setTickets(List.of(ticket));
    }

    @Test
    void addSession_successAdd_ok() {
        shoppingCart.setTickets(new ArrayList<>());
        Mockito.when(ticketDao.add(Mockito.any())).thenReturn(ticket);
        Mockito.when(shoppingCartDao.getByUser(user)).thenReturn(shoppingCart);
        shoppingCartService.addSession(movieSession, user);
        Mockito.verify(shoppingCartDao, Mockito.times(1)).update(shoppingCart);
    }

    @Test
    void getByUser_findShoppingCart_ok() {
        shoppingCart.setId(1L);
        Mockito.when(shoppingCartDao.getByUser(user)).thenReturn(shoppingCart);
        ShoppingCart actual = shoppingCartService.getByUser(user);

        Assertions.assertNotNull(actual, "There was no one shoppingCart added to DB");
        Assertions.assertEquals(1L, actual.getId(), "ID doesn't equal");
        Assertions.assertEquals(actual.getUser().getId(), shoppingCart.getUser().getId(),
                "User doesn't equal");
        Assertions.assertEquals(actual.getTickets().size(), shoppingCart.getTickets().size(),
                "Tickets list size doesn't equal");
    }

    @Test
    void registerNewShoppingCart_successRegister_ok() {
        shoppingCartService.registerNewShoppingCart(user);
        Mockito.verify(shoppingCartDao, Mockito.times(1)).add(Mockito.any());
    }

    @Test
    void clear_successClear_ok() {
        shoppingCartService.clear(shoppingCart);
        Mockito.verify(shoppingCartDao, Mockito.times(1)).update(shoppingCart);
    }
}