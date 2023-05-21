package cinema.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import cinema.dao.MovieDao;
import cinema.model.Movie;
import cinema.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {
    private MovieService movieService;
    @Mock
    private MovieDao movieDao;

    @BeforeEach
    void setUp() {
        movieService = new MovieServiceImpl(movieDao);
    }

    @Test
    void add_successSave_ok() {
        Movie addedMovie = new Movie();
        addedMovie.setTitle("Fast and furious");
        addedMovie.setDescription("Cool movie");

        Movie expected = new Movie();
        expected.setId(1L);
        expected.setTitle("Fast and furious");
        expected.setDescription("Cool movie");

        Mockito.when(movieDao.add(addedMovie)).thenReturn(expected);
        Movie actual = movieService.add(addedMovie);

        Assertions.assertNotNull(actual, "There was no one movie added to DB");
        Assertions.assertEquals(1L, actual.getId(), "ID doesn't equal");
        Assertions.assertEquals(addedMovie.getTitle(), actual.getTitle(), "Title doesn't equal");
        Assertions.assertEquals(addedMovie.getDescription(), actual.getDescription(),
                "Description doesn't equal");
    }

    @Test
    void get_successFind_ok() {
        Movie expected = new Movie();
        expected.setId(1L);
        expected.setTitle("Fast and furious");
        expected.setDescription("Cool movie");

        Mockito.when(movieDao.get(1L)).thenReturn(Optional.of(expected));

        Movie actual = movieService.get(1L);
        Assertions.assertNotNull(actual, "There was no one movie added to DB");
        Assertions.assertEquals(expected.getId(), actual.getId(),
                "IDs don't equal. Actual id = " + actual.getId()
                        + " but must be " + expected.getId());
        Assertions.assertEquals(expected.getTitle(), actual.getTitle(),
                "Title doesn't equal. Actual id = " + actual.getTitle()
                        + "but must be " + expected.getTitle());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription(),
                "Description doesn't equal. Actual id = " + actual.getDescription()
                        + " but must be " + expected.getDescription());
    }

    @Test
    void get_noExistCinemaHall_notOk() {
        Mockito.when(movieDao.get(1L)).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class, () -> movieService.get(1L),
                "Method should throws exception when no one movie exists in DB");
    }

    @Test
    void getAll_successFind_ok() {
        List<Movie> moviesFromDB = new ArrayList<>();

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Fast and furious");
        movie.setDescription("Big hall");
        moviesFromDB.add(movie);

        movie.setId(2L);
        movie.setTitle("Fast and furious 2");
        moviesFromDB.add(movie);

        movie.setId(3L);
        movie.setTitle("Fast and furious");
        moviesFromDB.add(movie);

        Mockito.when(movieDao.getAll()).thenReturn(moviesFromDB);
        List<Movie> actual = movieService.getAll();

        Assertions.assertNotNull(actual, "List of cinema halls should be not null");
        Assertions.assertEquals(actual.size(), 3,
                "List has to contain 3 element, but actual " + actual.size());
    }

    @Test
    void getAll_noOneCinemaHallPresentInDB_notOk() {
        Mockito.when(movieDao.getAll()).thenReturn(Collections.emptyList());
        List<Movie> actual = movieService.getAll();

        Assertions.assertNotNull(actual, "List of movies should be not null");
        Assertions.assertTrue(actual.isEmpty(), "List is not empty");
    }
}