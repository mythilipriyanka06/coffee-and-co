package com.coffeeandco.cafe.service;

import com.coffeeandco.cafe.model.Category;
import com.coffeeandco.cafe.model.Product;
import com.coffeeandco.cafe.model.User;
import com.coffeeandco.cafe.repository.CategoryRepository;
import com.coffeeandco.cafe.repository.ProductRepository;
import com.coffeeandco.cafe.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DatabaseSeeder(UserRepository userRepository, CategoryRepository categoryRepository,
                          ProductRepository productRepository,
                          org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {

        // ── Dynamic column metadata inspector: fix NOT NULL columns in products ──
        try {
            List<Map<String, Object>> columns = jdbcTemplate.queryForList("SHOW COLUMNS FROM products");
            for (Map<String, Object> column : columns) {
                String field  = (String) column.get("Field");
                String type   = (String) column.get("Type");
                String isNull = (String) column.get("Null");
                Object def    = column.get("Default");
                String key    = (String) column.get("Key");
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
                        } catch (Exception ex) { /* ignored */ }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to inspect products table: " + e.getMessage());
        }

        // ── Dynamic column metadata inspector: fix NOT NULL columns in orders ──
        try {
            List<Map<String, Object>> columns = jdbcTemplate.queryForList("SHOW COLUMNS FROM orders");
            for (Map<String, Object> column : columns) {
                String field  = (String) column.get("Field");
                String type   = (String) column.get("Type");
                String isNull = (String) column.get("Null");
                Object def    = column.get("Default");
                String key    = (String) column.get("Key");
                if ("NO".equals(isNull) && def == null && !"PRI".equals(key)) {
                    String defaultVal = "0";
                    if (type.toLowerCase().contains("varchar") || type.toLowerCase().contains("text") || type.toLowerCase().contains("char")) {
                        defaultVal = field.equalsIgnoreCase("payment_method") ? "'CASH'" : "''";
                    } else if (type.toLowerCase().contains("double") || type.toLowerCase().contains("float") || type.toLowerCase().contains("decimal")) {
                        defaultVal = "0.0";
                    }
                    try {
                        jdbcTemplate.execute("ALTER TABLE orders ALTER COLUMN " + field + " SET DEFAULT " + defaultVal);
                    } catch (Exception e) {
                        try {
                            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN " + field + " " + type + " DEFAULT " + defaultVal);
                        } catch (Exception ex) { /* ignored */ }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to inspect orders table: " + e.getMessage());
        }

        // ── 1. Seed Users (BCrypt hashed passwords) ──
        if (userRepository.count() == 0) {
            userRepository.save(new User("Admin Coffee", "admin@coffeeandco.com", passwordEncoder.encode("admin"), "1234567890", "ADMIN"));
            userRepository.save(new User("John Doe", "customer@coffeeandco.com", passwordEncoder.encode("customer"), "9876543210", "CUSTOMER"));
            System.out.println("Default users seeded: admin@coffeeandco.com / admin, customer@coffeeandco.com / customer");
        } else {
            // Upgrade any existing plain-text passwords to BCrypt
            List<User> allUsers = userRepository.findAll();
            for (User u : allUsers) {
                String pw = u.getPassword();
                if (pw != null && !pw.startsWith("$2")) {
                    u.setPassword(passwordEncoder.encode(pw));
                    userRepository.save(u);
                    System.out.println("Upgraded password for user: " + u.getEmail());
                }
            }
        }

        // ── 2. Seed Categories ──
        Map<String, Category> catMap = new HashMap<>();
        String[][] categories = {
            {"Hot Coffee",        "Freshly brewed hot coffee beverages"},
            {"Cold Coffee",       "Chilled and blended coffee drinks"},
            {"Signature Drinks",  "Our special cafe crafted beverages"},
            {"Tea",               "Premium organic hot and cold teas"},
            {"Desserts",          "Sweet treats and pastries to pair with your brew"},
            {"Snacks",            "Delicious baked goods and savory snacks"},
            {"Sandwiches",        "Freshly prepared gourmet sandwiches"},
            {"Pizza",             "Freshly baked pizzas with premium toppings"},
            {"Burger",            "Delicious burgers with choice of patties"},
            {"Pasta",             "Authentic Italian pasta in premium sauces"},
            {"Milkshakes",        "Thick and creamy classic milkshakes"},
            {"Fresh Juices",      "100% natural freshly squeezed juices"},
            {"Ice Creams",        "Creamy handcrafted ice creams and frozen desserts"},
            {"Bakery",            "Freshly baked breads, pastries, and treats"}
        };
        for (String[] cat : categories) {
            final String catName = cat[0];
            Category existing = categoryRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(catName))
                .findFirst().orElse(null);
            if (existing == null) {
                existing = categoryRepository.saveAndFlush(new Category(cat[0], cat[1]));
            }
            catMap.put(cat[0], existing);
        }
        System.out.println("Categories synced: " + catMap.size() + " categories.");

        // ── 3. Seed / Update Products ──
        // Format: { name, category, description, price, stock, imagePath }
        Object[][] products = {

            // ════════════════════════════════════════
            // ☕  HOT COFFEE  (13 items)
            // ════════════════════════════════════════
            {"Espresso",                    "Hot Coffee", "A bold, concentrated shot of our signature custom-roasted coffee beans.", 120.00, 100, "/images/products/classic_espresso.jpg"},
            {"Double Espresso",             "Hot Coffee", "Two shots of rich espresso for a stronger, bolder coffee experience.", 150.00, 80,  "/images/products/double_espresso.jpg"},
            {"Americano",                   "Hot Coffee", "Rich espresso shots topped with hot water for a smooth black coffee.", 140.00, 90,  "/images/products/american_coffee.jpg"},
            {"Cappuccino",                  "Hot Coffee", "Espresso, steamed milk, and fluffy milk foam – a café classic.", 180.00, 70,  "/images/products/vanilla_cappuccino.jpg"},
            {"Latte",                       "Hot Coffee", "Espresso combined with steamed milk and a light layer of velvety foam.", 190.00, 70,  "/images/products/classic_latte.jpg"},
            {"Caramel Latte",               "Hot Coffee", "Rich espresso, steamed milk, and sweet caramel syrup, topped with velvety foam.", 220.00, 60,  "/images/products/caramel_latte.jpg"},
            {"Mocha",                       "Hot Coffee", "A chocolatey delight combining smooth espresso, steamed milk, and decadent mocha sauce.", 220.00, 55,  "/images/products/mocha_bliss.jpg"},
            {"Flat White",                  "Hot Coffee", "Velvety microfoam espresso drink with a bold coffee-to-milk ratio.", 200.00, 50,  "/images/products/flat_white.jpg"},
            {"Hazelnut Latte",              "Hot Coffee", "Espresso with creamy steamed milk and a rich nutty hazelnut syrup swirl.", 230.00, 50,  "/images/products/hazelnut_latte.jpg"},
            {"Vanilla Latte",               "Hot Coffee", "Smooth espresso with steamed milk and sweet vanilla syrup.", 220.00, 60,  "/images/products/vanilla_latte.jpg"},
            {"Irish Coffee",                "Hot Coffee", "Hot coffee with a rich non-alcoholic Irish cream flavour, topped with cream.", 260.00, 35,  "/images/products/irish_coffee.jpg"},
            {"Filter Coffee",               "Hot Coffee", "Classic South Indian drip filter coffee, strong and aromatic.", 90.00,  100, "/images/products/filter_coffee.jpg"},
            {"South Indian Degree Coffee",  "Hot Coffee", "Traditional frothy South Indian coffee made with chicory-blend decoction and warm milk.", 110.00, 90,  "/images/products/south_indian_coffee.jpg"},

            // ════════════════════════════════════════
            // 🧊  COLD COFFEE  (10 items)
            // ════════════════════════════════════════
            {"Classic Cold Coffee",         "Cold Coffee", "Chilled milk blended with espresso for a refreshing iced coffee.", 180.00, 70,  "/images/products/cold_coffee.jpg"},
            {"Chocolate Cold Coffee",       "Cold Coffee", "Iced espresso blended with rich chocolate milk and cocoa syrup.", 240.00, 55,  "/images/products/cold_mocha.jpg"},
            {"Iced Americano",              "Cold Coffee", "Espresso shots poured over ice with cold water – clean and refreshing.", 170.00, 65,  "/images/products/iced_americano.jpg"},
            {"Iced Latte",                  "Cold Coffee", "Chilled milk and espresso poured over ice for a smooth cold coffee.", 200.00, 60,  "/images/products/iced_latte.jpg"},
            {"Caramel Frappe",              "Cold Coffee", "Blended ice, premium espresso, milk, and caramel syrup topped with cream.", 240.00, 50,  "/images/products/caramel_frappe.jpg"},
            {"Chocolate Frappe",            "Cold Coffee", "Espresso blended with ice, milk, and rich chocolate sauce, topped with cream.", 250.00, 45,  "/images/products/chocolate_frappe.jpg"},
            {"Oreo Frappe",                 "Cold Coffee", "Creamy Oreo cookie frappe blended with espresso and milk.", 260.00, 40,  "/images/products/oreo_frappe.jpg"},
            {"KitKat Frappe",               "Cold Coffee", "Smooth frappe loaded with KitKat flavour, espresso, and cream.", 270.00, 35,  "/images/products/kitkat_frappe.jpg"},
            {"Coffee Float",                "Cold Coffee", "Rich cold brew coffee poured over a scoop of vanilla ice cream.", 240.00, 40,  "/images/products/coffee_float.jpg"},
            {"Vanilla Cold Coffee",         "Cold Coffee", "Velvety cold coffee blended with vanilla syrup and chilled milk.", 210.00, 50,  "/images/products/vanilla_cold_coffee.jpg"},

            // ════════════════════════════════════════
            // 🥤  SIGNATURE DRINKS  (8 items)
            // ════════════════════════════════════════
            {"Spanish Latte",               "Signature Drinks", "Sweetened condensed milk espresso latte – a rich and creamy café favourite.", 240.00, 40,  "/images/products/spanish_latte.jpg"},
            {"Rose Latte",                  "Signature Drinks", "Floral espresso latte infused with rose syrup and steamed milk.", 220.00, 35,  "/images/products/rose_latte.jpg"},
            {"Nutella Coffee",              "Signature Drinks", "Espresso blended with creamy Nutella spread and steamed milk.", 280.00, 30,  "/images/products/nutella_coffee.jpg"},
            {"Brown Sugar Latte",           "Signature Drinks", "Espresso with rich brown sugar syrup and velvety oat milk.", 240.00, 35,  "/images/products/brown_sugar_latte.jpg"},
            {"Salted Caramel Coffee",       "Signature Drinks", "Smooth espresso with salted caramel drizzle and steamed milk.", 250.00, 35,  "/images/products/salted_caramel_coffee.jpg"},
            {"Affogato",                    "Signature Drinks", "A shot of hot espresso poured over a scoop of vanilla gelato.", 260.00, 30,  "/images/products/affogato.jpg"},
            {"Honey Cinnamon Latte",        "Signature Drinks", "Espresso with honey, a pinch of cinnamon, and steamed milk.", 230.00, 35,  "/images/products/honey_cinnamon_latte.jpg"},
            {"Classic Mocha Supreme",       "Signature Drinks", "Double espresso with extra-rich chocolate sauce and topped with whipped cream.", 250.00, 30,  "/images/products/classic_mocha_supreme.jpg"},

            // ════════════════════════════════════════
            // 🍵  TEA  (10 items)
            // ════════════════════════════════════════
            {"Masala Tea",                  "Tea", "Traditional Indian spiced tea brewed with milk, ginger, and aromatic spices.", 60.00, 150, "/images/products/masala_tea.jpg"},
            {"Ginger Tea",                  "Tea", "Strong and zingy brewed tea with fresh ginger slices.", 70.00, 120, "/images/products/ginger_tea.jpg"},
            {"Elaichi Tea",                 "Tea", "Classic milk tea delicately flavoured with crushed green cardamom.", 70.00, 120, "/images/products/elaichi_tea.jpg"},
            {"Lemon Tea",                   "Tea", "Refreshing black tea with a squeeze of fresh lemon juice.", 70.00, 100, "/images/products/lemon_tea.jpg"},
            {"Green Tea",                   "Tea", "Premium organic steamed green tea leaves, light and antioxidant-rich.", 80.00, 100, "/images/products/green_tea.jpg"},
            {"Tulsi Tea",                   "Tea", "Herbal tea brewed with holy basil leaves for a soothing experience.", 80.00, 90,  "/images/products/tulsi_tea.jpg"},
            {"Kashmiri Kahwa",              "Tea", "Aromatic Kashmiri green tea brewed with saffron, cardamom, and almonds.", 120.00, 60,  "/images/products/kashmiri_kahwa.jpg"},
            {"Black Tea",                   "Tea", "Freshly brewed classic plain black tea, bold and clean.", 60.00, 120, "/images/products/black_tea.jpg"},
            {"Mint Tea",                    "Tea", "Refreshing herbal tea brewed with fresh mint leaves.", 80.00, 80,  "/images/products/mint_tea.jpg"},
            {"Hibiscus Tea",                "Tea", "Tangy and vibrant hibiscus flower tea, served hot or cold.", 90.00, 70,  "/images/products/hibiscus_tea.jpg"},

            // ════════════════════════════════════════
            // 🍰  DESSERTS  (10 items)
            // ════════════════════════════════════════
            {"Brownie",                     "Desserts", "Warm, fudgy chocolate brownie loaded with Belgian chocolate chunks.", 180.00, 40,  "/images/products/chocolate_brownie.jpg"},
            {"Brownie with Ice Cream",      "Desserts", "Warm chocolate brownie served with a scoop of vanilla ice cream.", 250.00, 30,  "/images/products/brownie_ice_cream.jpg"},
            {"Cheesecake",                  "Desserts", "Classic creamy baked cheesecake with a buttery graham cracker crust.", 260.00, 25,  "/images/products/cheesecake.jpg"},
            {"Red Velvet Cake",             "Desserts", "Decadent red velvet cake layers with luscious cream cheese frosting.", 240.00, 25,  "/images/products/red_velvet_cake.jpg"},
            {"Tiramisu",                    "Desserts", "Italian layered dessert with espresso-soaked ladyfingers and mascarpone cream.", 280.00, 20,  "/images/products/tiramisu.jpg"},
            {"Chocolate Truffle Cake",      "Desserts", "Rich, dense chocolate cake coated with silky dark chocolate ganache.", 220.00, 25,  "/images/products/chocolate_truffle.jpg"},
            {"Blueberry Cheesecake",        "Desserts", "Classic cheesecake topped with a vibrant fresh blueberry compote.", 280.00, 20,  "/images/products/blueberry_cheesecake.jpg"},
            {"Mousse Cup",                  "Desserts", "Light and airy chocolate mousse served in an elegant cup.", 180.00, 30,  "/images/products/mousse_cup.jpg"},
            {"Chocolate Pastry",            "Desserts", "Soft, moist chocolate sponge pastry with ganache and cream layers.", 140.00, 35,  "/images/products/chocolate_pastry.jpg"},
            {"Cupcake",                     "Desserts", "Moist vanilla or chocolate cupcake topped with swirled buttercream frosting.", 90.00, 50,  "/images/products/cupcake.jpg"},

            // ════════════════════════════════════════
            // 🥐  SNACKS  (10 items)
            // ════════════════════════════════════════
            {"French Fries",                "Snacks", "Crispy, golden potato fries seasoned with salt.", 150.00, 80,  "/images/products/french_fries.jpg"},
            {"Peri Peri Fries",             "Snacks", "Crispy fries tossed in spicy peri peri masala for a fiery kick.", 180.00, 70,  "/images/products/peri_peri_fries.jpg"},
            {"Garlic Bread",                "Snacks", "Warm toasted bread spread with garlic butter and fresh herbs.", 140.00, 60,  "/images/products/garlic_bread.jpg"},
            {"Cheese Garlic Bread",         "Snacks", "Garlic herb bread loaded with melted mozzarella and cheddar cheese.", 220.00, 55,  "/images/products/cheese_garlic_bread.jpg"},
            {"Veg Puff",                    "Snacks", "Flaky pastry filled with spiced mixed vegetables – a café classic.", 60.00, 100, "/images/products/veg_puff.jpg"},
            {"Paneer Puff",                 "Snacks", "Buttery pastry filled with seasoned cottage cheese and herbs.", 80.00, 90,  "/images/products/paneer_puff.jpg"},
            {"Veg Roll",                    "Snacks", "Spiced vegetables wrapped in a soft, flaky roll.", 120.00, 70,  "/images/products/veg_roll.jpg"},
            {"Spring Roll",                 "Snacks", "Crispy golden spring rolls filled with stir-fried vegetables.", 160.00, 65,  "/images/products/spring_roll.jpg"},
            {"Nachos",                      "Snacks", "Crispy tortilla chips served with salsa, cheese dip, and jalapeños.", 180.00, 55,  "/images/products/nachos.jpg"},
            {"Popcorn",                     "Snacks", "Light and crunchy salted or buttered popcorn – perfect café snack.", 120.00, 80,  "/images/products/popcorn.jpg"},

            // ════════════════════════════════════════
            // 🥪  SANDWICHES  (10 items)
            // ════════════════════════════════════════
            {"Veg Sandwich",                "Sandwiches", "Grated veggies and cheese spread inside toasted bread slices.", 170.00, 60,  "/images/products/veg_sandwich.jpg"},
            {"Grilled Veg Sandwich",        "Sandwiches", "Fresh vegetables with cheese grilled to golden perfection.", 180.00, 55,  "/images/products/grilled_veg_sandwich.jpg"},
            {"Grilled Cheese Sandwich",     "Sandwiches", "Loaded with melted processed cheese between buttered toasted bread.", 210.00, 55,  "/images/products/cheese_sandwich.jpg"},
            {"Paneer Sandwich",             "Sandwiches", "Spiced cottage cheese filling grilled in soft sandwich bread.", 230.00, 50,  "/images/products/paneer_sandwich.jpg"},
            {"Corn Cheese Sandwich",        "Sandwiches", "Creamy sweet corn and mozzarella cheese grilled sandwich.", 180.00, 50,  "/images/products/corn_cheese_sandwich.jpg"},
            {"Club Sandwich",               "Sandwiches", "Triple-decker toasted sandwich with veggies, cheese, and mayo.", 220.00, 40,  "/images/products/club_sandwich.jpg"},
            {"Mexican Sandwich",            "Sandwiches", "Spiced black beans, jalapeños, salsa, and cheese in toasted bread.", 210.00, 40,  "/images/products/mexican_sandwich.jpg"},
            {"Bombay Sandwich",             "Sandwiches", "Classic Mumbai-style sandwich with mint chutney, cheese, and veggies.", 150.00, 60,  "/images/products/bombay_sandwich.jpg"},
            {"Aloo Sandwich",               "Sandwiches", "Spiced mashed potato filling in buttered and grilled bread slices.", 140.00, 65,  "/images/products/aloo_sandwich.jpg"},
            {"Chocolate Sandwich",          "Sandwiches", "Nutella and banana or chocolate spread grilled in soft bread.", 170.00, 45,  "/images/products/chocolate_sandwich.jpg"},

            // ════════════════════════════════════════
            // 🍕  PIZZA  (10 items)
            // ════════════════════════════════════════
            {"Margherita Pizza",            "Pizza", "Classic pizza topped with fresh tomato sauce, mozzarella, and basil.", 280.00, 40,  "/images/products/margherita_pizza.jpg"},
            {"Veg Delight",                 "Pizza", "Loaded with bell peppers, onions, sweet corn, and mushrooms.", 260.00, 35,  "/images/products/veg_delight_pizza.jpg"},
            {"Farmhouse Pizza",             "Pizza", "Topped with fresh tomatoes, capsicum, onions, and mushrooms.", 360.00, 30,  "/images/products/farmhouse_pizza.jpg"},
            {"Paneer Tikka Pizza",          "Pizza", "Marinated paneer tikka on a tangy tomato base with onions and peppers.", 340.00, 25,  "/images/products/paneer_tikka_pizza.jpg"},
            {"Cheese Burst Pizza",          "Pizza", "Crust filled with molten cheese with a rich mozzarella topping.", 360.00, 20,  "/images/products/cheese_burst_pizza.jpg"},
            {"Corn Pizza",                  "Pizza", "Sweet corn and capsicum on a herbed tomato sauce with mozzarella.", 260.00, 30,  "/images/products/corn_pizza.jpg"},
            {"Mushroom Pizza",              "Pizza", "Sautéed mushrooms, garlic, and mozzarella on a rustic tomato base.", 300.00, 25,  "/images/products/mushroom_pizza.jpg"},
            {"Mexican Pizza",               "Pizza", "Spicy jalapeños, black beans, salsa, and cheese on a corn tortilla base.", 330.00, 25,  "/images/products/mexican_pizza.jpg"},
            {"Tandoori Paneer Pizza",       "Pizza", "Tandoori marinated paneer with onions, peppers, and mint chutney base.", 350.00, 20,  "/images/products/tandoori_paneer_pizza.jpg"},
            {"Double Cheese Pizza",         "Pizza", "Extra mozzarella and cheddar loaded on a classic tomato base.", 380.00, 20,  "/images/products/double_cheese_pizza.jpg"},

            // ════════════════════════════════════════
            // 🍔  BURGERS  (10 items)
            // ════════════════════════════════════════
            {"Veg Burger",                  "Burger", "Crispy veg patty with lettuce, tomato, onions, and burger sauce in a soft bun.", 170.00, 60,  "/images/products/veg_burger.jpg"},
            {"Cheese Burger",               "Burger", "Juicy patty with a melted cheese slice, lettuce, and pickles.", 220.00, 55,  "/images/products/cheese_burger.jpg"},
            {"Paneer Burger",               "Burger", "Spiced paneer tikka patty with mint chutney and fresh veggies.", 190.00, 45,  "/images/products/paneer_burger.jpg"},
            {"Aloo Tikki Burger",           "Burger", "Classic Indian-style aloo tikki patty with tangy tamarind chutney.", 140.00, 60,  "/images/products/aloo_tikki_burger.jpg"},
            {"Mexican Burger",              "Burger", "Spicy black bean patty with jalapeños, salsa, and cheese.", 210.00, 40,  "/images/products/mexican_burger.jpg"},
            {"Spicy Veg Burger",            "Burger", "Extra-spicy veg patty with habanero sauce and crunchy onions.", 180.00, 45,  "/images/products/spicy_veg_burger.jpg"},
            {"Double Cheese Burger",        "Burger", "Double cheese slices with a thick patty, lettuce, and special sauce.", 220.00, 35,  "/images/products/double_cheese_burger.jpg"},
            {"Mushroom Burger",             "Burger", "Sautéed mushroom patty with garlic aioli and fresh greens.", 200.00, 40,  "/images/products/mushroom_burger.jpg"},
            {"Corn Burger",                 "Burger", "Sweet corn patty with creamy coleslaw and burger sauce.", 170.00, 45,  "/images/products/corn_burger.jpg"},
            {"Crunchy Burger",              "Burger", "Extra-crispy breaded patty with crunchy slaw and spicy mayo.", 190.00, 40,  "/images/products/crunchy_burger.jpg"},

            // ════════════════════════════════════════
            // 🍝  PASTA  (10 items)
            // ════════════════════════════════════════
            {"White Sauce Pasta",           "Pasta", "Penne pasta tossed in a rich, creamy Béchamel Alfredo sauce.", 290.00, 35,  "/images/products/white_sauce_pasta.jpg"},
            {"Red Sauce Pasta",             "Pasta", "Penne pasta cooked in a tangy and spicy Italian tomato basil sauce.", 280.00, 35,  "/images/products/red_sauce_pasta.jpg"},
            {"Pink Sauce Pasta",            "Pasta", "Perfectly balanced mix of tomato and creamy white sauce with pasta.", 230.00, 30,  "/images/products/pink_sauce_pasta.jpg"},
            {"Alfredo Pasta",               "Pasta", "Fettuccine in a velvety Parmesan and butter cream sauce.", 260.00, 25,  "/images/products/alfredo_pasta.jpg"},
            {"Arrabbiata Pasta",            "Pasta", "Penne in a fiery, garlicky tomato chilli sauce.", 240.00, 30,  "/images/products/arrabbiata_pasta.jpg"},
            {"Cheesy Pasta",                "Pasta", "Penne tossed in a rich three-cheese sauce – mozzarella, cheddar, and Parmesan.", 250.00, 30,  "/images/products/cheesy_pasta.jpg"},
            {"Veg Pasta",                   "Pasta", "Seasonal vegetables tossed with pasta in a light olive oil and herb sauce.", 220.00, 35,  "/images/products/veg_pasta.jpg"},
            {"Mushroom Pasta",              "Pasta", "Sautéed mushrooms and garlic pasta in a creamy white sauce.", 240.00, 30,  "/images/products/mushroom_pasta.jpg"},
            {"Paneer Pasta",                "Pasta", "Soft cottage cheese cubes tossed with pasta in a makhani-inspired sauce.", 250.00, 25,  "/images/products/paneer_pasta.jpg"},
            {"Italian Herb Pasta",          "Pasta", "Pasta tossed in olive oil, garlic, and a blend of Italian herbs.", 240.00, 30,  "/images/products/italian_herb_pasta.jpg"},

            // ════════════════════════════════════════
            // 🥤  MILKSHAKES  (10 items)
            // ════════════════════════════════════════
            {"Chocolate Shake",             "Milkshakes", "Rich chocolate ice cream blended with milk and chocolate syrup.", 180.00, 40,  "/images/products/chocolate_shake.jpg"},
            {"Oreo Shake",                  "Milkshakes", "Creamy vanilla ice cream blended with Oreo cookies and milk.", 220.00, 35,  "/images/products/oreo_shake.jpg"},
            {"KitKat Shake",                "Milkshakes", "Velvety shake blended with KitKat wafer bars and ice cream.", 230.00, 30,  "/images/products/kitkat_shake.jpg"},
            {"Strawberry Shake",            "Milkshakes", "Sweet strawberries blended with milk and vanilla ice cream.", 190.00, 35,  "/images/products/strawberry_shake.jpg"},
            {"Vanilla Shake",               "Milkshakes", "Classic pure vanilla ice cream blended with chilled milk.", 170.00, 40,  "/images/products/vanilla_shake.jpg"},
            {"Mango Shake",                 "Milkshakes", "Sweet Alphonso mango pulp blended with chilled milk.", 180.00, 40,  "/images/products/mango_shake.jpg"},
            {"Banana Shake",                "Milkshakes", "Fresh banana blended with milk and a scoop of vanilla ice cream.", 160.00, 40,  "/images/products/banana_shake.jpg"},
            {"Cold Coffee Shake",           "Milkshakes", "Espresso blended with milk and vanilla ice cream for a mocha shake.", 210.00, 35,  "/images/products/cold_coffee_shake.jpg"},
            {"Butterscotch Shake",          "Milkshakes", "Butterscotch ice cream blended with milk and caramel toffee bits.", 190.00, 35,  "/images/products/butterscotch_shake.jpg"},
            {"Black Forest Shake",          "Milkshakes", "Chocolate ice cream shake with cherry compote and whipped cream.", 230.00, 30,  "/images/products/black_forest_shake.jpg"},

            // ════════════════════════════════════════
            // 🍹  FRESH JUICES  (10 items)
            // ════════════════════════════════════════
            {"Orange Juice",                "Fresh Juices", "Freshly squeezed sweet oranges – pure and natural.", 120.00, 50,  "/images/products/orange_juice.jpg"},
            {"Apple Juice",                 "Fresh Juices", "Fresh cold-pressed apple juice – crisp and refreshing.", 140.00, 45,  "/images/products/apple_juice.jpg"},
            {"Watermelon Juice",            "Fresh Juices", "Refreshing freshly blended seasonal watermelon juice.", 110.00, 55,  "/images/products/watermelon_juice.jpg"},
            {"Pineapple Juice",             "Fresh Juices", "Sweet and tangy freshly pressed pineapple juice.", 130.00, 45,  "/images/products/pineapple_juice.jpg"},
            {"Mosambi Juice",               "Fresh Juices", "Freshly squeezed sweet lime (mosambi) juice – light and citrusy.", 120.00, 50,  "/images/products/mosambi_juice.jpg"},
            {"Pomegranate Juice",           "Fresh Juices", "Rich antioxidant-packed fresh pomegranate juice.", 160.00, 35,  "/images/products/pomegranate_juice.jpg"},
            {"Mango Juice",                 "Fresh Juices", "Creamy, rich juice made from sweet Alphonso mangoes.", 150.00, 40,  "/images/products/mango_juice.jpg"},
            {"Mixed Fruit Juice",           "Fresh Juices", "A vibrant blend of seasonal fruits for a refreshing drink.", 170.00, 40,  "/images/products/mixed_fruit_juice.jpg"},
            {"Kiwi Juice",                  "Fresh Juices", "Freshly pressed kiwi fruit juice – tangy and nutritious.", 180.00, 35,  "/images/products/kiwi_juice.jpg"},
            {"Carrot Beetroot Juice",       "Fresh Juices", "Energising blend of fresh carrot and beetroot juice.", 150.00, 40,  "/images/products/carrot_beetroot_juice.jpg"},

            // ════════════════════════════════════════
            // 🍨  ICE CREAMS  (10 items)
            // ════════════════════════════════════════
            {"Vanilla Ice Cream",           "Ice Creams", "Classic creamy vanilla ice cream made with pure vanilla bean extract.", 80.00,  80,  "/images/products/vanilla_ice_cream.jpg"},
            {"Chocolate Ice Cream",         "Ice Creams", "Rich, decadent dark chocolate ice cream for chocoholics.", 100.00, 70,  "/images/products/chocolate_ice_cream.jpg"},
            {"Strawberry Ice Cream",        "Ice Creams", "Sweet strawberry swirled ice cream made with real fruit.", 100.00, 65,  "/images/products/strawberry_ice_cream.jpg"},
            {"Butterscotch Ice Cream",      "Ice Creams", "Creamy butterscotch ice cream with crunchy caramel praline bits.", 110.00, 60,  "/images/products/butterscotch_ice_cream.jpg"},
            {"Black Currant Ice Cream",     "Ice Creams", "Vibrant black currant ice cream with a tangy berry finish.", 120.00, 55,  "/images/products/black_currant_ice_cream.jpg"},
            {"Belgian Chocolate Ice Cream", "Ice Creams", "Premium ice cream made with 70% Belgian dark chocolate.", 150.00, 40,  "/images/products/belgian_chocolate_ice_cream.jpg"},
            {"Mango Ice Cream",             "Ice Creams", "Lush Alphonso mango flavoured ice cream – a summer favourite.", 120.00, 60,  "/images/products/mango_ice_cream.jpg"},
            {"Kulfi",                       "Ice Creams", "Traditional Indian frozen dessert with cardamom, saffron, and pistachios.", 90.00,  70,  "/images/products/kulfi.jpg"},
            {"Tender Coconut Ice Cream",    "Ice Creams", "Refreshing ice cream made with fresh tender coconut flesh and water.", 130.00, 50,  "/images/products/tender_coconut_ice_cream.jpg"},
            {"Sundae",                      "Ice Creams", "Ice cream sundae with chocolate sauce, nuts, cherries, and whipped cream.", 180.00, 35,  "/images/products/sundae.jpg"},

            // ════════════════════════════════════════
            // 🍩  BAKERY  (10 items)
            // ════════════════════════════════════════
            {"Croissant",                   "Bakery", "Classic light and flaky French pastry, perfectly golden and buttery.", 120.00, 50,  "/images/products/croissant.jpg"},
            {"Butter Croissant",            "Bakery", "Extra-buttery, layered croissant with a melt-in-your-mouth texture.", 140.00, 45,  "/images/products/butter_croissant_bake.jpg"},
            {"Chocolate Croissant",         "Bakery", "Flaky croissant filled with rich Belgian dark chocolate.", 170.00, 40,  "/images/products/chocolate_croissant.jpg"},
            {"Muffin",                      "Bakery", "Soft, moist vanilla muffin with a golden top – a café staple.", 90.00,  60,  "/images/products/muffin.jpg"},
            {"Blueberry Muffin",            "Bakery", "Moist muffin studded with juicy fresh blueberries.", 110.00, 55,  "/images/products/blueberry_muffin.jpg"},
            {"Donut",                       "Bakery", "Soft, glazed ring donut – classic sugar-dusted or glazed.", 100.00, 60,  "/images/products/donut.jpg"},
            {"Chocolate Donut",             "Bakery", "Fluffy donut dipped in rich chocolate glaze and sprinkled with toppings.", 120.00, 55,  "/images/products/chocolate_donut.jpg"},
            {"Banana Bread",                "Bakery", "Moist and flavourful loaf baked with ripe bananas and a touch of cinnamon.", 130.00, 40,  "/images/products/banana_bread.jpg"},
            {"Cookies",                     "Bakery", "Freshly baked chocolate chip cookies – crispy outside, chewy inside.", 80.00,  70,  "/images/products/cookies.jpg"},
            {"Garlic Bun",                  "Bakery", "Soft baked bun brushed with garlic butter and dried herbs.", 90.00,  65,  "/images/products/garlic_bun.jpg"},
            {"Brownie Bake",                "Desserts", "Rich chocolate brownie, dense and fudgy.", 180.00, 50, "/images/combos/brownie.jpg"},
            {"Cheese Tart",                 "Desserts", "Creamy, decadent cheese tart in a crisp pastry shell.", 180.00, 50, "/images/combos/cheese_tart.jpg"},
            {"Scone",                       "Bakery", "Traditional English scone, golden baked and perfect with jam and cream.", 120.00, 50, "/images/combos/scone.jpg"},
            {"Cold Brew",                   "Cold Coffee", "Smooth and refreshing cold brew coffee.", 150.00, 50, "/images/combos/cold_brew.jpg"},
            {"Choc Cookie",                 "Bakery", "Classic freshly baked chocolate chip cookie.", 80.00, 50, "/images/combos/chocolate_cookie.jpg"},
            {"Tea",                         "Tea", "Freshly brewed hot tea with milk or water.", 60.00, 50, "/images/combos/tea.jpg"},
            {"Lemon Cake",                  "Desserts", "Zesty lemon cake slice with frosting.", 140.00, 50, "/images/combos/lemon_cake.jpg"},
        };

        // Load existing product names once for fast lookup
        List<Product> allProducts = productRepository.findAll();
        java.util.Set<String> existingNames = new java.util.HashSet<>();
        for (Product p : allProducts) {
            existingNames.add(p.getName().toLowerCase().trim());
        }

        // Also handle legacy name aliases
        Map<String, String> legacyAliases = new HashMap<>();
        legacyAliases.put("espresso",         "classic espresso");
        legacyAliases.put("mocha",            "mocha bliss");
        legacyAliases.put("cappuccino",       "vanilla cappuccino");
        legacyAliases.put("iced latte",       "iced latte");
        legacyAliases.put("cold brew",        "cold brew");
        legacyAliases.put("choco frappe",     "choco frappe");
        legacyAliases.put("hazelnut cold brew", "hazelnut cold brew");
        legacyAliases.put("coconut espresso", "coconut espresso");
        legacyAliases.put("matcha latte",     "matcha latte");
        legacyAliases.put("classic cold coffee", "cold coffee");
        legacyAliases.put("chocolate cold coffee", "cold mocha");
        legacyAliases.put("grilled cheese sandwich", "cheese sandwich");
        legacyAliases.put("margherita pizza", "margherita");
        legacyAliases.put("farmhouse pizza", "farmhouse");
        legacyAliases.put("brownie", "chocolate brownie");
        legacyAliases.put("brownie bake", "brownie");

        int added = 0, updated = 0, skipped = 0;

        for (Object[] row : products) {
            String name    = (String) row[0];
            String catName = (String) row[1];
            String desc    = (String) row[2];
            double price   = (double) row[3];
            int stock      = (int)    row[4];
            String imgPath = (String) row[5];

            Category cat = catMap.get(catName);
            if (cat == null) continue;

            String nameLower = name.toLowerCase().trim();

            // Check direct match or legacy alias match
            boolean exists = existingNames.contains(nameLower);
            if (!exists) {
                String alias = legacyAliases.get(nameLower);
                if (alias != null) exists = existingNames.contains(alias);
            }

            if (exists) {
                // Update price, description, and image of existing products
                String finalName = name;
                Product existing = allProducts.stream()
                    .filter(p -> {
                        String pn = p.getName().toLowerCase().trim();
                        String alias = legacyAliases.get(finalName.toLowerCase().trim());
                        return pn.equals(finalName.toLowerCase().trim()) || (alias != null && pn.equals(alias));
                    })
                    .findFirst().orElse(null);
                if (existing != null) {
                    existing.setName(name);
                    existing.setPrice(price);
                    existing.setCategory(cat);
                    existing.setDescription(desc);
                    existing.setImagePath(imgPath);
                    productRepository.saveAndFlush(existing);
                    updated++;
                }
            } else {
                // Insert new product
                productRepository.saveAndFlush(new Product(name, cat, desc, price, stock, true, imgPath));
                existingNames.add(nameLower);
                added++;
            }
        }

        System.out.println("Products synced – Added: " + added + ", Updated: " + updated + ", Skipped: " + skipped);
    }
}
