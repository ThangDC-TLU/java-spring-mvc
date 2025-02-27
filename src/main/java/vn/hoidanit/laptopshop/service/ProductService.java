package vn.hoidanit.laptopshop.service;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final UserService userService;

    public ProductService(ProductRepository productRepository, CartRepository cartRepository,
            CartDetailRepository cartDetailRepository, UserService userService) {
        this.productRepository = productRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.cartRepository = cartRepository;
        this.userService = userService;
    }

    public Product handelSaveProduct(Product pr) {
        Product laptopProduct = this.productRepository.save(pr);
        return laptopProduct;
    }

    public List<Product> fetchProducts() {
        return this.productRepository.findAll();
    }

    public Optional<Product> fetchProductById(Long id) {
        return this.productRepository.findById(id);
    }

    public void handelDeleteProduct(Long id) {
        this.productRepository.deleteById(id);
    }

    public void handelAddProductToCart(String email, long productId, HttpSession session) {
        User user = this.userService.getUserByEmail(email);
        if (user != null) {
            // check user đã có Cart chưa ? nếu chưa -> tạo mới
            Cart cart = this.cartRepository.findByUser(user);

            if (cart == null) {
                // Tạo mới cart cho user
                Cart ortherCart = new Cart();
                ortherCart.setUser(user);
                ortherCart.setSum(0);
                // Lưu cart vào csdl
                cart = this.cartRepository.save(ortherCart);
            }

            // save cart_detail
            // tìm product by id
            Optional<Product> producOptional = this.productRepository.findById(productId);
            if (producOptional != null) {
                // Lưu product và cart_id vào cartDetail
                Product realProduct = producOptional.get();
                CartDetail oldCartDetail = this.cartDetailRepository.findByCartAndProduct(cart, realProduct);
                if (oldCartDetail == null) {
                    CartDetail cartDetail = new CartDetail();
                    cartDetail.setCart(cart);
                    cartDetail.setProduct(realProduct);
                    cartDetail.setPrice(realProduct.getPrice());
                    cartDetail.setQuantity(1);
                    this.cartDetailRepository.save(cartDetail);

                    // update cart (sum)
                    int s = cart.getSum() + 1;
                    cart.setSum(s);
                    this.cartRepository.save(cart);
                    session.setAttribute("sum", s);

                } else {
                    oldCartDetail.setQuantity(oldCartDetail.getQuantity() + 1);
                    this.cartDetailRepository.save(oldCartDetail);
                }

            }

        }

    }

    public Cart fetchCartByUser(User user) {
        return this.cartRepository.findByUser(user);
    }

    public void handelRemoveCartDetail(Long cartDetailId, HttpSession session) {
        Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(cartDetailId);
        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();
            Cart currentCart = cartDetail.getCart();

            // delete cartDetail
            this.cartDetailRepository.deleteById(cartDetailId);

            if (currentCart.getSum() > 1) {
                int s = currentCart.getSum() - 1;
                currentCart.setSum(s);
            } else {
                this.cartRepository.deleteById(currentCart.getId());
                session.setAttribute("sum", 0);
            }

        }
    }
}
