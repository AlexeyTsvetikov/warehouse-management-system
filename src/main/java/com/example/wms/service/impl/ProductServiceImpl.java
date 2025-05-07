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
import com.example.wms.service.ProductService;
import com.example.wms.utils.PaginationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private final CategoryRepository categoryRepository;
    private final ManufacturerRepository manufacturerRepository;

    @Override
    @Transactional
    public ProductInfoResp createProduct(ProductInfoReq req) {
        if (productRepository.findBySku(req.getSku()).isPresent()) {
            throw new CommonBackendException("Product  with sku already exists", HttpStatus.CONFLICT);
        }

        Product product = new Product();
        product.setSku(req.getSku());
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setWeight(req.getWeight());
        product.setDimensions(req.getDimensions());
        product.setIsActive(true);

        if (req.getCategoryName() != null) {
            Category category = categoryRepository.findByNameAndIsActiveTrue(req.getCategoryName())
                    .orElseThrow(() -> new CommonBackendException(
                            "Category with name : " + req.getCategoryName() + " not found", HttpStatus.NOT_FOUND));
            product.setCategory(category);
        } else {
            throw new CommonBackendException("Category must be provided", HttpStatus.BAD_REQUEST);
        }

        if (req.getManufacturerName() != null) {
            Manufacturer manufacturer = manufacturerRepository.findByNameAndIsActiveTrue(req.getManufacturerName())
                    .orElseThrow(() -> new CommonBackendException(
                            "Manufacturer with name : " + req.getManufacturerName() + " not found", HttpStatus.NOT_FOUND));
            product.setManufacturer(manufacturer);
        } else {
            throw new CommonBackendException("Manufacturer must be provided", HttpStatus.BAD_REQUEST);
        }

        Product savedProduct = productRepository.save(product);

        ProductInfoResp resp = objectMapper.convertValue(savedProduct, ProductInfoResp.class);
        resp.setManufacturerName(savedProduct.getManufacturer().getName());
        resp.setCategoryName(savedProduct.getCategory().getName());
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductInfoResp getProduct(Long id) {
        final String errMsg = String.format("Product  with id: %s not found", id);

        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        ProductInfoResp resp = objectMapper.convertValue(product, ProductInfoResp.class);
        resp.setManufacturerName(product.getManufacturer().getName());
        resp.setCategoryName(product.getCategory().getName());
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductInfoResp> getAllProducts(Integer page, Integer perPage, String sort, Sort.Direction order, String filter) {

        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Product> products;

        if(StringUtils.hasText(filter)) {
            products = productRepository.findAllFiltered(pageRequest, filter);
        } else {
            products = productRepository.findAllByIsActiveTrue(pageRequest);
        }

        List<ProductInfoResp> content = products.getContent().stream()
                .map(product -> {
                    ProductInfoResp resp = objectMapper.convertValue(product, ProductInfoResp.class);
                    resp.setManufacturerName(product.getManufacturer().getName());
                    resp.setCategoryName(product.getCategory().getName());
                    return resp;
                }).collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, products.getTotalElements());
    }

    @Override
    @Transactional
    public ProductInfoResp updateProduct(Long id, ProductInfoReq req) {
        final String errMsg = String.format("Product  with id: %s not found", id);

        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        product.setSku(req.getSku() != null ? req.getSku() : product.getSku());
        product.setName(req.getName() != null ? req.getName() : product.getName());
        product.setDescription(req.getDescription() != null ? req.getDescription() : product.getDescription());
        product.setWeight(req.getWeight() != null ? req.getWeight() : product.getWeight());
        product.setDimensions(req.getDimensions() != null ? req.getDimensions() : product.getDimensions());

        if (req.getCategoryName() != null) {
            Category category = categoryRepository.findByNameAndIsActiveTrue(req.getCategoryName())
                    .orElseThrow(() -> new CommonBackendException(
                            "Category with name : " + req.getCategoryName() + " not found", HttpStatus.NOT_FOUND));
            product.setCategory(category);
        }

        if (req.getManufacturerName() != null) {
            Manufacturer manufacturer = manufacturerRepository.findByNameAndIsActiveTrue(req.getManufacturerName())
                    .orElseThrow(() -> new CommonBackendException(
                            "Manufacturer with name : " + req.getManufacturerName() + " not found", HttpStatus.NOT_FOUND));
            product.setManufacturer(manufacturer);
        }

        Product updatedProduct = productRepository.save(product);
        ProductInfoResp resp = objectMapper.convertValue(updatedProduct, ProductInfoResp.class);
        resp.setManufacturerName(updatedProduct.getManufacturer().getName());
        resp.setCategoryName(updatedProduct.getCategory().getName());
        return resp;

    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        final String errMsg = String.format("Product  with id: %s not found", id);

        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));
        product.setIsActive(false);
        productRepository.save(product);
    }

}
