package com.coffeeandco.cafe.service;

import com.coffeeandco.cafe.model.Category;
import com.coffeeandco.cafe.model.Product;
import com.coffeeandco.cafe.model.User;
import com.coffeeandco.cafe.repository.CategoryRepository;
import com.coffeeandco.cafe.repository.ProductRepository;
import com.coffeeandco.cafe.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    public DatabaseSeeder(UserRepository userRepository, CategoryRepository categoryRepository, ProductRepository productRepository, org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // Dynamic column metadata inspector: automatically resolve fallback defaults for custom NOT NULL columns in products
        try {
            java.util.List<java.util.Map<String, Object>> columns = jdbcTemplate.queryForList("SHOW COLUMNS FROM products");
            for (java.util.Map<String, Object> column : columns) {
                String field = (String) column.get("Field");
                String type = (String) column.get("Type");
                String isNull = (String) column.get("Null");
                Object def = column.get("Default");
                String key = (String) column.get("Key");

                if ("NO".equals(isNull) && def == null && !"PRI".equals(key)) {
                    String defaultVal = "0";
                    if (type.toLowerCase().contains("varchar") || type.toLowerCase().contains("text") || type.toLowerCase().contains("char")) {
                        defaultVal = "''";
                    } else if (type.toLowerCase().contains("double") || type.toLowerCase().contains("float") || type.toLowerCase().contains("decimal")) {
                        defaultVal = "0.0";
                    } else if (type.toLowerCase().contains("boolean") || type.toLowerCase().contains("bit")) {
                        defaultVal = "0";
                    }
                    try {
                        jdbcTemplate.execute("ALTER TABLE products ALTER COLUMN " + field + " SET DEFAULT " + defaultVal);
                    } catch (Exception e) {
                        try {
                            jdbcTemplate.execute("ALTER TABLE products MODIFY COLUMN " + field + " " + type + " DEFAULT " + defaultVal);
                        } catch (Exception ex) {
                            // Ignored
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to inspect products table: " + e.getMessage());
        }

        // Dynamic column metadata inspector: automatically resolve fallback defaults for custom NOT NULL columns in orders
        try {
            java.util.List<java.util.Map<String, Object>> columns = jdbcTemplate.queryForList("SHOW COLUMNS FROM orders");
            for (java.util.Map<String, Object> column : columns) {
                String field = (String) column.get("Field");
                String type = (String) column.get("Type");
                String isNull = (String) column.get("Null");
                Object def = column.get("Default");
                String key = (String) column.get("Key");

                if ("NO".equals(isNull) && def == null && !"PRI".equals(key)) {
                    String defaultVal = "0";
                    if (type.toLowerCase().contains("varchar") || type.toLowerCase().contains("text") || type.toLowerCase().contains("char")) {
                        if (field.equalsIgnoreCase("payment_method")) {
                            defaultVal = "'CASH'";
                        } else {
                            defaultVal = "''";
                        }
                    } else if (type.toLowerCase().contains("double") || type.toLowerCase().contains("float") || type.toLowerCase().contains("decimal")) {
                        defaultVal = "0.0";
                    }
                    try {
                        jdbcTemplate.execute("ALTER TABLE orders ALTER COLUMN " + field + " SET DEFAULT " + defaultVal);
                    } catch (Exception e) {
                        try {
                            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN " + field + " " + type + " DEFAULT " + defaultVal);
                        } catch (Exception ex) {
                            // Ignored
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to inspect orders table: " + e.getMessage());
        }

        // 1. Seed Users
        if (userRepository.count() == 0) {
            userRepository.save(new User("Admin Coffee", "admin@coffeeandco.com", "admin", "1234567890", "ADMIN"));
            userRepository.save(new User("John Doe", "customer@coffeeandco.com", "customer", "9876543210", "CUSTOMER"));
            System.out.println("Default users seeded: admin@coffeeandco.com / admin, customer@coffeeandco.com / customer");
        }

        // 2. Seed Categories
        Map<String, Category> categoryMap = new HashMap<>();
        String[][] categories = {
            {"Hot Coffee", "Freshly brewed hot coffee beverages"},
            {"Cold Coffee", "Chilled and blended coffee drinks"},
            {"Signature Drinks", "Our special cafe crafted beverages"},
            {"Tea", "Premium organic hot and cold teas"},
            {"Desserts", "Sweet treats and pastries to pair with your brew"},
            {"Snacks", "Delicious baked goods and savory snacks"},
            {"Sandwiches", "Freshly prepared gourmet sandwiches"},
            {"Pizza", "Freshly baked pizzas with premium toppings"},
            {"Burger", "Delicious burgers with choice of patties"},
            {"Pasta", "Authentic Italian pasta in premium sauces"},
            {"Milkshakes", "Thick and creamy classic milkshakes"},
            {"Fresh Juices", "100% natural freshly squeezed juices"}
        };
        for (String[] cat : categories) {
            Category existingCat = categoryRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(cat[0]))
                .findFirst().orElse(null);
            if (existingCat == null) {
                existingCat = categoryRepository.saveAndFlush(new Category(cat[0], cat[1]));
            }
            categoryMap.put(cat[0], existingCat);
        }
        System.out.println("Categories synced.");

        // 3. Seed/Update Products
        Object[][] productsData = {
            // Hot Coffee
            {"Espresso", "Hot Coffee", "A bold, concentrated shot of our signature custom-roasted coffee beans.", 120.00, 100, "/images/products/classic_espresso.jpg"},
            {"Americano", "Hot Coffee", "Rich espresso shots topped with hot water for a smooth black coffee.", 140.00, 80, "/images/products/american_coffee.jpg"},
            {"Cappuccino", "Hot Coffee", "Espresso, steamed milk, and fluffy milk foam, sweetened with a touch of natural vanilla.", 180.00, 50, "/images/products/vanilla_cappuccino.jpg"},
            {"Latte", "Hot Coffee", "Espresso combined with steamed milk and a light layer of foam.", 190.00, 60, "/images/products/classic_latte.jpg"},
            {"Caramel Latte", "Hot Coffee", "Rich espresso, steamed milk, and sweet caramel syrup, topped with velvety foam.", 220.00, 50, "/images/products/caramel_latte.jpg"},
            {"Mocha", "Hot Coffee", "A chocolatey delight combining smooth espresso, steamed milk, and decadent mocha sauce.", 210.00, 40, "/images/products/mocha_bliss.jpg"},
            
            // Cold Coffee
            {"Iced Latte", "Cold Coffee", "Chilled milk and espresso poured over ice for a refreshing cold coffee.", 210.00, 50, "/images/products/iced_latte.jpg"},
            {"Cold Brew", "Cold Coffee", "Slow-steeped cold brew coffee served black over ice.", 200.00, 50, "/images/products/cold_brew.jpg"},
            {"Hazelnut Cold Brew", "Cold Coffee", "Slow-steeped cold brew coffee infused with rich, nutty hazelnut syrup.", 240.00, 45, "/images/products/hazelnut_cold_brew.jpg"},
            {"Caramel Frappe", "Cold Coffee", "Blended ice, premium espresso, milk, and caramel syrup topped with cream.", 260.00, 40, "/images/products/caramel_frappe.jpg"},
            {"Choco Frappe", "Cold Coffee", "Blended ice, premium espresso, milk, and chocolate syrup topped with sweetened whipped cream.", 240.00, 35, "/images/products/choco_frappe.jpg"},
            
            // Signature Drinks
            {"Coconut Espresso", "Signature Drinks", "A unique refreshing tropical blend of dark espresso poured over sweet coconut milk.", 250.00, 30, "/images/products/coconut_espresso.jpg"},
            {"Irish Coffee", "Signature Drinks", "Fresh hot coffee infused with creamy non-alcoholic Irish cream flavouring, topped with cream.", 280.00, 25, "/images/products/irish_coffee.jpg"},
            
            // Tea
            {"Masala Tea", "Tea", "Traditional Indian spiced tea brewed with milk and aromatic spices.", 60.00, 100, "/images/products/masala_tea.jpg"},
            {"Green Tea", "Tea", "Premium organic steamed green tea leaves.", 80.00, 80, "/images/products/green_tea.jpg"},
            {"Black Tea", "Tea", "Freshly brewed classic black tea.", 70.00, 90, "/images/products/black_tea.jpg"},
            {"Matcha Latte", "Tea", "Pure Japanese ceremonial-grade matcha green tea powder whisked with warm, creamy milk.", 230.00, 40, "/images/products/matcha_latte.jpg"},
            
            // Desserts
            {"Chocolate Brownie", "Desserts", "Warm, fudgy chocolate brownie loaded with Belgian chocolate chunks.", 180.00, 30, "/images/products/chocolate_brownie.jpg"},
            {"Cheesecake", "Desserts", "Classic creamy baked cheesecake with a buttery graham cracker crust.", 260.00, 15, "/images/products/cheesecake.jpg"},
            {"Red Velvet Cake", "Desserts", "Decadent velvet-textured layers with cream cheese frosting.", 240.00, 20, "/images/products/red_velvet_cake.jpg"},
            
            // Snacks
            {"French Fries", "Snacks", "Crispy, golden potato fries seasoned with salt.", 140.00, 50, "/images/products/french_fries.jpg"},
            {"Garlic Bread", "Snacks", "Warm toasted bread spread with garlic butter and herbs.", 170.00, 40, "/images/products/garlic_bread.jpg"},
            {"Butter Croissant", "Snacks", "Golden, flaky, and buttery fresh-baked traditional French pastry.", 160.00, 35, "/images/products/butter_croissant.jpg"},
            
            // Sandwiches
            {"Veg Sandwich", "Sandwiches", "Grated veggies and cheese spread inside toasted bread slice.", 180.00, 40, "/images/products/veg_sandwich.jpg"},
            {"Chicken Sandwich", "Sandwiches", "Grilled chicken breast, lettuce, and mayo inside toasted sandwich bread.", 240.00, 30, "/images/products/chicken_sandwich.jpg"},
            
            // Pizza
            {"Margherita Pizza", "Pizza", "Classic pizza topped with fresh tomato sauce, mozzarella cheese, and basil.", 320.00, 25, "/images/products/margherita_pizza.jpg"},
            {"Veg Supreme Pizza", "Pizza", "Loaded with onions, bell peppers, olives, mushrooms, and sweet corn.", 420.00, 20, "/images/products/veg_supreme_pizza.jpg"},
            {"Chicken Pizza", "Pizza", "Topped with grilled chicken chunks, red onions, bell peppers, and cheese.", 480.00, 15, "/images/products/chicken_pizza.jpg"},
            
            // Burger
            {"Veg Burger", "Burger", "Crispy veg patty, lettuce, tomato, onions, and burger sauce in a soft bun.", 180.00, 35, "/images/products/veg_burger.jpg"},
            {"Chicken Burger", "Burger", "Crispy or grilled chicken patty with lettuce and mayo in a soft bun.", 250.00, 30, "/images/products/chicken_burger.jpg"},
            
            // Pasta
            {"White Sauce Pasta", "Pasta", "Penne pasta tossed in a rich, creamy Alfredo sauce.", 260.00, 25, "/images/products/white_sauce_pasta.jpg"},
            {"Red Sauce Pasta", "Pasta", "Penne pasta cooked in a tangy and spicy tomato basil sauce.", 250.00, 25, "/images/products/red_sauce_pasta.jpg"},
            
            // Milkshakes
            {"Chocolate Shake", "Milkshakes", "Rich chocolate ice cream blended with milk and chocolate syrup.", 220.00, 30, "/images/products/chocolate_shake.jpg"},
            {"Oreo Shake", "Milkshakes", "Creamy vanilla ice cream blended with Oreo cookies and milk.", 240.00, 25, "/images/products/oreo_shake.jpg"},
            {"Strawberry Shake", "Milkshakes", "Sweet strawberries blended with milk and vanilla ice cream.", 230.00, 25, "/images/products/strawberry_shake.jpg"},
            
            // Fresh Juices
            {"Orange Juice", "Fresh Juices", "Freshly squeezed sweet oranges.", 140.00, 35, "/images/products/orange_juice.jpg"},
            {"Watermelon Juice", "Fresh Juices", "Refreshing freshly blended watermelon juice.", 130.00, 40, "/images/products/watermelon_juice.jpg"},
            {"Mango Juice", "Fresh Juices", "Creamy, rich juice made from sweet Alphonso mangoes.", 170.00, 30, "/images/products/mango_juice.jpg"}
        };
        for (Object[] prod : productsData) {
            String name = (String) prod[0];
            String catName = (String) prod[1];
            String desc = (String) prod[2];
            double price = (double) prod[3];
            int stock = (int) prod[4];
            String imgPath = (String) prod[5];
            Category cat = categoryMap.get(catName);
            if (cat != null) {
                java.util.Optional<Product> existingOpt = productRepository.findAll().stream()
                    .filter(p -> p.getName().equalsIgnoreCase(name)
                              || (name.equals("Espresso") && p.getName().equalsIgnoreCase("Classic Espresso"))
                              || (name.equals("Mocha") && p.getName().equalsIgnoreCase("Mocha Bliss"))
                              || (name.equals("Cappuccino") && p.getName().equalsIgnoreCase("Vanilla Cappuccino"))
                              || (name.equals("Green Tea") && p.getName().equalsIgnoreCase("Matcha Latte")))
                    .findFirst();
                if (existingOpt.isPresent()) {
                    Product p = existingOpt.get();
                    p.setName(name);
                    p.setPrice(price);
                    p.setCategory(cat);
                    p.setDescription(desc);
                    p.setImagePath(imgPath);
                    productRepository.saveAndFlush(p);
                } else {
                    productRepository.saveAndFlush(new Product(name, cat, desc, price, stock, true, imgPath));
                }
            }
        }
        System.out.println("Product seed values loaded and synced.");
    }
}
