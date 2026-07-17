package com.coffeeandco.cafe.controller;

import com.coffeeandco.cafe.model.*;
import com.coffeeandco.cafe.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CustomerController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    public CustomerController(ProductService productService, CategoryService categoryService, OrderService orderService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderService = orderService;
    }

    // Initialize cart in session if not present
    private Cart getOrCreateCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    @GetMapping("/shop")
    public String browseProducts(@RequestParam(value = "search", required = false) String search,
                                 @RequestParam(value = "categoryId", required = false) Long categoryId,
                                 HttpSession session,
                                 Model model) {
        List<Product> products;
        if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId);
            model.addAttribute("selectedCategoryId", categoryId);
        } else if (search != null && !search.isEmpty()) {
            products = productService.searchProducts(search);
            model.addAttribute("searchQuery", search);
        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        
        // Cart count for badge
        Cart cart = getOrCreateCart(session);
        model.addAttribute("cartCount", cart.getItemCount());
        
        return "customer/shop";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        Cart cart = getOrCreateCart(session);
        model.addAttribute("cart", cart);
        return "customer/cart";
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            HttpSession session) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return "ERROR: Product not found";
        }
        if (product.getStock() < quantity) {
            return "ERROR: Insufficient stock. Only " + product.getStock() + " left.";
        }

        Cart cart = getOrCreateCart(session);
        cart.addItem(product, quantity);
        return "SUCCESS:" + cart.getItemCount();
    }

    @PostMapping("/cart/update")
    public String updateCartQuantity(@RequestParam("productId") Long productId,
                                     @RequestParam("quantity") int quantity,
                                     HttpSession session) {
        Cart cart = getOrCreateCart(session);
        Product product = productService.getProductById(productId);
        if (product != null && product.getStock() >= quantity) {
            cart.updateQuantity(productId, quantity);
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart/remove/{productId}")
    public String removeFromCart(@PathVariable("productId") Long productId, HttpSession session) {
        Cart cart = getOrCreateCart(session);
        cart.removeItem(productId);
        return "redirect:/cart";
    }

    @GetMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        Cart cart = getOrCreateCart(session);
        cart.clear();
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        Cart cart = getOrCreateCart(session);

        if (cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your cart is empty!");
            return "redirect:/cart";
        }

        try {
            Order order = orderService.placeOrder(user, cart);
            cart.clear();
            return "redirect:/invoice/" + order.getBillNumber();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/orders")
    public String orderHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders);
        return "customer/orders";
    }

    @GetMapping("/invoice/{billNumber}")
    public String viewInvoice(@PathVariable("billNumber") String billNumber, HttpSession session, Model model) {
        Order order = orderService.getOrderByBillNumber(billNumber);
        if (order == null) {
            return "redirect:/orders";
        }
        
        // Security check: Customer can only see their own invoices
        User loggedInUser = (User) session.getAttribute("user");
        if ("CUSTOMER".equals(loggedInUser.getRole()) && !order.getUser().getId().equals(loggedInUser.getId())) {
            return "redirect:/orders";
        }

        model.addAttribute("order", order);
        return "customer/invoice";
    }

    @GetMapping("/product/order/{id}")
    public String orderProduct(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Product product = productService.getProductById(id);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Product not found!");
            return "redirect:/shop";
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("redirectUrl", "/product/order/" + id);
            return "redirect:/login?error=Please login to order.";
        }

        Cart cart = getOrCreateCart(session);
        cart.addItem(product, 1);
        return "redirect:/cart";
    }

    @PostMapping("/cart/add-combo")
    @ResponseBody
    public String addComboToCart(@RequestParam("item1") String item1,
                                 @RequestParam("item2") String item2,
                                 HttpSession session) {
        List<Product> products1 = productService.searchProducts(item1);
        List<Product> products2 = productService.searchProducts(item2);

        Product p1 = null;
        for (Product p : products1) {
            if (p.getName().toLowerCase().contains(item1.toLowerCase())) {
                p1 = p;
                break;
            }
        }
        if (p1 == null && !products1.isEmpty()) p1 = products1.get(0);

        Product p2 = null;
        for (Product p : products2) {
            if (p.getName().toLowerCase().contains(item2.toLowerCase())) {
                p2 = p;
                break;
            }
        }
        if (p2 == null && !products2.isEmpty()) p2 = products2.get(0);

        if (p1 == null || p2 == null) {
            return "ERROR: Combo items not found in database";
        }

        if (p1.getStock() < 1 || p2.getStock() < 1) {
            return "ERROR: Insufficient stock for combo items.";
        }

        Cart cart = getOrCreateCart(session);
        cart.addItem(p1, 1);
        cart.addItem(p2, 1);

        return "SUCCESS:" + cart.getItemCount();
    }
}
