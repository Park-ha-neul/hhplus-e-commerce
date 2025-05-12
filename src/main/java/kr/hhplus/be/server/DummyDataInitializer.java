package kr.hhplus.be.server;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.TopProduct;
import kr.hhplus.be.server.domain.user.UserPoint;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import kr.hhplus.be.server.domain.user.User;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

@Component
public class DummyDataInitializer implements CommandLineRunner {

    @Autowired
    private SessionFactory sessionFactory;

    public void insertDataUsingStatelessSession() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Product.ProductStatus[] statuses = Product.ProductStatus.values();
        int productStatusCount = statuses.length;

        UserCoupon.UserCouponStatus[] userCouponstatuses = UserCoupon.UserCouponStatus.values();
        int userCouponStatusCount = userCouponstatuses.length;

        TopProduct.PeriodType[] periodTypes = TopProduct.PeriodType.values();
        Random random = new Random();
        LocalDate today = LocalDate.now();

        PointHistory.TransactionType[] transactionTypes = PointHistory.TransactionType.values();

        // user insert
        for (int i = 0; i < 1000; i++) {
            User user = new User("user_" + i, i % 2 == 0);
            session.save(user);
            session.flush();
        }

        // userPoint insert
        for (int i = 0; i<1000; i++){
            UserPoint userPoint = new UserPoint(Long.valueOf(i), 2000L);
            session.save(userPoint);
            session.flush();
        }

        // history insert
        for (int i = 0; i<1000; i++){
            PointHistory.TransactionType transactionType = transactionTypes[i % transactionTypes.length];
            PointHistory pointHistory = new PointHistory(1L, 100L, 2000L, 1900L, transactionType);
            session.save(pointHistory);
            session.flush();
        }

        // product
        for (int i = 0; i<1000; i++){
            Product.ProductStatus status = statuses[i % productStatusCount];
            Product product = new Product(Long.valueOf(i), "상품_" + i, "설명", 200L, 200L, status);
            session.save(product);
            session.flush();
        }

        // order
        for (int i = 0; i<1000; i++){
            Order order = new Order(Long.valueOf(i), Long.valueOf(i));

            session.save(order);

            for (long productId = 1; productId <= 5; productId++) {
                OrderItem orderItem = new OrderItem(order, productId, 5L, 1000L);
                order.addOrderItem(orderItem);

                session.save(orderItem);
            }
            order.complete();
            session.update(order);
            session.flush();
        }

        // userCoupon
        for (int i = 0; i < 10000; i++) {
            UserCoupon.UserCouponStatus status = userCouponstatuses[i % userCouponStatusCount];
            UserCoupon userCoupon = new UserCoupon(1L, Long.valueOf(i+1), status);
            session.save(userCoupon);
            session.flush();
        }

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void run(String... args) throws Exception {
        insertDataUsingStatelessSession(); // 대량 데이터 삽입
    }
}
