// Alt Sistem 1: Envanter (Dinamik ve Kalıcı)
class InventoryService {
    private CatalogManager catalog = CatalogManager.getInstance(); //  Singleton

    public boolean checkStock(String productId) {
        Product p = catalog.getProductById(productId);
        // [cite: 8] Minimal fonksiyonellik: Stok varsa true döner
        return p != null && p.getStock() > 0;
    }

    public void updateInventory() {
        //  Tüm ürünlerin güncel stoğunu CSV'ye yansıtır
        OrderStorage.updateInventoryCSV(catalog.getAllProducts());
        System.out.println("Envanter: CSV dosyası ve Singleton katalog güncellendi.");
    }
}

// Alt Sistem 2: Ödeme İşlemcisi (Bridge Kullanımı)
class PaymentService {
    public boolean processSecurePayment(double amount, String provider) {
        //  Bridge Deseni: Soyutlama (Processor) ve Uygulama (Gateway) ayrımı
        PaymentImpl gateway = provider.equalsIgnoreCase("Stripe") ? new StripeGateway() : new PayPalGateway();
        PaymentProcessor processor = new CreditCardPayment(gateway);
        
        processor.processPayment(amount);
        System.out.println("Ödeme Servisi: " + amount + " TL tutarında işlem onaylandı.");
        return true; 
    }
}

// Alt Sistem 3: Sipariş Onay ve Kayıt
class OrderFinalizer {
    public void finalizeOrder(Order order) {
        //  Memento: Siparişin final halini kaydet
        History history = new History();
        history.add(order.saveState());
        
        //  CSV: Siparişi kalıcı olarak kaydet
        String orderId = "FAC-" + System.currentTimeMillis();
        OrderStorage.saveToCSV(orderId, order.getItems(), "Facade_Müşterisi", order.getTotalAmount());
        
        System.out.println("Onay Servisi: Sipariş CSV'ye yazıldı ve Memento kaydı oluşturuldu.");
    }
}

/**
 * Façade Deseni Uygulaması: Karmaşık bir satın alma işlemini tek bir metotta birleştirir.
 */
public class PurchaseFacade {
    private InventoryService inventory = new InventoryService();
    private PaymentService payment = new PaymentService();
    private OrderFinalizer finalizer = new OrderFinalizer();
    private OrderHandler chain;

    public PurchaseFacade() {
        //  Chain of Responsibility yapılandırması
        this.chain = new StockCheckHandler();
        this.chain.setNextHandler(new PaymentCheckHandler());
    }

    public boolean buyNow(Product product, String customer) {
        System.out.println("\n--- FACADE: HIZLI SATIN ALMA BASLATILDI ---");

        // 1. Stok Kontrolü
        if (!inventory.checkStock(product.getId())) {
            System.out.println("Hata: Stok yetersiz.");
            return false;
        }

        // 2. Sipariş İnşası (Builder) 
        Order order = new ConcreteOrderBuilder()
                .buildCustomerInfo(customer)
                .buildItems(java.util.List.of(product))
                .calculateTotal()
                .getResult();

        // 3. Ödeme İşlemi (Bridge Entegrasyonu) 
        payment.processSecurePayment(order.getTotalAmount(), "Stripe");

        // 4. Doğrulama Zinciri (Chain of Responsibility) 
        if (!chain.handle(order)) {
            return false;
        }

        // 5. Stok Düşürme ve CSV Güncelleme 
        product.setStock(product.getStock() - 1);
        inventory.updateInventory();

        // 6. Kayıt ve Onay (Memento & CSV) 
        finalizer.finalizeOrder(order);

        System.out.println("--- FACADE: ISLEM BASARIYLA TAMAMLANDI ---\n");
        return true;
    }
}