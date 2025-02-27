package vn.hoidanit.laptopshop.controller.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.ProductService;
import org.springframework.web.bind.annotation.RequestBody;

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

    @PostMapping("/add-product-to-cart/{id}")
    public String addProductToCart(@PathVariable Long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        long productId = id;
        this.productService.handelAddProductToCart((String) session.getAttribute("email"), productId, session);
        return "redirect:/";
    }

    @GetMapping("/cart")
    public String getCartPage(Model model, HttpServletRequest request) {
        User currentUser = new User(); // null
        HttpSession session = request.getSession(false);
        long id = (long) session.getAttribute("id");
        currentUser.setId(id);

        Cart cart = this.productService.fetchCartByUser(currentUser);

        List<CartDetail> cartDetails = cart == null ? new ArrayList<CartDetail>() : cart.getCartDetails();
        double totalPrice = 0;
        for (CartDetail cd : cartDetails) {
            totalPrice += cd.getPrice() * cd.getQuantity();
        }
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("cartDetails", cartDetails);

        return "client/cart/show";
    }

    @PostMapping("/delete-cart-product/{id}")
    public String deleteCartProduct(@PathVariable Long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        long cartDetailId = id;
        this.productService.handelRemoveCartDetail(id, session);
        return "redirect:/cart";
    }

}
