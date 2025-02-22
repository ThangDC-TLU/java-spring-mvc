package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UploadService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@Controller
public class ProductController {
    public final ProductService productService;
    public final UploadService uploadService;

    public ProductController(ProductService productService, UploadService uploadService) {
        this.productService = productService;
        this.uploadService = uploadService;
    }

    @GetMapping("/admin/product")
    public String getProduct(Model model) {
        List<Product> products = this.productService.fetchProducts();
        model.addAttribute("products", products);
        return "admin/product/show";
    }

    @GetMapping("/admin/product/create")
    public String getCreateProductPage(Model model) {
        model.addAttribute("newProduct", new Product());
        return "admin/product/create";
    }

    @PostMapping("/admin/product/create")
    public String createProductPage(@ModelAttribute("newProduct") @Valid Product pr,
            BindingResult newProductBindingResult,
            @RequestParam("hoidanitFile") MultipartFile file) {

        List<FieldError> errors = newProductBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(">>>" + error.getField() + " - " + error.getDefaultMessage());
        }

        if (newProductBindingResult.hasErrors()) {
            return "admin/product/create";
        }
        String image = this.uploadService.handelSaveUploadFile(file, "product");
        pr.setImage(image);
        this.productService.handelSaveProduct(pr);
        return "redirect:/admin/product";
    }

    @GetMapping("/admin/product/{id}")
    public String getProductDetail(Model model, @PathVariable Long id) {
        Product pr = this.productService.fetchProductById(id).get();
        model.addAttribute("product", pr);
        return "admin/product/detail";
    }

    @GetMapping("/admin/product/update/{id}")
    public String getUpdateProduct(Model model, @PathVariable Long id) {
        Product pr = this.productService.fetchProductById(id).get();
        model.addAttribute("productUpdate", pr);
        return "admin/product/update";
    }

    @PostMapping("/admin/product/update")
    public String updateProduct(Model model, @ModelAttribute("productUpdate") @Valid Product pr,
            BindingResult newProductBindingResult,
            @RequestParam("hoidanitFile") MultipartFile file) {

        if (newProductBindingResult.hasErrors()) {
            // Lấy thông tin sản phẩm cũ từ DB
            Product existingProduct = this.productService.fetchProductById(pr.getId()).orElse(null);
            if (existingProduct != null) {
                pr.setImage(existingProduct.getImage());
            }
            model.addAttribute("productUpdate", pr);
            return "admin/product/update";
        }

        Optional<Product> optionalProduct = this.productService.fetchProductById(pr.getId());

        if (!optionalProduct.isPresent()) {
            throw new RuntimeException("Product not found with ID: " + pr.getId());
        }

        Product currentProduct = optionalProduct.get();
        if (currentProduct != null) {
            currentProduct.setName(pr.getName());
            currentProduct.setPrice(pr.getPrice());
            currentProduct.setDetailDesc(pr.getDetailDesc());
            currentProduct.setFactory(pr.getFactory());
            currentProduct.setShortDesc(pr.getShortDesc());
            currentProduct.setTarget(pr.getTarget());
            currentProduct.setQuantity(pr.getQuantity());

            if (!file.isEmpty()) {
                this.uploadService.handelDeleteFile(currentProduct.getImage());
                String image = this.uploadService.handelSaveUploadFile(file, "product");
                currentProduct.setImage(image);
            }

            this.productService.handelSaveProduct(currentProduct);
        }

        return "redirect:/admin/product";
    }

    @GetMapping("/admin/product/delete/{id}")
    public String getDeleteProductPage(Model model, @PathVariable Long id) {
        Product pr = this.productService.fetchProductById(id).get();
        model.addAttribute("productDelete", pr);
        return "admin/product/delete";
    }

    @PostMapping("/admin/product/delete")
    public String deleteProduct(@ModelAttribute("productDelete") Product pr) {
        this.productService.handelDeleteProduct(pr.getId());
        return "redirect:/admin/product";
    }

}
