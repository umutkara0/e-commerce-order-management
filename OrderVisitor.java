// Visitor Arayüzü
interface OrderVisitor {
    void visit(Product product);
    void visit(Order order);
}

// Somut Ziyaretçi: Detaylı Analiz Ziyaretçisi
class OrderAnalysisVisitor implements OrderVisitor {
    private StringBuilder report = new StringBuilder();
    private double totalWeight = 0;

    @Override
    public void visit(Product product) {
        // Her ürün için özel bir analiz yapıyoruz
        report.append("- ").append(product.getName())
              .append(" için kargo ağırlığı hesaplanıyor...\n");
        totalWeight += 1.5; // Örnek: her ürün 1.5 kg varsayılsın
    }

    @Override
    public void visit(Order order) {
        report.append("\nAnaliz Sonucu: Toplam Tahmini Ağırlık: ")
              .append(totalWeight).append(" kg\n");
    }

    public String getAnalysisReport() {
        return report.toString();
    }
}
