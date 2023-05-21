package cinema.service.impl;

import cinema.dao.MovieDao;
import cinema.model.Movie;
import cinema.service.MovieService;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class MovieServiceImpl implements MovieService {
    private static final Logger logger = LogManager.getLogger(MovieServiceImpl.class);
    private final MovieDao movieDao;

    public MovieServiceImpl(MovieDao movieDao) {
        this.movieDao = movieDao;
    }

    @Override
    public Movie add(Movie movie) {
        logger.info("Add movie method was called. Params: movie = {}", movie);
        return movieDao.add(movie);
    }

    @Override
    public Movie get(Long id) {
        logger.info("Get movie method was called. Params: id = {}", id);
        return movieDao.get(id).orElseThrow(() -> {
            logger.warn("Can't find movie with id {}", id);
            return new RuntimeException("Can't get movie by id " + id);
        });
    }

    @Override
    public List<Movie> getAll() {
        logger.info("Get all movies method was called.");
        return movieDao.getAll();
    }
}
