package cinema.service.impl;

import cinema.dao.MovieSessionDao;
import cinema.model.MovieSession;
import cinema.service.MovieSessionService;
import java.time.LocalDate;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class MovieSessionServiceImpl implements MovieSessionService {
    private static final Logger logger = LogManager.getLogger(MovieServiceImpl.class);
    private final MovieSessionDao movieSessionDao;

    public MovieSessionServiceImpl(MovieSessionDao movieSessionDao) {
        this.movieSessionDao = movieSessionDao;
    }

    @Override
    public List<MovieSession> findAvailableSessions(Long movieId, LocalDate date) {
        logger.info("FindAvailableSessions method was called. Params: movieId = {}, date = {}",
                movieId, date);
        return movieSessionDao.findAvailableSessions(movieId, date);
    }

    @Override
    public MovieSession add(MovieSession session) {
        logger.info("Add movie session method was called. Params: session = {}", session);
        return movieSessionDao.add(session);
    }

    @Override
    public MovieSession get(Long id) {
        logger.info("Get movie session method was called. Params: id = {}", id);
        return movieSessionDao.get(id).orElseThrow(() -> {
            logger.warn("Can't find movie session with id {}", id);
            return new RuntimeException("Session with id " + id + " not found");
        });
    }

    @Override
    public MovieSession update(MovieSession movieSession) {
        logger.info("Update movie session method was called. Params: session = {}",
                movieSession);
        return movieSessionDao.update(movieSession);
    }

    @Override
    public void delete(Long id) {
        logger.info("Delete movie session method was called. Params: id = {}", id);
        movieSessionDao.delete(id);
    }
}
