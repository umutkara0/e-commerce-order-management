import java.util.List;
// 1. Abstract Sınıf (Template)
abstract class ReportGenerator {
    
    // TEMPLATE METHOD: Algoritmanın iskeleti
    public final String generateReport() {
        System.out.println("--- RAPOR ÜRETİMİ BAŞLATILDI ---");
        String data = collectData();
        String formattedReport = formatReport(data);
        exportReport(formattedReport);
        System.out.println("--- RAPOR ÜRETİMİ TAMAMLANDI ---\n");
        return formattedReport; // GUi ye yazdırmak için
    }

    // Alt sınıfların uygulaması gereken temel adımlar
    protected abstract String collectData();
    protected abstract String formatReport(String rawData);
    
    // Ortak adım (Genellikle tüm alt sınıflar için aynı kalır)
    protected void exportReport(String report) {
        System.out.println("Rapor PDF olarak dışa aktarılıyor...");
        // Gerçekte dosyaya yazma kodu olurdu
        System.out.println(report);
    }
}

// 2. Concrete Template 1
class SalesReport extends ReportGenerator {
    @Override
    protected String collectData() {
        // Veriyi dosyadan (CSV) çekerek SRP'yi korur
        List<String> lines = OrderStorage.readFromCSV();
        if (lines.isEmpty()) return "Kayıtlı satış bulunamadı.";
        
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            String[] parts = line.split(",");
                sb.append("ID: ").append(parts[0])
                .append(" | Müşteri: ").append(parts[1])
                .append(" | Ürün: ").append(parts[2])
                .append(" | Tutar: ").append(parts[3]).append(" TL\n");
        }
        return sb.toString();
    }

    @Override
    protected String formatReport(String rawData) {
        return "=== CSV TABANLI SATIŞ RAPORU ===\n" + rawData + "================================\n";
    }
}

// 3. Concrete Template 2
class InventoryReporter extends ReportGenerator {
    @Override
    protected String collectData() {
        // En güncel veriyi CatalogManager (veya CSV) üzerinden toplar 
        List<Product> products = CatalogManager.getInstance().getAllProducts();
        StringBuilder sb = new StringBuilder();
        for (Product p : products) {
            sb.append("Ürün: ").append(p.getName())
              .append(" | Kalan Stok: ").append(p.getStock()).append("\n");
        }
        return sb.toString();
    }

    @Override
    protected String formatReport(String rawData) {
        return "======= GÜNCEL ENVANTER DURUMU =======\n" + rawData + "======================================\n";
    }
}