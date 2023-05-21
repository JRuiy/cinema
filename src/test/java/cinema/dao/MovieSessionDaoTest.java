package cinema.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import cinema.dao.impl.CinemaHallDaoImpl;
import cinema.dao.impl.MovieDaoImpl;
import cinema.dao.impl.MovieSessionDaoImpl;
import cinema.exception.DataProcessingException;
import cinema.model.CinemaHall;
import cinema.model.Movie;
import cinema.model.MovieSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MovieSessionDaoTest extends AbstractTest {
    private MovieSessionDao movieSessionDao;
    private CinemaHall cinemaHall;
    private Movie movie;

    @Override
    protected Class<?>[] entities() {
        return new Class[] {MovieSession.class, CinemaHall.class, Movie.class};
    }

    @BeforeEach
    void setUp() {
        movieSessionDao = new MovieSessionDaoImpl(getSessionFactory());
        CinemaHallDao cinemaHallDao = new CinemaHallDaoImpl(getSessionFactory());
        MovieDao movieDao = new MovieDaoImpl(getSessionFactory());

        cinemaHall = new CinemaHall();
        cinemaHall.setCapacity(100);
        cinemaHall.setDescription("Big hall");
        cinemaHallDao.add(cinemaHall);

        movie = new Movie();
        movie.setTitle("Fast and Furious");
        movie.setDescription("Great movie!");
        movieDao.add(movie);
    }

    @Test
    void save_successSave_ok() {
        MovieSession expected = new MovieSession();
        expected.setMovie(movie);
        expected.setCinemaHall(cinemaHall);
        LocalDateTime dateTime = LocalDateTime.of(2023, 5, 13, 12, 30);
        expected.setShowTime(dateTime);
        MovieSession actual = movieSessionDao.add(expected);

        Assertions.assertNotNull(actual, "There was no one movie session added to DB");
        Assertions.assertEquals(1L, actual.getId(), "ID doesn't equal");
        Assertions.assertEquals(expected.getMovie().getId(), actual.getMovie().getId(),
                "Movie doesn't equal");
        Assertions.assertEquals(expected.getCinemaHall().getId(), actual.getCinemaHall().getId(),
                "CinemaHall doesn't equal");
        Assertions.assertEquals(expected.getShowTime(), actual.getShowTime(),
                "Show time doesn't equal");
    }

    @Test
    void get_successFindById_ok() {
        MovieSession expected = new MovieSession();
        expected.setMovie(movie);
        expected.setCinemaHall(cinemaHall);
        LocalDateTime dateTime = LocalDateTime.of(2023, 5, 13, 12, 30);
        expected.setShowTime(dateTime);
        movieSessionDao.add(expected);

        Optional<MovieSession> movieSessionById = movieSessionDao.get(1L);
        if (movieSessionById.isEmpty()) {
            Assertions.fail("No one movie session present in DB. You have some problem with add() method");
        }
        Assertions.assertEquals(movieSessionById.get().getId(), 1L,
                "IDs don't equal. Actual id = " + movieSessionById.get().getId() + " but must be 1");
    }

    @Test
    void get_noMovieSessionPresentInDbById_notOk() {
        Optional<MovieSession> movieSessionById = movieSessionDao.get(1L);
        Assertions.assertEquals(movieSessionById, Optional.empty(),
                "Method should return Optional.empty()");
    }

    @Test
    void get_nullValue_notOk() {
        Assertions.assertThrows(DataProcessingException.class, () -> movieSessionDao.get(null),
                "Method should throw DataProcessingException for null value");
    }
    // не працює
    @Test
    void findAvailableSession_successFindSession_ok() {
        MovieSession movieSession = new MovieSession();
        movieSession.setMovie(movie);
        movieSession.setCinemaHall(cinemaHall);
        LocalDateTime dateTime = LocalDateTime.of(2023, 5, 13, 12, 30);
        movieSession.setShowTime(dateTime);
        movieSessionDao.add(movieSession);
        movieSession.setShowTime(dateTime.minusDays(1));
        movieSessionDao.add(movieSession);
        movieSession.setShowTime(dateTime.plusHours(3));
        movieSessionDao.add(movieSession);

        List<MovieSession> availableSessions =
                movieSessionDao.findAvailableSessions(1L, dateTime.toLocalDate());
        Assertions.assertNotNull(availableSessions, "List of availableSessions must be not null");
        Assertions.assertEquals(2, availableSessions.size(),
                "Size of available sessions must be 1, but actual is " + availableSessions.size());
    }

    @Test
    void findAvailableSession_noOneMovieSession_ok() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 5, 13, 12, 30);
        List<MovieSession> availableSessions =
                movieSessionDao.findAvailableSessions(1L, dateTime.toLocalDate());
        Assertions.assertNotNull(availableSessions, "List of availableSessions must be not null");
        Assertions.assertEquals(0, availableSessions.size(),
                "Size of available sessions must be 0, but actual is " + availableSessions.size());
    }

    @Test
    void delete_successDeleteMovieSession_ok() {
        MovieSession movieSession = new MovieSession();
        movieSession.setMovie(movie);
        movieSession.setCinemaHall(cinemaHall);
        LocalDateTime dateTime = LocalDateTime.of(2023, 5, 13, 12, 30);
        movieSession.setShowTime(dateTime);
        movieSessionDao.add(movieSession);
        movieSession.setShowTime(dateTime.minusDays(1));
        movieSessionDao.add(movieSession);
        movieSession.setShowTime(dateTime.plusHours(3));
        movieSessionDao.add(movieSession);

        movieSessionDao.delete(1L);
        List<MovieSession> movieSessions = ((MovieSessionDaoImpl) movieSessionDao).getAll();
        for (MovieSession movieSessionsFromDB : movieSessions) {
            if (movieSessionsFromDB.getId().equals(1L)) {
                Assertions.fail("You don't delete MovieSession by id 1, or delete the wrong one");
            }
        }
        Assertions.assertEquals(movieSessions.size(), 2,
                "A size of all MovieSessions should be 2, but actual " + movieSessions.size());
    }

    @Test
    void delete_noMovieSessionToDelete_notOk() {
        Assertions.assertThrows(DataProcessingException.class, () -> movieSessionDao.delete(1L),
                "Method should throw DataProcessingException when no one MovieSession in DB with id 1");
    }

    @Test
    void delete_nullValue_notOk() {
        Assertions.assertThrows(DataProcessingException.class, () -> movieSessionDao.delete(null),
                "Method should throw DataProcessingException for null value");
    }

    @Test
    void update_successUpdateMovieSession_ok() {
        MovieSession saved = new MovieSession();
        saved.setMovie(movie);
        saved.setCinemaHall(cinemaHall);
        LocalDateTime dateTime = LocalDateTime.of(2023, 5, 13, 12, 30);
        saved.setShowTime(dateTime);
        movieSessionDao.add(saved);

        saved.setShowTime(dateTime.plusDays(1));
        MovieSession updated = movieSessionDao.update(saved);
        Assertions.assertNotNull(updated, "Updated saved should be not null");
        Assertions.assertEquals(saved.getId(), updated.getId(), "Id was changed");
        Assertions.assertEquals(saved.getMovie().getId(), updated.getMovie().getId(),
                "MovieId doesn't match");
        Assertions.assertEquals(saved.getCinemaHall().getId(), updated.getCinemaHall().getId(),
                "CinemaHallId doesn't match");
        Assertions.assertEquals(updated.getShowTime(), dateTime.plusDays(1),"Date time wasn't changed");
    }

    @Test
    void update_noMovieSessionToUpdate_notOk() {
        MovieSession movieSession = new MovieSession();
        movieSession.setMovie(movie);
        movieSession.setCinemaHall(cinemaHall);
        LocalDateTime dateTime = LocalDateTime.of(2023, 5, 13, 12, 30);
        movieSession.setShowTime(dateTime);
        Assertions.assertThrows(DataProcessingException.class, () -> movieSessionDao.update(movieSession),
                "Method should throw DataProcessingException for transient object");
    }

    @Test
    void update_nullValue_notOk() {
        Assertions.assertThrows(DataProcessingException.class, () -> movieSessionDao.update(null),
                "Method should throw DataProcessingException for null value");
    }
}