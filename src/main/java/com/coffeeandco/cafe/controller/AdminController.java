package com.coffeeandco.cafe.controller;

import com.coffeeandco.cafe.model.*;
import com.coffeeandco.cafe.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final OrderService orderService;

    public AdminController(UserService userService, CategoryService categoryService,
                           ProductService productService, OrderService orderService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<Product> products = productService.getAllProducts();
        List<Category> categories = categoryService.getAllCategories();
        List<Order> orders = orderService.getAllOrders();

        double totalRevenue = orderService.getTotalRevenue();
        long pendingOrders = orderService.getPendingOrdersCount();

        // Best selling products calculation
        Map<Product, Integer> productSales = new HashMap<>();
        for (Order order : orders) {
            if ("COMPLETED".equals(order.getStatus())) {
                for (OrderItem item : order.getItems()) {
                    productSales.put(item.getProduct(), productSales.getOrDefault(item.getProduct(), 0) + item.getQuantity());
                }
            }
        }

        List<Map.Entry<Product, Integer>> bestSellers = productSales.entrySet().stream()
                .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        // Recent Orders
        List<Order> recentOrders = orders.stream()
                .limit(5)
                .collect(Collectors.toList());

        // Charts Data: Category Revenue
        Map<String, Double> categoryRevenue = new HashMap<>();
        for (Order order : orders) {
            if ("COMPLETED".equals(order.getStatus())) {
                for (OrderItem item : order.getItems()) {
                    String catName = item.getProduct().getCategory().getName();
                    categoryRevenue.put(catName, categoryRevenue.getOrDefault(catName, 0.0) + (item.getPrice() * item.getQuantity()));
                }
            }
        }

        // Fill empty categories with 0
        for (Category cat : categories) {
            categoryRevenue.putIfAbsent(cat.getName(), 0.0);
        }

        model.addAttribute("totalProducts", products.size());
        model.addAttribute("totalCategories", categories.size());
        model.addAttribute("totalOrders", orders.size());
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("bestSellers", bestSellers);
        model.addAttribute("recentOrders", recentOrders);
        
        // Pass Chart labels and data as Lists
        model.addAttribute("chartLabels", new ArrayList<>(categoryRevenue.keySet()));
        model.addAttribute("chartData", new ArrayList<>(categoryRevenue.values()));

        return "dashboard";
    }

    // CATEGORY MANAGEMENT
    @GetMapping("/categories")
    public String manageCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("category", new Category());
        return "admin/categories";
    }

    @PostMapping("/categories/add")
    public String addCategory(@ModelAttribute("category") Category category, RedirectAttributes ra) {
        try {
            categoryService.saveCategory(category);
            ra.addFlashAttribute("success", "Category added successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error saving category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/edit")
    public String editCategory(@ModelAttribute("category") Category category, RedirectAttributes ra) {
        try {
            Category existing = categoryService.getCategoryById(category.getId());
            if (existing != null) {
                existing.setName(category.getName());
                existing.setDescription(category.getDescription());
                categoryService.saveCategory(existing);
                ra.addFlashAttribute("success", "Category updated successfully!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error updating category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            categoryService.deleteCategory(id);
            ra.addFlashAttribute("success", "Category deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Cannot delete category. Check if products belong to it.");
        }
        return "redirect:/admin/categories";
    }

    // PRODUCT MANAGEMENT
    @GetMapping("/products")
    public String manageProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("product", new Product());
        return "admin/products";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute("product") Product product,
                             @RequestParam("categoryId") Long categoryId,
                             RedirectAttributes ra) {
        try {
            Category cat = categoryService.getCategoryById(categoryId);
            product.setCategory(cat);
            // Default image if empty
            if (product.getImagePath() == null || product.getImagePath().trim().isEmpty()) {
                product.setImagePath("/images/products/classic_espresso.jpg");
            }
            productService.saveProduct(product);
            ra.addFlashAttribute("success", "Product added successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error adding product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/products/edit")
    public String editProduct(@ModelAttribute("product") Product product,
                              @RequestParam("categoryId") Long categoryId,
                              RedirectAttributes ra) {
        try {
            Product existing = productService.getProductById(product.getId());
            if (existing != null) {
                existing.setName(product.getName());
                existing.setDescription(product.getDescription());
                existing.setPrice(product.getPrice());
                existing.setStock(product.getStock());
                existing.setAvailable(product.isAvailable());
                Category cat = categoryService.getCategoryById(categoryId);
                existing.setCategory(cat);
                if (product.getImagePath() != null && !product.getImagePath().trim().isEmpty()) {
                    existing.setImagePath(product.getImagePath());
                }
                productService.saveProduct(existing);
                ra.addFlashAttribute("success", "Product updated successfully!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error updating product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            productService.deleteProduct(id);
            ra.addFlashAttribute("success", "Product deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error deleting product.");
        }
        return "redirect:/admin/products";
    }

    // ORDER MANAGEMENT
    @GetMapping("/orders")
    public String manageOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/orders";
    }

    @PostMapping("/orders/status")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId,
                                    @RequestParam("status") String status,
                                    RedirectAttributes ra) {
        try {
            orderService.updateOrderStatus(orderId, status);
            ra.addFlashAttribute("success", "Order status updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error updating order status.");
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            orderService.deleteOrder(id);
            ra.addFlashAttribute("success", "Order deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error deleting order.");
        }
        return "redirect:/admin/orders";
    }

    // USER MANAGEMENT
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/edit")
    public String editUserRole(@RequestParam("userId") Long userId,
                               @RequestParam("role") String role,
                               RedirectAttributes ra) {
        try {
            User existing = userService.getUserById(userId);
            if (existing != null) {
                existing.setRole(role);
                userService.save(existing);
                ra.addFlashAttribute("success", "User role updated successfully!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error updating user role.");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            userService.deleteUser(id);
            ra.addFlashAttribute("success", "User deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error deleting user.");
        }
        return "redirect:/admin/users";
    }

    // REPORTS & ANALYTICS
    @GetMapping("/reports")
    public String showReports(Model model) {
        List<Order> orders = orderService.getAllOrders();
        double totalRev = orderService.getTotalRevenue();
        
        // Compute daily sales (grouping by date)
        Map<String, Double> dailySales = new TreeMap<>();
        for (Order order : orders) {
            if ("COMPLETED".equals(order.getStatus())) {
                String date = order.getOrderDate().toLocalDate().toString();
                dailySales.put(date, dailySales.getOrDefault(date, 0.0) + order.getGrandTotal());
            }
        }

        // Product performance (qty sold)
        Map<String, Integer> productPerformance = new HashMap<>();
        for (Order order : orders) {
            if ("COMPLETED".equals(order.getStatus())) {
                for (OrderItem item : order.getItems()) {
                    String pName = item.getProduct().getName();
                    productPerformance.put(pName, productPerformance.getOrDefault(pName, 0) + item.getQuantity());
                }
            }
        }

        model.addAttribute("totalRevenue", totalRev);
        model.addAttribute("ordersCount", orders.size());
        model.addAttribute("dailySalesLabels", new ArrayList<>(dailySales.keySet()));
        model.addAttribute("dailySalesData", new ArrayList<>(dailySales.values()));
        model.addAttribute("productPerformanceLabels", new ArrayList<>(productPerformance.keySet()));
        model.addAttribute("productPerformanceData", new ArrayList<>(productPerformance.values()));

        return "admin/reports";
    }
}
