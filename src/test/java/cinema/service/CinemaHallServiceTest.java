package cinema.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import cinema.dao.CinemaHallDao;
import cinema.model.CinemaHall;
import cinema.service.CinemaHallService;
import cinema.service.impl.CinemaHallServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CinemaHallServiceTest {
    private CinemaHallService cinemaHallService;
    @Mock
    private CinemaHallDao cinemaHallDao;

    @BeforeEach
    void setUp() {
        cinemaHallService = new CinemaHallServiceImpl(cinemaHallDao);
    }

    @Test
    void add_successSave_ok() {
        CinemaHall addedCinemaHall = new CinemaHall();
        addedCinemaHall.setCapacity(100);
        addedCinemaHall.setDescription("Big hall");

        CinemaHall expected = new CinemaHall();
        expected.setId(1L);
        expected.setCapacity(100);
        expected.setDescription("Big hall");

        Mockito.when(cinemaHallDao.add(addedCinemaHall)).thenReturn(expected);
        CinemaHall actual = cinemaHallService.add(addedCinemaHall);

        Assertions.assertNotNull(actual, "There was no one cinema hall added to DB");
        Assertions.assertEquals(1L, actual.getId(), "ID doesn't equal");
        Assertions.assertEquals(addedCinemaHall.getCapacity(), actual.getCapacity(), "Capacity doesn't equal");
        Assertions.assertEquals(addedCinemaHall.getDescription(), actual.getDescription(),
                "Description doesn't equal");
    }

    @Test
    void get_successFind_ok() {
        CinemaHall expected = new CinemaHall();
        expected.setId(1L);
        expected.setCapacity(100);
        expected.setDescription("Big hall");

        Mockito.when(cinemaHallDao.get(1L)).thenReturn(Optional.of(expected));

        CinemaHall actual = cinemaHallService.get(1L);
        Assertions.assertNotNull(actual, "There was no one cinema hall added to DB");
        Assertions.assertEquals(expected.getId(), actual.getId(),
                "IDs don't equal. Actual id = " + actual.getId()
                        + " but must be " + expected.getId());
        Assertions.assertEquals(expected.getCapacity(), actual.getCapacity(),
                "Capacity doesn't equal. Actual id = " + actual.getCapacity()
                        + "but must be " + expected.getCapacity());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription(),
                "Description doesn't equal. Actual id = " + actual.getDescription()
                        + " but must be " + expected.getDescription());
    }

    @Test
    void get_noExistCinemaHall_notOk() {
        Mockito.when(cinemaHallDao.get(1L)).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class, () -> cinemaHallService.get(1L),
                "Method should throws exception when no one cinemaHall exists in DB");
    }

    @Test
    void getAll_successFind_ok() {
        List<CinemaHall> cinemaHallsFromDB = new ArrayList<>();

        CinemaHall cinemaHall = new CinemaHall();
        cinemaHall.setId(1L);
        cinemaHall.setCapacity(100);
        cinemaHall.setDescription("Big hall");
        cinemaHallsFromDB.add(cinemaHall);

        cinemaHall.setId(2L);
        cinemaHall.setCapacity(150);
        cinemaHallsFromDB.add(cinemaHall);

        cinemaHall.setId(3L);
        cinemaHall.setCapacity(200);
        cinemaHallsFromDB.add(cinemaHall);

        Mockito.when(cinemaHallDao.getAll()).thenReturn(cinemaHallsFromDB);
        List<CinemaHall> actual = cinemaHallService.getAll();

        Assertions.assertNotNull(actual, "List of cinema halls should be not null");
        Assertions.assertEquals(actual.size(), 3,
                "List has to contain 3 element, but actual " + cinemaHallsFromDB.size());
    }

    @Test
    void getAll_noOneCinemaHallPresentInDB_notOk() {
        Mockito.when(cinemaHallDao.getAll()).thenReturn(Collections.emptyList());
        List<CinemaHall> actual = cinemaHallService.getAll();

        Assertions.assertNotNull(actual, "List of cinema halls should be not null");
        Assertions.assertTrue(actual.isEmpty(), "List is not empty");
    }
}
