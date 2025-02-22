package vn.hoidanit.laptopshop.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.service.ProductService;

@Controller
public class ItemController {
    public final ProductService productService;

    public ItemController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product/{id}")
    public String getProductDetail(Model model, @PathVariable Long id) {
        Product pr = this.productService.fetchProductById(id).get();
        System.out.println(pr);
        model.addAttribute("product", pr);
        model.addAttribute("id", id);
        return "client/product/detail";
    }

}
