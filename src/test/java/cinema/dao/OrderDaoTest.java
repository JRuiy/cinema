package cinema.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import cinema.dao.impl.CinemaHallDaoImpl;
import cinema.dao.impl.MovieDaoImpl;
import cinema.dao.impl.MovieSessionDaoImpl;
import cinema.dao.impl.OrderDaoImpl;
import cinema.dao.impl.RoleDaoImpl;
import cinema.dao.impl.TicketDaoImpl;
import cinema.dao.impl.UserDaoImpl;
import cinema.exception.DataProcessingException;
import cinema.model.CinemaHall;
import cinema.model.Movie;
import cinema.model.MovieSession;
import cinema.model.Order;
import cinema.model.Role;
import cinema.model.Ticket;
import cinema.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderDaoTest extends AbstractTest {

    private OrderDao orderDao;
    private User user;
    private Ticket ticket;

    @Override
    protected Class<?>[] entities() {
        return new Class[] {Order.class, User.class, Movie.class,
                CinemaHall.class, Ticket.class, MovieSession.class, Role.class};
    }

    @BeforeEach
    void setUp() {
        orderDao = new OrderDaoImpl(getSessionFactory());
        RoleDao roleDao = new RoleDaoImpl(getSessionFactory());
        UserDao userDao = new UserDaoImpl(getSessionFactory());
        MovieDao movieDao = new MovieDaoImpl(getSessionFactory());
        CinemaHallDao cinemaHallDao = new CinemaHallDaoImpl(getSessionFactory());
        MovieSessionDao movieSessionDao = new MovieSessionDaoImpl(getSessionFactory());
        TicketDao ticketDao = new TicketDaoImpl(getSessionFactory());

        Role userRole = roleDao.add(new Role(Role.RoleName.USER));

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

    @Test
    void add_successAdd_ok() {
        Order expected = new Order();
        expected.setUser(user);
        expected.setTickets(List.of(ticket));
        expected.setOrderTime(LocalDateTime.now());

        Order actual = orderDao.add(expected);
        Assertions.assertNotNull(actual, "There was no one shoppingCart added to DB");
        Assertions.assertEquals(1L, actual.getId(), "ID doesn't equal");
        Assertions.assertEquals(expected.getTickets().get(0), actual.getTickets().get(0), "Tickets don't equal");
    }

    @Test
    void add_nullValue_notOk() {
        Assertions.assertThrows(DataProcessingException.class, () -> orderDao.add(null),
                "Method should throw DataProcessingException for null value");
    }

    @Test
    void getOrdersHistory_existUserInDB_ok() {
        Order order = new Order();
        order.setUser(user);
        order.setTickets(List.of(ticket));
        order.setOrderTime(LocalDateTime.now());
        orderDao.add(order);

        List<Order> orders = orderDao.getOrdersHistory(user);

        Assertions.assertNotNull(orders, "List of orders must be not null");
        Assertions.assertEquals(1, orders.size(), "Incorrect size of orders. Actual is "
                + orders.size() + " but must be 1");
    }

    @Test
    void getOrdersHistory_notExistUserInDB_notOk() {
        User user = new User();
        Assertions.assertThrows(DataProcessingException.class, () -> orderDao.getOrdersHistory(user),
                "Method should throw DataProcessingException for null value");
    }

    @Test
    void getOrdersHistory_noOrders_ok() {
        List<Order> orders = orderDao.getOrdersHistory(user);

        Assertions.assertNotNull(orders, "List of orders must be not null");
        Assertions.assertEquals(0, orders.size(), "Incorrect size of orders. Actual is "
                + orders.size() + " but must be 0");
    }
}