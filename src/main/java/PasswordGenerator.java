import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("Admin Password Hash:");
        System.out.println(encoder.encode("admin123"));

        System.out.println();

        System.out.println("User Password Hash:");
        System.out.println(encoder.encode("user123"));
    }
}