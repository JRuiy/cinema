package cinema.dao;

import java.util.List;
import java.util.Optional;
import cinema.dao.impl.MovieDaoImpl;
import cinema.exception.DataProcessingException;
import cinema.model.Movie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MovieDaoTest extends AbstractTest {
    private MovieDao movieDao;

    @Override
    protected Class<?>[] entities() {
        return new Class[] {Movie.class};
    }

    @BeforeEach
    void setUp() {
        movieDao = new MovieDaoImpl(getSessionFactory());
    }

    @Test
    void save_successSave_ok() {
        Movie expected = new Movie();
        expected.setTitle("Fast and Furious");
        expected.setDescription("Great movie!");
        Movie actual = movieDao.add(expected);
        Assertions.assertNotNull(actual, "There was no one movie added to DB");
        Assertions.assertEquals(1L, actual.getId(), "ID doesn't equal");
        Assertions.assertEquals(expected.getTitle(), actual.getTitle(), "Title doesn't equal");
        Assertions.assertEquals(expected.getDescription(), actual.getDescription(),
                "Description doesn't equal");
    }

    @Test
    void save_nullValue_notOk() {
        Assertions.assertThrows(DataProcessingException.class, () -> movieDao.add(null),
                "Method should throw DataProcessingException for null value");
    }

    @Test
    void get_successFindById_ok() {
        Movie expected = new Movie();
        expected.setTitle("Fast and Furious");
        expected.setDescription("Great movie!");
        movieDao.add(expected);
        Optional<Movie> movieById = movieDao.get(1L);
        if (movieById.isEmpty()) {
            Assertions.fail("No one movie present in DB. You have some problem with add() method");
        }
        Assertions.assertEquals(movieById.get().getId(), 1L,
                "IDs don't equal. Actual id = " + movieById.get().getId() + " but must be 1");
    }

    @Test
    void get_noMoviePresentInDbById_notOk() {
        Optional<Movie> movieById = movieDao.get(1L);
        Assertions.assertEquals(movieById, Optional.empty(),
                "Method should return Optional.empty()");
    }

    @Test
    void get_nullValue_notOk() {
        Assertions.assertThrows(DataProcessingException.class, () -> movieDao.get(null),
                "Method should throw DataProcessingException for null value");
    }

    @Test
    void getAll_successFind_ok() {
        Movie movie = new Movie();
        movie.setTitle("Fast and Furious");
        movie.setDescription("Great movie!");
        movieDao.add(movie);
        movie.setTitle("Fast and Furious 2");
        movieDao.add(movie);
        movie.setTitle("Fast and Furious 3");
        movieDao.add(movie);

        List<Movie> movies = movieDao.getAll();
        Assertions.assertNotNull(movies, "List of movies should be not null");
        Assertions.assertEquals(movies.size(), 3,
                "List has to contain 3 element, but actual " + movies.size());
    }

    @Test
    void getAll_noOneMoviePresentInDB_notOk() {
        List<Movie> movies = movieDao.getAll();
        Assertions.assertNotNull(movies, "List of movies should be not null");
        Assertions.assertTrue(movies.isEmpty(), "List is not empty");
    }
}