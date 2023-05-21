package cinema.service.impl;

import cinema.dao.ShoppingCartDao;
import cinema.dao.TicketDao;
import cinema.model.MovieSession;
import cinema.model.ShoppingCart;
import cinema.model.Ticket;
import cinema.model.User;
import cinema.service.ShoppingCartService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private static final Logger logger = LogManager.getLogger(ShoppingCartServiceImpl.class);
    private final ShoppingCartDao shoppingCartDao;
    private final TicketDao ticketDao;

    public ShoppingCartServiceImpl(ShoppingCartDao shoppingCartDao, TicketDao ticketDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.ticketDao = ticketDao;
    }

    @Override
    public void addSession(MovieSession movieSession, User user) {
        logger.info("Add session method was called. Params: movieSession = {}, user = {}",
                movieSession, user);
        Ticket ticket = new Ticket();
        ticket.setMovieSession(movieSession);
        ticket.setUser(user);
        logger.info("Add ticket method was called. Params: ticket = {}", ticket);
        ticket = ticketDao.add(ticket);
        logger.info("Success add ticket. Params: ticket = {}", ticket);
        ShoppingCart shoppingCart = shoppingCartDao.getByUser(user);
        logger.info("Success find shoppingCart. Params: shoppingCart = {}", shoppingCart);
        shoppingCart.getTickets().add(ticket);
        logger.info("Update shoppingCart method was called. Params: shoppingCart = {}",
                shoppingCart);
        shoppingCartDao.update(shoppingCart);
        logger.info("Success update shoppingCart. Params: shoppingCart = {}", shoppingCart);
    }

    @Override
    public ShoppingCart getByUser(User user) {
        logger.info("Get shoppingCart by user method was called. Params: user = {}", user);
        return shoppingCartDao.getByUser(user);
    }

    @Override
    public void registerNewShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        logger.info("Register new shoppingCart method was called. Params: user = {}", user);
        shoppingCartDao.add(shoppingCart);
        logger.info("Success add shoppingCart. Params: shoppingCart = {}", shoppingCart);
    }

    @Override
    public void clear(ShoppingCart shoppingCart) {
        shoppingCart.setTickets(null);
        logger.info("Clear shoppingCart method was called. Params: shoppingCart = {}",
                shoppingCart);
        shoppingCartDao.update(shoppingCart);
        logger.info("Success update shoppingCart. Params: shoppingCart = {}", shoppingCart);
    }
}
