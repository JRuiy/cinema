package cinema.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import cinema.dao.impl.CinemaHallDaoImpl;
import cinema.dao.impl.MovieDaoImpl;
import cinema.dao.impl.MovieSessionDaoImpl;
import cinema.dao.impl.RoleDaoImpl;
import cinema.dao.impl.ShoppingCartDaoImpl;
import cinema.dao.impl.TicketDaoImpl;
import cinema.dao.impl.UserDaoImpl;
import cinema.exception.DataProcessingException;
import cinema.model.CinemaHall;
import cinema.model.Movie;
import cinema.model.MovieSession;
import cinema.model.Role;
import cinema.model.ShoppingCart;
import cinema.model.Ticket;
import cinema.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShoppingCartDaoTest extends AbstractTest {
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private User user;
    private Ticket ticket;
    private Role userRole;

    @BeforeEach
    void setUp() {
        shoppingCartDao = new ShoppingCartDaoImpl(getSessionFactory());
        RoleDao roleDao = new RoleDaoImpl(getSessionFactory());
        userDao = new UserDaoImpl(getSessionFactory());
        MovieDao movieDao = new MovieDaoImpl(getSessionFactory());
        CinemaHallDao cinemaHallDao = new CinemaHallDaoImpl(getSessionFactory());
        MovieSessionDao movieSessionDao = new MovieSessionDaoImpl(getSessionFactory());
        TicketDao ticketDao = new TicketDaoImpl(getSessionFactory());

        userRole = roleDao.add(new Role(Role.RoleName.USER));

        user = new User();
        user.setEmail("bob@gmail.com");
        user.setPassword("12345678");
        user.setRoles(Set.of(userRole));
        userDao.add(user);

        Movie movie = new Movie();
        movie.setTitle("Fast and Furious");
        movie.setDescription("Great movie!");
        movieDao.add(movie);

        CinemaHall cinemaHall = new CinemaHall();
        cinemaHall.setCapacity(500);
        cinemaHall.setDescription("Really big hall");
        cinemaHallDao.add(cinemaHall);

        MovieSession movieSession = new MovieSession();
        movieSession.setMovie(movie);
        movieSession.setCinemaHall(cinemaHall);
        movieSession.setShowTime(LocalDateTime.now());
        movieSessionDao.add(movieSession);

        ticket = new Ticket();
        ticket.setUser(user);
        ticket.setMovieSession(movieSession);
        ticketDao.add(ticket);
    }

    @Override
    protected Class<?>[] entities() {
        return new Class[]{ShoppingCart.class, User.class, Movie.class,
                CinemaHall.class, Ticket.class, MovieSession.class, Role.class};
    }

    @Test
    void add_successAdd_ok() {
        ShoppingCart expected = new ShoppingCart();
        expected.setUser(user);
        expected.setTickets(List.of(ticket));
        ShoppingCart actual = shoppingCartDao.add(expected);

        Assertions.assertNotNull(actual, "There was no one shoppingCart added to DB");
        Assertions.assertEquals(1L, actual.getId(), "ID doesn't equal");
        Assertions.assertEquals(expected.getUser().getId(), actual.getUser().getId(),
                "User doesn't equal");
        Assertions.assertEquals(expected.getTickets().size(), actual.getTickets().size(),
                "Quantity of tickets don't equal");
        Assertions.assertEquals(expected.getTickets().get(0).getId(),
                actual.getTickets().get(0).getId(), "Ticket doesn't match");
    }

    @Test
    void add_nullValue_notOk() {
        Assertions.assertThrows(DataProcessingException.class, () -> shoppingCartDao.add(null),
                "Method should throw DataProcessingException for null value");
    }

    @Test
    void getByUser_successGet_ok() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setTickets(List.of(ticket));
        shoppingCartDao.add(shoppingCart);

        ShoppingCart shoppingCartByUser = shoppingCartDao.getByUser(user);
        Assertions.assertNotNull(shoppingCartByUser, "There was no one shoppingCart in DB");
        Assertions.assertEquals(shoppingCartByUser.getUser().getId(), user.getId(),
                "User doesn't equal");
    }

    @Test
    void getByUser_nullValue_notOk() {
        Assertions.assertThrows(DataProcessingException.class,
                () -> shoppingCartDao.getByUser(null),
                "Method should throw DataProcessingException for null value");
    }

    @Test
    void getByUser_noShoppingCartPresentInDB_notOk() {
        Assertions.assertThrows(DataProcessingException.class,
                () -> shoppingCartDao.getByUser(user),
                "Method should throw DataProcessingException when no present ShoppingCart");
    }

    @Test
    void update_successUpdateShoppingCart_ok() {
        ShoppingCart saved = new ShoppingCart();
        saved.setUser(user);
        saved.setTickets(List.of(ticket));
        shoppingCartDao.add(saved);

        User userUpdated = new User();
        userUpdated.setEmail("alice@gmail.com");
        userUpdated.setPassword("12345678");
        userUpdated.setRoles(Set.of(userRole));
        userDao.add(userUpdated);

        saved.setUser(userUpdated);
        saved.setTickets(new ArrayList<>());
        ShoppingCart updated = shoppingCartDao.update(saved);

        Assertions.assertNotNull(updated, "Updated shoppingCart should be not null");
        Assertions.assertEquals(saved.getId(), updated.getId(), "Id doesn't equals");
        Assertions.assertEquals(userUpdated.getId(), updated.getUser().getId(), "User`s id doesn't equals");
        Assertions.assertEquals(updated.getTickets().size(), 0, "List of tickets wasn't changed");
    }

    @Test
    void update_noShoppingCartToUpdate_notOk() {
        ShoppingCart shoppingCart = new ShoppingCart();
        Assertions.assertThrows(DataProcessingException.class, () -> shoppingCartDao.update(shoppingCart),
                "Method should throw DataProcessingException for transient object");
    }

    @Test
    void update_nullValue_notOk() {
        Assertions.assertThrows(DataProcessingException.class, () -> shoppingCartDao.update(null),
                "Method should throw DataProcessingException for null value");
    }
}