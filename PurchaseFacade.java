// Alt Sistem 1: Envanter
class InventoryService {
    public boolean checkStock(String productId) {
        // Basit kontrol
        return true; 
    }

    public void decreaseStock(String productId) {
        System.out.println("Envanterden ürün " + productId + " düşüldü.");
    }
}

// Alt Sistem 2: Ödeme (Bridge desenindeki processor kullanılabilir, ama Façade için basitleştirilmiş bir versiyon yaratalım)
class PaymentGatewayFacade {
    public boolean process(double amount) {
        System.out.printf("Ödeme alt sistemi: %.2f TL başarılı.\n", amount);
        return true;
    }
}

// Alt Sistem 3: Bildirim
class NotificationService {
    public void sendConfirmationEmail(String customerId, String orderId) {
        System.out.println("Bildirim servisi: Müşteri " + customerId + " için sipariş " + orderId + " onay e-postası gönderildi.");
    }
}

/**
 * Façade Deseni Uygulaması: Karmaşık bir satın alma işlemini tek bir metotta birleştirir.
 */
class PurchaseFacade {
    private final InventoryService inventoryService;
    private final PaymentGatewayFacade paymentGateway;
    private final NotificationService notificationService;

    public PurchaseFacade() {
        this.inventoryService = new InventoryService();
        this.paymentGateway = new PaymentGatewayFacade();
        this.notificationService = new NotificationService();
    }

    // Hemen Satın Al (Buy Now) işlevi
    public boolean buyNow(Product product, String customerId) {
        String orderId = "ORD-" + System.currentTimeMillis();
        System.out.println("\n--- FACA DE SATIN ALMA BASLATILDI ---");

        // 1. Stok Kontrolü
        if (!inventoryService.checkStock(product.getId())) {
            System.out.println("Satın alma başarısız: Stok yetersiz.");
            return false;
        }

        // 2. Ödeme İşleme
        if (!paymentGateway.process(product.getPrice())) {
            System.out.println("Satın alma başarısız: Ödeme hatası.");
            return false;
        }

        // 3. Stok Düşme ve Sipariş Oluşturma
        inventoryService.decreaseStock(product.getId());
        
        // 4. Bildirim
        notificationService.sendConfirmationEmail(customerId, orderId);
        
        System.out.println("--- FACA DE SATIN ALMA BASARILI: Sipariş No: " + orderId + " ---");
        return true;
    }
}