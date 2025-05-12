package kr.hhplus.be.server.infrastructure.product;

import org.springframework.data.repository.query.Param;
import kr.hhplus.be.server.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PopularProductJpaRepository extends JpaRepository<Order, Long> {

    @Query(value = """
                    SELECT
                        oi.product_id AS productId,
                        p.product_name AS productName,
                        p.price AS price,
                        SUM(oi.quantity) AS totalCount,
                        ROW_NUMBER() OVER (ORDER BY SUM(oi.quantity) DESC) AS ranking
                    FROM
                        user_order uo
                    JOIN
                        order_item oi ON uo.order_id = oi.order_id
                    JOIN
                        product p ON oi.product_id = p.product_id
                    WHERE
                        uo.status = 'SUCCESS'
                        AND uo.create_date BETWEEN :startDate AND :endDate
                    GROUP BY
                        oi.product_id, p.product_name, p.price
                    ORDER BY
                        totalCount DESC
                    LIMIT :limit
            """, nativeQuery = true)
    List<PopularProductProjection> findPopularProductsByPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("limit") int limit);
}
