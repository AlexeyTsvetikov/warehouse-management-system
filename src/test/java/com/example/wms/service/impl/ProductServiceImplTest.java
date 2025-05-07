package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Category;
import com.example.wms.model.db.entity.Manufacturer;
import com.example.wms.model.db.entity.Product;
import com.example.wms.model.db.repository.CategoryRepository;
import com.example.wms.model.db.repository.ManufacturerRepository;
import com.example.wms.model.db.repository.ProductRepository;
import com.example.wms.model.dto.request.ProductInfoReq;
import com.example.wms.model.dto.response.ProductInfoResp;
import com.example.wms.utils.PaginationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ManufacturerRepository manufacturerRepository;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    void createProduct() {
        Category category = new Category();
        category.setId(1L);
        category.setName("TestCategory");

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1L);
        manufacturer.setName("TestManufacturer");

        ProductInfoReq req = new ProductInfoReq();
        req.setSku("SKU123");
        req.setName("Test Product");
        req.setCategoryName(category.getName());
        req.setManufacturerName(manufacturer.getName());
        req.setDescription("TestDescription");
        req.setDimensions("TestDimensions");
        req.setWeight(BigDecimal.valueOf(100));

        Product product = new Product();
        product.setSku(req.getSku());
        product.setName(req.getName());
        product.setCategory(category);
        product.setManufacturer(manufacturer);
        product.setDescription(req.getDescription());
        product.setDimensions(req.getDimensions());
        product.setWeight(req.getWeight());

        when(productRepository.findBySku(req.getSku())).thenReturn(Optional.empty());

        when(categoryRepository.findByNameAndIsActiveTrue(req.getCategoryName())).thenReturn(Optional.of(category));

        when(manufacturerRepository.findByNameAndIsActiveTrue(req.getManufacturerName())).thenReturn(Optional.of(manufacturer));

        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductInfoResp productInfoResp = productService.createProduct(req);

        assertEquals(product.getSku(), productInfoResp.getSku());
        assertEquals(product.getName(), productInfoResp.getName());
        assertEquals(product.getCategory().getName(), productInfoResp.getCategoryName());
        assertEquals(product.getManufacturer().getName(), productInfoResp.getManufacturerName());
        assertEquals(product.getDescription(), productInfoResp.getDescription());
        assertEquals(product.getDimensions(), productInfoResp.getDimensions());
        assertEquals(product.getWeight(), productInfoResp.getWeight());
    }

    @Test
    void createProductExists() {
        ProductInfoReq req = new ProductInfoReq();
        req.setSku("SKU123");

        Product existingproduct = new Product();
        existingproduct.setSku("SKU123");

        when(productRepository.findBySku(req.getSku())).thenReturn(Optional.of(existingproduct));

        assertThrows(CommonBackendException.class, () -> productService.createProduct(req));
    }

    @Test
    void createProductNotFoundCategory() {
        ProductInfoReq req = new ProductInfoReq();
        req.setCategoryName("NoCategoryName");

        when(categoryRepository.findByNameAndIsActiveTrue(req.getCategoryName())).thenReturn(Optional.empty());

        assertThrows(CommonBackendException.class, () -> productService.createProduct(req));
    }

    @Test
    void createProductNullCategory() {
        ProductInfoReq req = new ProductInfoReq();
        req.setCategoryName(null);

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                productService.createProduct(req));
        assertEquals("Category must be provided", exception.getMessage());
    }

    @Test
    void createProductNotFoundManufacturer() {
        Category category = new Category();
        category.setName("TestCategory");

        ProductInfoReq req = new ProductInfoReq();
        req.setCategoryName("TestCategory");
        req.setManufacturerName("NoManufacturerName");

        when(categoryRepository.findByNameAndIsActiveTrue(req.getCategoryName())).thenReturn(Optional.of(category));

        when(manufacturerRepository.findByNameAndIsActiveTrue(req.getManufacturerName())).thenReturn(Optional.empty());

        assertThrows(CommonBackendException.class, () -> productService.createProduct(req));
    }

    @Test
    void createProductNullManufacturer() {
        Category category = new Category();
        category.setName("TestCategory");

        ProductInfoReq req = new ProductInfoReq();
        req.setManufacturerName(null);
        req.setCategoryName("TestCategory");

        when(categoryRepository.findByNameAndIsActiveTrue(req.getCategoryName())).thenReturn(Optional.of(category));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                productService.createProduct(req));
        assertEquals("Manufacturer must be provided", exception.getMessage());
    }

    @Test
    void getProduct() {
        Category category = new Category();
        category.setName("TestCategory");

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("TestManufacturer");

        Product product = new Product();
        product.setSku("SKU123");
        product.setName("Test Product");
        product.setCategory(category);
        product.setManufacturer(manufacturer);
        product.setDescription("TestDescription");
        product.setDimensions("TestDimensions");
        product.setWeight(BigDecimal.valueOf(100));
        product.setIsActive(true);

        when(productRepository.findByIdAndIsActiveTrue(product.getId())).thenReturn(Optional.of(product));

        ProductInfoResp productInfoResp = productService.getProduct(product.getId());
        assertEquals(product.getSku(), productInfoResp.getSku());
        assertEquals(product.getName(), productInfoResp.getName());
        assertEquals(product.getCategory().getName(), productInfoResp.getCategoryName());
        assertEquals(product.getManufacturer().getName(), productInfoResp.getManufacturerName());
        assertEquals(product.getDescription(), productInfoResp.getDescription());
        assertEquals(product.getDimensions(), productInfoResp.getDimensions());
        assertEquals(product.getWeight(), productInfoResp.getWeight());
    }

    @Test
    void getProductNotFound() {
        Long nonExistingId = 999L;
        assertProductNotFound(nonExistingId);
    }


    @Test
    void getAllProductsWithFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;
        String filter = "active";

        Category category = new Category();
        category.setName("TestCategory");

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("TestManufacturer");

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Product product = new Product();
        product.setCategory(category);
        product.setManufacturer(manufacturer);

        ProductInfoResp productInfoResp = new ProductInfoResp();

        when(productRepository.findAllFiltered(pageable, filter)).thenReturn(new PageImpl<>(List.of(product)));

        when(objectMapper.convertValue(product, ProductInfoResp.class)).thenReturn(productInfoResp);

        Page<ProductInfoResp> result = productService.getAllProducts(pageNumber, pageSize, sortField, sortDirection, filter);

        assertEquals(1, result.getContent().size());
        assertEquals(productInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllProductsWithoutFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        Category category = new Category();
        category.setName("TestCategory");

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("TestManufacturer");

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Product product = new Product();
        product.setCategory(category);
        product.setManufacturer(manufacturer);
        product.setIsActive(true);

        ProductInfoResp productInfoResp = new ProductInfoResp();

        when(productRepository.findAllByIsActiveTrue(pageable)).thenReturn(new PageImpl<>(List.of(product)));

        when(objectMapper.convertValue(product, ProductInfoResp.class)).thenReturn(productInfoResp);

        Page<ProductInfoResp> result = productService.getAllProducts(pageNumber, pageSize, sortField, sortDirection, null);

        assertEquals(1, result.getContent().size());
        assertEquals(productInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateProductAllValues() {
        Category oldCategory = new Category();
        oldCategory.setId(1L);
        oldCategory.setName("OldCategory");

        Manufacturer oldManufacturer = new Manufacturer();
        oldManufacturer.setId(1L);
        oldManufacturer.setName("OldManufacturer");

        Category newCategory = new Category();
        newCategory.setId(2L);
        newCategory.setName("NewCategory");

        Manufacturer newManufacturer = new Manufacturer();
        newManufacturer.setId(2L);
        newManufacturer.setName("NewManufacturer");

        Product product = new Product();
        product.setId(1L);
        product.setSku("oldSku");
        product.setName("oldName");
        product.setDescription("oldDescription");
        product.setWeight(BigDecimal.valueOf(10));
        product.setDimensions("oldDimensions");
        product.setCategory(oldCategory);
        product.setManufacturer(oldManufacturer);
        product.setIsActive(true);

        ProductInfoReq req = new ProductInfoReq();
        req.setSku("newSku");
        req.setName("newName");
        req.setDescription("newDescription");
        req.setWeight(BigDecimal.valueOf(20));
        req.setDimensions("newDimensions");
        req.setCategoryName(newCategory.getName());
        req.setManufacturerName(newManufacturer.getName());

        when(productRepository.findByIdAndIsActiveTrue(product.getId())).thenReturn(Optional.of(product));

        when(categoryRepository.findByNameAndIsActiveTrue(req.getCategoryName())).thenReturn(Optional.of(newCategory));

        when(manufacturerRepository.findByNameAndIsActiveTrue(req.getManufacturerName())).thenReturn(Optional.of(newManufacturer));

        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductInfoResp expectedResponse = new ProductInfoResp();
        expectedResponse.setId(product.getId());
        expectedResponse.setSku(req.getSku());
        expectedResponse.setName(req.getName());
        expectedResponse.setDescription(req.getDescription());
        expectedResponse.setWeight(req.getWeight());
        expectedResponse.setDimensions(req.getDimensions());
        expectedResponse.setCategoryName(newCategory.getName());
        expectedResponse.setManufacturerName(newManufacturer.getName());

        ProductInfoResp resp = productService.updateProduct(product.getId(), req);
        assertEquals(expectedResponse.getId(), resp.getId());
        assertEquals(expectedResponse.getSku(), resp.getSku());
        assertEquals(expectedResponse.getName(), resp.getName());
        assertEquals(expectedResponse.getDescription(), resp.getDescription());
        assertEquals(expectedResponse.getWeight(), resp.getWeight());
        assertEquals(expectedResponse.getDimensions(), resp.getDimensions());
        assertEquals(expectedResponse.getCategoryName(), resp.getCategoryName());
        assertEquals(expectedResponse.getManufacturerName(), resp.getManufacturerName());
    }

    @Test
    void updateProductNullValues() {
        Category oldCategory = new Category();
        oldCategory.setId(1L);
        oldCategory.setName("OldCategory");

        Manufacturer oldManufacturer = new Manufacturer();
        oldManufacturer.setId(1L);
        oldManufacturer.setName("OldManufacturer");

        Product product = new Product();
        product.setId(1L);
        product.setSku("oldSku");
        product.setName("oldName");
        product.setDescription("oldDescription");
        product.setWeight(BigDecimal.valueOf(10));
        product.setDimensions("oldDimensions");
        product.setCategory(oldCategory);
        product.setManufacturer(oldManufacturer);
        product.setIsActive(true);

        ProductInfoReq req = new ProductInfoReq();
        req.setSku(null);
        req.setName(null);
        req.setDescription(null);
        req.setWeight(null);
        req.setDimensions(null);
        req.setCategoryName(null);
        req.setManufacturerName(null);

        when(productRepository.findByIdAndIsActiveTrue(product.getId())).thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductInfoResp expectedResponse = new ProductInfoResp();
        expectedResponse.setId(product.getId());
        expectedResponse.setSku(product.getSku());
        expectedResponse.setName(product.getName());
        expectedResponse.setDescription(product.getDescription());
        expectedResponse.setWeight(product.getWeight());
        expectedResponse.setDimensions(product.getDimensions());
        expectedResponse.setCategoryName(product.getCategory().getName());
        expectedResponse.setManufacturerName(product.getManufacturer().getName());

        ProductInfoResp resp = productService.updateProduct(product.getId(), req);

        assertEquals(expectedResponse.getId(), resp.getId());
        assertEquals(expectedResponse.getSku(), resp.getSku());
        assertEquals(expectedResponse.getName(), resp.getName());
        assertEquals(expectedResponse.getDescription(), resp.getDescription());
        assertEquals(expectedResponse.getWeight(), resp.getWeight());
        assertEquals(expectedResponse.getDimensions(), resp.getDimensions());
        assertEquals(expectedResponse.getCategoryName(), resp.getCategoryName());
        assertEquals(expectedResponse.getManufacturerName(), resp.getManufacturerName());
    }

    @Test
    void updateProductCategoryNotFound() {
        Category oldCategory = new Category();
        oldCategory.setId(1L);
        oldCategory.setName("OldCategory");

        Manufacturer oldManufacturer = new Manufacturer();
        oldManufacturer.setId(1L);
        oldManufacturer.setName("OldManufacturer");

        Product product = new Product();
        product.setId(1L);
        product.setSku("oldSku");
        product.setName("oldName");
        product.setDescription("oldDescription");
        product.setWeight(BigDecimal.valueOf(10));
        product.setDimensions("oldDimensions");
        product.setCategory(oldCategory);
        product.setManufacturer(oldManufacturer);
        product.setIsActive(true);

        ProductInfoReq req = new ProductInfoReq();
        req.setSku(null);
        req.setName(null);
        req.setDescription(null);
        req.setWeight(null);
        req.setDimensions(null);
        req.setCategoryName("CategoryNotFound");
        req.setManufacturerName(null);

        when(productRepository.findByIdAndIsActiveTrue(product.getId())).thenReturn(Optional.of(product));
        when(categoryRepository.findByNameAndIsActiveTrue(req.getCategoryName())).thenReturn(Optional.empty());

        CommonBackendException ex = assertThrows(CommonBackendException.class, () ->
                productService.updateProduct(product.getId(), req));
        assertEquals("Category with name : " + req.getCategoryName() + " not found", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void updateProductManufacturerNotFound() {
        Category oldCategory = new Category();
        oldCategory.setId(1L);
        oldCategory.setName("OldCategory");

        Manufacturer oldManufacturer = new Manufacturer();
        oldManufacturer.setId(1L);
        oldManufacturer.setName("OldManufacturer");

        Product product = new Product();
        product.setId(1L);
        product.setSku("oldSku");
        product.setName("oldName");
        product.setDescription("oldDescription");
        product.setWeight(BigDecimal.valueOf(10));
        product.setDimensions("oldDimensions");
        product.setCategory(oldCategory);
        product.setManufacturer(oldManufacturer);
        product.setIsActive(true);

        ProductInfoReq req = new ProductInfoReq();
        req.setSku(null);
        req.setName(null);
        req.setDescription(null);
        req.setWeight(null);
        req.setDimensions(null);
        req.setCategoryName(null);
        req.setManufacturerName("CategoryNotFound");

        when(productRepository.findByIdAndIsActiveTrue(product.getId())).thenReturn(Optional.of(product));
        when(manufacturerRepository.findByNameAndIsActiveTrue(req.getManufacturerName())).thenReturn(Optional.empty());

        CommonBackendException ex = assertThrows(CommonBackendException.class, () ->
                productService.updateProduct(product.getId(), req));
        assertEquals("Manufacturer with name : " + req.getManufacturerName() + " not found", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void deleteProduct() {
        Product product = new Product();
        product.setId(1L);

        when(productRepository.findByIdAndIsActiveTrue(product.getId())).thenReturn(Optional.of(product));

        productService.deleteProduct(product.getId());

        verify(productRepository, times(1)).save(any(Product.class));

        assertEquals(false, product.getIsActive());
    }

    @Test
    void deleteProductNotFound() {
        Long nonExistingId = 999L;
        assertProductNotFound(nonExistingId);
    }

    private void assertProductNotFound(Long productId) {
        when(productRepository.findByIdAndIsActiveTrue(productId)).thenReturn(Optional.empty());
        assertThrows(CommonBackendException.class, () -> productService.getProduct(productId));
    }
}