import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OrderStorage {
    private static final String FILE_PATH = "orders.csv";

    // Siparişi CSV'ye ekler
    public static void saveToCSV(String orderId, String customer, double total) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            pw.println(orderId + "," + customer + "," + total);
        } catch (IOException e) {
            e.printStackTrace();
        }
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