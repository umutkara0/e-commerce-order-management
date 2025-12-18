import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OrderStorage {
    private static final String FILE_PATH = "orders.csv";
    private static final String INVENTORY_FILE = "inventory.csv";

    // Siparişi CSV'ye ekler
    public static void saveToCSV(String orderId, List<Product> items, String customer, double total) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            String itemNames = items.stream()
            .map(Product::getName)
            .collect(java.util.stream.Collectors.joining(" | ")); // Ürünleri " | " ile ayırır
            pw.println(orderId + "," + customer + "," + itemNames + "," + total);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateInventoryCSV(List<Product> products) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(INVENTORY_FILE, false))) { // 'false' ile dosya temizlenip tekrar yazılır
            for (Product p : products) {
                pw.println(p.getId() + "," + p.getName() + "," + p.getStock());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // CSV'deki tüm satırları okur
    public static List<String> readFromCSV() {
        List<String> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                records.add(line);
            }
        } catch (IOException e) {
            // Dosya yoksa boş liste döner
        }
        return records;
    }
}