package cinema.dao;

import java.time.LocalDateTime;
import java.util.Set;
import cinema.dao.impl.CinemaHallDaoImpl;
import cinema.dao.impl.MovieDaoImpl;
import cinema.dao.impl.MovieSessionDaoImpl;
import cinema.dao.impl.RoleDaoImpl;
import cinema.dao.impl.TicketDaoImpl;
import cinema.dao.impl.UserDaoImpl;
import cinema.model.CinemaHall;
import cinema.model.Movie;
import cinema.model.MovieSession;
import cinema.model.Role;
import cinema.model.Ticket;
import cinema.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TicketDaoTest extends AbstractTest {

    private TicketDao ticketDao;
    private User user;
    private MovieSession movieSession;

    @Override
    protected Class<?>[] entities() {
        return new Class[] {Ticket.class, Movie.class,
                CinemaHall.class, MovieSession.class, User.class, Role.class};
    }

    @BeforeEach
    void setUp() {
        RoleDao roleDao = new RoleDaoImpl(getSessionFactory());
        UserDao userDao = new UserDaoImpl(getSessionFactory());
        MovieDao movieDao = new MovieDaoImpl(getSessionFactory());
        CinemaHallDao cinemaHallDao = new CinemaHallDaoImpl(getSessionFactory());
        MovieSessionDao movieSessionDao = new MovieSessionDaoImpl(getSessionFactory());
        ticketDao = new TicketDaoImpl(getSessionFactory());

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

        movieSession = new MovieSession();
        movieSession.setMovie(movie);
        movieSession.setCinemaHall(cinemaHall);
        movieSession.setShowTime(LocalDateTime.now());
        movieSessionDao.add(movieSession);
    }

    @Test
    void add_successAdd_ok() {
        Ticket expected = new Ticket();
        expected.setUser(user);
        expected.setMovieSession(movieSession);
        Ticket actual = ticketDao.add(expected);

        Assertions.assertNotNull(actual, "There was no one ticket added to DB");
        Assertions.assertEquals(1L, actual.getId(), "ID doesn't equal");
        Assertions.assertEquals(user.getId(), expected.getUser().getId(), "User doesn't equal");
        Assertions.assertEquals(movieSession.getId(), expected.getMovieSession().getId(), "User doesn't equal");
    }
}