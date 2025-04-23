package kr.hhplus.be.server;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.TopProduct;
import kr.hhplus.be.server.domain.user.UserPoint;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import kr.hhplus.be.server.domain.user.User;
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
        StatelessSession session = sessionFactory.openStatelessSession();
        session.beginTransaction();

        Product.ProductStatus[] statuses = Product.ProductStatus.values();
        int productStatusCount = statuses.length;

        UserCoupon.UserCouponStatus[] userCouponstatuses = UserCoupon.UserCouponStatus.values();
        int userCouponStatusCount = userCouponstatuses.length;

        TopProduct.PeriodType[] periodTypes = TopProduct.PeriodType.values();
        Random random = new Random();
        LocalDate today = LocalDate.now();

        // user insert
        for (int i = 0; i < 1000; i++) {
            User user = new User("user_" + i, i % 2 == 0);
            session.insert(user);
        }

        // userPoint insert
        for (int i = 0; i<1000; i++){
            UserPoint userPoint = new UserPoint(Long.valueOf(i), 2000L);
            session.insert(userPoint);
        }

        // product
        for (int i = 0; i<1000; i++){
            Product.ProductStatus status = statuses[i % productStatusCount];
            Product product = new Product(Long.valueOf(i), "상품_" + i, "설명", 200L, 200L, status);
            session.insert(product);
        }

        // order
        for (int i = 0; i<1000; i++){
            Order order = new Order(Long.valueOf(i), Long.valueOf(i));
            session.insert(order);
        }

        // userCoupon
        for (int i = 0; i < 10000; i++) {
            UserCoupon.UserCouponStatus status = userCouponstatuses[i % userCouponStatusCount];
            UserCoupon userCoupon = new UserCoupon(1L, Long.valueOf(i+1), status);
            session.insert(userCoupon);
        }

        // topProduct
        for (int i = 0; i < 10000; i++) {
            TopProduct.PeriodType periodType = periodTypes[i % periodTypes.length];
            Long productId = (long) (i + 1);
            int rank = (i % 10) + 1;
            Long totalCount = (long) random.nextInt(1000); // 예: 0~999 랜덤 수
            LocalDate calculateDate = today.minusDays(random.nextInt(30)); // 최근 30일 중 하루

            TopProduct topProduct = new TopProduct(productId, rank, totalCount, calculateDate, periodType);
            session.insert(topProduct);
        }

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void run(String... args) throws Exception {
        insertDataUsingStatelessSession(); // 대량 데이터 삽입
    }
}
