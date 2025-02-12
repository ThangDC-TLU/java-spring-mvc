package vn.hoidanit.laptopshop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.ProductRepository;

@Service
public class ProductService {
    public final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product handelSaveProduct(Product pr) {
        Product laptopProduct = this.productRepository.save(pr);
        System.out.println(laptopProduct);
        return laptopProduct;
    }

    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }
}
