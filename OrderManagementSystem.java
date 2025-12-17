import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * Tasarım desenlerini uygulayan işlevsel yazılım sistemi.
 * Java dili kullanılarak geliştirilmiştir.
 */
public class OrderManagementSystem extends JFrame {
    private CatalogManager catalog = CatalogManager.getInstance(); // Singleton
    private DiscountContext discountContext = new DiscountContext(); // Strategy
    private History orderHistory = new History(); // Memento (Caretaker)
    private Order currentOrder;
    
    // UI Bileşenleri - Sınıf seviyesinde tanımlı
    private JTextArea logArea;
    private JLabel statusLabel;
    private JCheckBox giftWrapCb, insuranceCb;
    private JComboBox<String> typeCombo, gatewayCombo;

    public OrderManagementSystem() {
        setTitle("Design Patterns E-Market");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Ürün Listesi Paneli (Singleton kullanımı) 
        JPanel productPanel = new JPanel(new GridLayout(0, 1));
        productPanel.setBorder(BorderFactory.createTitledBorder("Ürün Kataloğu"));
        
        for (Product p : catalog.getAllProducts()) {
            JButton btn = new JButton(p.getName() + " - " + p.getPrice() + " TL");
            btn.addActionListener(e -> selectProduct(p));
            productPanel.add(btn);
        }

        // 2. Yapılandırma Paneli (Decorator ve Bridge için seçimler)
        JPanel configPanel = new JPanel(new GridLayout(2, 1));
        
        // Paketleme Paneli (Decorator)
        JPanel decoratorPanel = new JPanel();
        decoratorPanel.setBorder(BorderFactory.createTitledBorder("Paketleme Seçenekleri (Decorator)"));
        giftWrapCb = new JCheckBox("Hediye Paketi (+5 TL)"); // HATA GIDERILDI: Tip tanımı kaldırıldı
        insuranceCb = new JCheckBox("Sigorta (+20 TL)");    // HATA GIDERILDI: Tip tanımı kaldırıldı
        decoratorPanel.add(giftWrapCb);
        decoratorPanel.add(insuranceCb);

        // Ödeme Paneli (Bridge)
        JPanel bridgePanel = new JPanel();
        bridgePanel.setBorder(BorderFactory.createTitledBorder("Ödeme Ayarları (Bridge)"));
        typeCombo = new JComboBox<>(new String[]{"Kredi Kartı", "Banka Havalesi"});
        gatewayCombo = new JComboBox<>(new String[]{"Stripe", "PayPal"});
        bridgePanel.add(new JLabel("Yöntem:"));
        bridgePanel.add(typeCombo);
        bridgePanel.add(new JLabel("Altyapı:"));
        bridgePanel.add(gatewayCombo);

        configPanel.add(decoratorPanel);
        configPanel.add(bridgePanel);

        // 3. İşlem Butonları (Facade, Strategy, Memento, Chain, Template)
        JPanel actionPanel = new JPanel(new GridLayout(2, 3));
        
        JButton buyNowBtn = new JButton("Hemen Al (Facade)");
        buyNowBtn.addActionListener(e -> runFacade());

        JButton discountBtn = new JButton("%10 İndirim (Strategy)");
        discountBtn.addActionListener(e -> applyStrategy());

        JButton undoBtn = new JButton("Geri Al (Memento)");
        undoBtn.addActionListener(e -> undoAction());

        JButton finalizeBtn = new JButton("Öde ve Onayla (Bridge/Chain)");
        finalizeBtn.addActionListener(e -> {
            applyPackaging(); // Önce paketi hesapla
            runPayment();     // Sonra öde (Bridge)
            runChain();       // En son onayla (Chain)
        });

        JButton reportBtn = new JButton("Stok Raporu (Template)");
        reportBtn.addActionListener(e -> {
            InventoryReporter reporter = new InventoryReporter();
            reporter.generateReport(); // Dinamik verilerle rapor basar
        });

        actionPanel.add(buyNowBtn);
        actionPanel.add(discountBtn);
        actionPanel.add(undoBtn);
        actionPanel.add(finalizeBtn);
        actionPanel.add(reportBtn);

        // 4. Log Paneli
        logArea = new JTextArea();
        logArea.setEditable(false);
        statusLabel = new JLabel("Durum: Bekleniyor...");

        add(productPanel, BorderLayout.WEST);
        add(configPanel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    // --- DESEN TETİKLEYİCİ METOTLAR ---
    
    private void selectProduct(Product p) {
        // Builder Kullanımı
        currentOrder = new ConcreteOrderBuilder()
                .buildCustomerInfo("Müşteri 1")
                .buildItems(List.of(p))
                .calculateTotal()
                .getResult();
        logArea.append("\n>>> Yeni Sipariş: " + p.getName() + " eklendi.\n");
        orderHistory.add(currentOrder.saveState()); // Memento Kaydı
    }

    private void applyPackaging() {
        if (currentOrder == null) return;
        // Decorator Deseni Kullanımı 
        Packaging myPackage = new BasicPackaging(); 

        if (giftWrapCb.isSelected()) {
            myPackage = new GiftWrapDecorator(myPackage);
        }
        if (insuranceCb.isSelected()) {
            myPackage = new InsuranceDecorator(myPackage);
        }

        logArea.append("Paketleme: " + myPackage.getDescription() + " (Maliyet: " + myPackage.getCost() + " TL)\n");
    }

    private void runFacade() {
        if (catalog.getAllProducts().isEmpty()) return;
        // Facade Kullanımı
        PurchaseFacade facade = new PurchaseFacade();
        Product firstProduct = catalog.getAllProducts().get(0);
        boolean success = facade.buyNow(firstProduct, "User01");
        logArea.append(success ? "Facade: Hızlı alım başarıyla tamamlandı.\n" : "Facade: Hata oluştu.\n");
    }

    private void applyStrategy() {
        if (currentOrder != null) {
            // Strategy Kullanımı
            discountContext.setDiscountStrategy(new PercentageDiscount(0.10));
            double finalPrice = discountContext.getFinalPrice(currentOrder.getTotalAmount());
            logArea.append("Strategy: %10 İndirim uygulandı. Yeni Fiyat: " + finalPrice + " TL\n");
        }
    }

    private void undoAction() {
        // Memento Kullanımı
        OrderMemento last = orderHistory.getLast();
        if (last != null && currentOrder != null) {
            currentOrder.restoreState(last);
            logArea.append("Memento: Önceki duruma geri dönüldü.\n");
        }
    }

    private void runChain() {
        if (currentOrder == null) return;
        // Chain of Responsibility Kullanımı
        OrderHandler stock = new StockCheckHandler();
        OrderHandler payment = new PaymentCheckHandler();
        stock.setNextHandler(payment);
        boolean result = stock.handle(currentOrder);
        logArea.append(result ? "Chain: Kontroller başarılı, sipariş onaylandı.\n" : "Chain: Kontrol başarısız!\n");
    }

    private void runPayment() {
        if (currentOrder == null) return;
        // Bridge Deseni Kullanımı
        PaymentImpl implementation = gatewayCombo.getSelectedItem().equals("Stripe") 
                                    ? new StripeGateway() : new PayPalGateway();

        PaymentProcessor processor;
        if (typeCombo.getSelectedItem().equals("Kredi Kartı")) {
            processor = new CreditCardPayment(implementation);
        } else {
            processor = new BankTransferPayment(implementation);
        }

        processor.processPayment(currentOrder.getTotalAmount()); 
        logArea.append("Bridge: Ödeme işlemi " + gatewayCombo.getSelectedItem() + " üzerinden yapıldı.\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrderManagementSystem().setVisible(true));
    }
}