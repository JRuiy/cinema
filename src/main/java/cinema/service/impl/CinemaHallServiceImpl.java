package cinema.service.impl;

import cinema.dao.CinemaHallDao;
import cinema.model.CinemaHall;
import cinema.service.CinemaHallService;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CinemaHallServiceImpl implements CinemaHallService {
    private static final Logger logger = LogManager.getLogger(CinemaHallServiceImpl.class);
    private final CinemaHallDao cinemaHallDao;

    public CinemaHallServiceImpl(CinemaHallDao cinemaHallDao) {
        this.cinemaHallDao = cinemaHallDao;
    }

    @Override
    public CinemaHall add(CinemaHall cinemaHall) {
        logger.info("Add cinema hall method was called. Params: cinema hall = {}", cinemaHall);
        return cinemaHallDao.add(cinemaHall);
    }

    @Override
    public CinemaHall get(Long id) {
        logger.info("Get cinema hall method was called. Params: id = {}", id);
        return cinemaHallDao.get(id).orElseThrow(() -> {
            logger.warn("Can't find cinema hall with id {}", id);
            return new RuntimeException("Can't get cinema hall by id " + id);
        });
    }

    @Override
    public List<CinemaHall> getAll() {
        logger.info("Get all cinema halls method was called.");
        return cinemaHallDao.getAll();
    }
}
