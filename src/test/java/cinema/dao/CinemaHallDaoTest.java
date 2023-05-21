package cinema.dao;

import java.util.List;
import java.util.Optional;
import cinema.dao.impl.CinemaHallDaoImpl;
import cinema.exception.DataProcessingException;
import cinema.model.CinemaHall;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CinemaHallDaoTest extends AbstractTest{
    private CinemaHallDao cinemaHallDao;

    @Override
    protected Class<?>[] entities() {
        return new Class[] {CinemaHall.class};
    }

    @BeforeEach
    void setUp() {
        cinemaHallDao = new CinemaHallDaoImpl(getSessionFactory());
    }

    @Test
    void add_successSave_ok() {
        CinemaHall expected = new CinemaHall();
        expected.setCapacity(100);
        expected.setDescription("Big hall");
        CinemaHall actual = cinemaHallDao.add(expected);
        Assertions.assertNotNull(actual, "There was no one cinema hall added to DB");
        Assertions.assertEquals(1L, actual.getId(), "ID doesn't equal");
        Assertions.assertEquals(expected.getCapacity(), actual.getCapacity(), "Capacity doesn't equal");
        Assertions.assertEquals(expected.getDescription(), actual.getDescription(),
                "Description doesn't equal");
    }

    @Test
    void add_nullValue_notOk() {
        Assertions.assertThrows(DataProcessingException.class, () -> cinemaHallDao.add(null),
                "Method should throw DataProcessingException for null value");
    }

    @Test
    void get_successFindById_notOk() {
        CinemaHall expected = new CinemaHall();
        expected.setCapacity(100);
        expected.setDescription("Big hall");
        cinemaHallDao.add(expected);
        Optional<CinemaHall> cinemaHallById = cinemaHallDao.get(1L);
        if (cinemaHallById.isEmpty()) {
            Assertions.fail("No one cinema hall present in DB. You have some problem with add() method");
        }
        Assertions.assertEquals(cinemaHallById.get().getId(), 1L,
                "IDs don't equal. Actual id = " + cinemaHallById.get().getId() + " but must be 1");
    }

    @Test
    void get_noCinemaHallPresentInDbById_notOk() {
        Optional<CinemaHall> cinemaHallById = cinemaHallDao.get(1L);
        Assertions.assertEquals(cinemaHallById, Optional.empty(),
                "Method should return Optional.empty()");
    }

    @Test
    void get_nullValue_notOk() {
        Assertions.assertThrows(DataProcessingException.class, () -> cinemaHallDao.get(null),
                "Method should throw DataProcessingException for null value");
    }

    @Test
    void getAll_successFind_ok() {
        CinemaHall cinemaHall = new CinemaHall();
        cinemaHall.setCapacity(100);
        cinemaHall.setDescription("Big hall");
        cinemaHallDao.add(cinemaHall);
        cinemaHall.setCapacity(150);
        cinemaHallDao.add(cinemaHall);
        cinemaHall.setCapacity(200);
        cinemaHallDao.add(cinemaHall);

        List<CinemaHall> cinemaHalls = cinemaHallDao.getAll();
        Assertions.assertNotNull(cinemaHalls, "List of cinema halls should be not null");
        Assertions.assertEquals(cinemaHalls.size(), 3,
                "List has to contain 3 element, but actual " + cinemaHalls.size());
    }

    @Test
    void getAll_noOneCinemaHallPresentInDB_notOk() {
        List<CinemaHall> cinemaHalls = cinemaHallDao.getAll();
        Assertions.assertNotNull(cinemaHalls, "List of cinema halls should be not null");
        Assertions.assertTrue(cinemaHalls.isEmpty(), "List is not empty");
    }
}