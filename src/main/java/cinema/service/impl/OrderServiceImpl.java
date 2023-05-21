package cinema.service.impl;

import cinema.dao.OrderDao;
import cinema.model.Order;
import cinema.model.ShoppingCart;
import cinema.model.User;
import cinema.service.OrderService;
import cinema.service.ShoppingCartService;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);
    private final OrderDao orderDao;
    private final ShoppingCartService shoppingCartService;

    public OrderServiceImpl(OrderDao orderDao, ShoppingCartService shoppingCartService) {
        this.orderDao = orderDao;
        this.shoppingCartService = shoppingCartService;
    }

    @Override
    public Order completeOrder(ShoppingCart shoppingCart) {
        logger.info("CompleteOrder method was called. Params: shoppingCart = {}", shoppingCart);
        Order order = new Order();
        order.setOrderTime(LocalDateTime.now());
        order.setTickets(shoppingCart.getTickets());
        order.setUser(shoppingCart.getUser());
        logger.info("Add order method was called. Params: order = {}", order);
        order = orderDao.add(order);
        logger.info("Successes added order to DB. Params: order = {}", order);
        logger.info("Clear shoppingCart method was called. Params: shoppingCart = {}",
                shoppingCart);
        shoppingCartService.clear(shoppingCart);
        logger.info("Successes cleared shoppingCart. Params: shoppingCart id = {}",
                shoppingCart.getId());
        return order;
    }

    @Override
    public List<Order> getOrdersHistory(User user) {
        logger.info("GetOrdersHistory method was called. Params: user = {}", user);
        return orderDao.getOrdersHistory(user);
    }
}
