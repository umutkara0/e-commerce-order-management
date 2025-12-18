import java.util.ArrayList;
import java.util.List;

/**
 * Singleton Deseni Uygulaması: Tekil ürün kataloğu yöneticisi.
 */
public final class CatalogManager {

    // 1. Singleton örneğini tutacak statik alan (eager initialization).
    private static final CatalogManager INSTANCE = new CatalogManager();

    // 2. Katalogdaki ürünleri tutan basit bir liste.
    private final List<Product> products;

    // 3. Yapıcı metodu private yaparak dışarıdan örnek oluşturmayı engelle.
    private CatalogManager() {
        products = new ArrayList<>();
        // Basit ürünler ekleyelim
        products.add(new Product("P101", "Akıllı Telefon", 5000.0,2));
        products.add(new Product("P102", "Dizüstü Bilgisayar", 12000.0,2));
        products.add(new Product("P103", "Kablosuz Kulaklık", 800.0,2));
    }

    // 4. Singleton örneğine erişim noktası.
    public static CatalogManager getInstance() {
        return INSTANCE;
    }

    // Katalog işlevleri
    public Product getProductById(String id) {
        return products.stream()
                       .filter(p -> p.getId().equals(id))
                       .findFirst()
                       .orElse(null);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }
}

// Katalogdaki ürünlerin temel sınıfı
class Product implements OrderElement {
    private final String id;
    private final String name;
    private final double price;
    private int stock;

    public Product(String id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    @Override
    public void accept(OrderVisitor visitor) {
        visitor.visit(this);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    public void setStock(int stock) { this.stock = stock; }
}