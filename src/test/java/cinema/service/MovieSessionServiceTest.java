package cinema.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import cinema.dao.MovieSessionDao;
import cinema.model.CinemaHall;
import cinema.model.Movie;
import cinema.model.MovieSession;
import cinema.service.impl.MovieSessionServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovieSessionServiceTest {
    private MovieSessionService movieSessionService;
    @Mock
    private MovieSessionDao movieSessionDao;
    private CinemaHall cinemaHall;
    private Movie movie;
    private MovieSession addedMovieSession;

    @BeforeEach
    void setUp() {
        movieSessionService = new MovieSessionServiceImpl(movieSessionDao);
        cinemaHall = new CinemaHall();
        cinemaHall.setCapacity(100);
        cinemaHall.setDescription("Big hall");

        movie = new Movie();
        movie.setTitle("Fast and Furious");
        movie.setDescription("Great movie!");

        addedMovieSession = new MovieSession();
        addedMovieSession.setMovie(movie);
        addedMovieSession.setCinemaHall(cinemaHall);
        addedMovieSession.setShowTime(LocalDateTime.MAX);
    }

    @Test
    void add_successAdd_ok() {
        MovieSession expected = new MovieSession();
        expected.setId(1L);
        expected.setMovie(movie);
        expected.setCinemaHall(cinemaHall);
        expected.setShowTime(LocalDateTime.MAX);

        Mockito.when(movieSessionDao.add(addedMovieSession)).thenReturn(expected);
        MovieSession actual = movieSessionService.add(addedMovieSession);


        Assertions.assertNotNull(actual, "There was no one movieSession added to DB");
        Assertions.assertEquals(1L, actual.getId(), "ID doesn't equal");
        Assertions.assertEquals(expected.getMovie().getId(), actual.getMovie().getId(),
                "Movie doesn't equal");
        Assertions.assertEquals(expected.getCinemaHall().getId(), actual.getCinemaHall().getId(),
                "CinemaHall doesn't equal");
        Assertions.assertEquals(expected.getShowTime(), actual.getShowTime(),
                "ShowTime doesn't equal");
    }

    @Test
    void get_successFind_ok() {
        MovieSession expected = new MovieSession();
        expected.setId(1L);
        expected.setMovie(movie);
        expected.setCinemaHall(cinemaHall);
        expected.setShowTime(LocalDateTime.MAX);

        Mockito.when(movieSessionDao.get(1L)).thenReturn(Optional.of(expected));

        MovieSession actual = movieSessionService.get(1L);
        Assertions.assertNotNull(actual, "There was no one movieSession added to DB");
        Assertions.assertEquals(1L, actual.getId(), "ID doesn't equal");
        Assertions.assertEquals(expected.getMovie().getId(), actual.getMovie().getId(),
                "Movie doesn't equal");
        Assertions.assertEquals(expected.getCinemaHall().getId(), actual.getCinemaHall().getId(),
                "CinemaHall doesn't equal");
        Assertions.assertEquals(expected.getShowTime(), actual.getShowTime(),
                "ShowTime doesn't equal");
    }

    @Test
    void get_noExistMovieSession_notOk() {
        Mockito.when(movieSessionDao.get(1L)).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class, () -> movieSessionService.get(1L),
                "Method should throws exception when no one movieSession exists in DB");
    }

    @Test
    void delete_successDeleteMovieSession_ok() {
        movieSessionService.delete(1L);
        Mockito.verify(movieSessionDao, Mockito.times(1)).delete(1L);
    }

    @Test
    void findAvailableSession_successFindSession_ok() {
        List<MovieSession> movieSessions = new ArrayList<>();

        MovieSession movieSession = new MovieSession();
        movieSession.setId(1L);
        movieSession.setMovie(movie);
        movieSession.setCinemaHall(cinemaHall);
        LocalDateTime dateTime = LocalDateTime.of(2023, 5, 13, 12, 30);
        movieSession.setShowTime(dateTime);
        movieSessions.add(movieSession);
        movieSession.setShowTime(dateTime.minusDays(1));
        movieSessions.add(movieSession);
        movieSession.setShowTime(dateTime.plusHours(3));
        movieSessions.add(movieSession);

        Mockito.when(movieSessionDao.findAvailableSessions(1L, dateTime.toLocalDate()))
                .thenReturn(movieSessions);
        List<MovieSession> availableSessions =
                movieSessionService.findAvailableSessions(1L, dateTime.toLocalDate());
        Assertions.assertNotNull(availableSessions, "List of availableSessions must be not null");
        Assertions.assertEquals(3, availableSessions.size(),
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
}