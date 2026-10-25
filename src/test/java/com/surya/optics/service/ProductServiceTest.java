package com.surya.optics.service;

import com.surya.optics.entity.Product;
import com.surya.optics.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    private final ProductRepository repository=mock(ProductRepository.class);
    private final ProductService service=new ProductService(repository);
    private Product product(){Product p=new Product();p.setName("Classic Frame");p.setSku("FR-001");p.setPurchasePrice(new BigDecimal("200"));p.setSellingPrice(new BigDecimal("500"));p.setStockQuantity(8);p.setReorderLevel(3);return p;}
    @Test void savesNewProductWithNormalizedSku(){when(repository.existsBySkuIgnoreCase("FR-001")).thenReturn(false);when(repository.save(any(Product.class))).thenAnswer(i->i.getArgument(0));Product saved=service.save(product());assertEquals("FR-001",saved.getSku());assertEquals("Classic Frame",saved.getName());}
    @Test void rejectsDuplicateSku(){when(repository.existsBySkuIgnoreCase("FR-001")).thenReturn(true);assertThrows(IllegalArgumentException.class,()->service.save(product()));}
    @Test void stockCanBeAdjustedButNeverNegative(){Product p=product();p.setId(2L);when(repository.findByIdForUpdate(2L)).thenReturn(java.util.Optional.of(p));when(repository.save(any(Product.class))).thenAnswer(i->i.getArgument(0));assertEquals(10,service.adjustStock(2L,2).getStockQuantity());assertThrows(IllegalArgumentException.class,()->service.adjustStock(2L,-11));verify(repository,times(2)).findByIdForUpdate(2L);}
    @Test void rejectsNegativePrice(){Product p=product();p.setPurchasePrice(new BigDecimal("-1"));assertThrows(IllegalArgumentException.class,()->service.save(p));}
    @Test void editingProductDoesNotOverwriteLiveStock() {
        Product existing = product();
        existing.setId(2L);
        existing.setStockQuantity(4);
        Product submitted = product();
        submitted.setId(2L);
        submitted.setStockQuantity(99);
        when(repository.existsBySkuIgnoreCaseAndIdNot("FR-001", 2L)).thenReturn(false);
        when(repository.findById(2L)).thenReturn(java.util.Optional.of(existing));
        when(repository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product saved = service.save(submitted);

        assertEquals(4, saved.getStockQuantity(), "Editing descriptive fields must not reset stock");
    }

}
