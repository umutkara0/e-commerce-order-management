import java.util.List;
public class BehavioralPatternMain {
    public static void main(String[] args) {
        CatalogManager manager = CatalogManager.getInstance();
        Product phone = manager.getProductById("P101");

        // 1. MEMENTO ve VISITOR Testi
        Order order = new ConcreteOrderBuilder()
            .buildCustomerInfo("Ceren Yılmaz")
            .buildItems(List.of(phone))
            .buildShippingAddress("İzmir")
            .calculateTotal()
            .getResult();

        History history = new History();
        history.add(order.saveState()); // Başlangıç durumunu kaydet (PENDING)

        System.out.println("\n--- VISITOR TESTI ---");
        RecalculatePriceVisitor visitor = new RecalculatePriceVisitor();
        order.accept(visitor);
        // Sipariş toplamı güncellendi (Visitor ile)
        System.out.println(order.toString());
        history.add(order.saveState()); // Güncel durumu kaydet

        // 2. CHAIN OF RESPONSIBILITY Testi
        System.out.println("\n--- CHAIN OF RESPONSIBILITY TESTI ---");
        OrderHandler stock = new StockCheckHandler();
        OrderHandler payment = new PaymentCheckHandler();
        
        stock.setNextHandler(payment);
        
        boolean chainResult = stock.handle(order);
        if (chainResult) {
            order.setStatus("PROCESSED");
            System.out.println("Sipariş Onayı Başarılı. Yeni Durum: " + order.getStatus());
        }
        history.add(order.saveState()); // PROCESSED durumunu kaydet

        // 3. MEMENTO Geri Yükleme Testi
        System.out.println("\n--- MEMENTO GERI YUKLEME TESTI ---");
        // İkinci (indeks 1) duruma geri dön
        order.restoreState(history.getLast()); 
        System.out.println("Geri Yüklenen Durum: " + order.getStatus());

        // 4. STRATEGY Testi
        System.out.println("\n--- STRATEGY TESTI ---");
        DiscountContext context = new DiscountContext();
        double orderAmount = order.getTotalAmount();
        
        // Yüzdesel indirim uygula
        context.setDiscountStrategy(new PercentageDiscount(0.10));
        System.out.printf("%%10 İndirimli Fiyat: %.2f TL\n", context.getFinalPrice(orderAmount));
        
        // Sabit tutar indirim uygula
        context.setDiscountStrategy(new FixedAmountDiscount(50.0));
        System.out.printf("50 TL İndirimli Fiyat: %.2f TL\n", context.getFinalPrice(orderAmount));

        // 5. TEMPLATE METHOD Testi
        System.out.println("\n--- TEMPLATE METHOD TESTI ---");
        ReportGenerator salesReport = new SalesReport();
        salesReport.generateReport();

        ReportGenerator inventoryReport = new InventoryReporter();
        inventoryReport.generateReport();
    }
}