// 1. Abstract Sınıf (Template)
abstract class ReportGenerator {
    
    // TEMPLATE METHOD: Algoritmanın iskeleti
    public final void generateReport() {
        System.out.println("--- RAPOR ÜRETİMİ BAŞLATILDI ---");
        String data = collectData();
        String formattedReport = formatReport(data);
        exportReport(formattedReport);
        System.out.println("--- RAPOR ÜRETİMİ TAMAMLANDI ---\n");
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
class SalesReporter extends ReportGenerator {
    @Override
    protected String collectData() {
        System.out.println("Satış verileri toplanıyor...");
        return "Satışlar: 150 adet, Toplam Ciro: 50,000 TL";
    }

    @Override
    protected String formatReport(String rawData) {
        System.out.println("Satış raporu başlıkları ekleniyor...");
        return "SATIS RAPORU\n" + rawData;
    }
}

// 3. Concrete Template 2
class InventoryReporter extends ReportGenerator {
    @Override
    protected String collectData() {
        System.out.println("Envanter verileri toplanıyor...");
        return "Stok: Telefon=50, Laptop=20";
    }

    @Override
    protected String formatReport(String rawData) {
        System.out.println("Envanter özeti tablosu oluşturuluyor...");
        return "ENVANTER RAPORU\n" + rawData;
    }
}