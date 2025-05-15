package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class PopularProductPersistenceServiceTest {

    @InjectMocks
    private PopularProductPersistenceService persistenceService;

    @Mock
    private DailyTopProductRepository dailyTopProductRepository;

    @Test
    void saveDailyTopProducts_shouldDeleteByDateAndSave() {
        // given
        LocalDate date = LocalDate.of(2025, 5, 15);

        DailyTopProduct product1 = new DailyTopProduct(1L, 1L, 1, 100, date);
        DailyTopProduct product2 = new DailyTopProduct(2L, 2L, 2, 50, date);

        List<DailyTopProduct> products = List.of(product1, product2);

        // when
        persistenceService.saveDailyTopProducts(products, date);

        // then
        verify(dailyTopProductRepository).deleteByDate(date);
        verify(dailyTopProductRepository).saveAll(products);

        verifyNoMoreInteractions(dailyTopProductRepository);
    }
}
