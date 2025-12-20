import java.awt.*;
import java.util.ArrayList;
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
    private JComboBox<String> regionCombo;

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

        JPanel factoryPanel = new JPanel();
        factoryPanel.setBorder(BorderFactory.createTitledBorder("Teslimat Bölgesi (Abstract Factory)"));
        regionCombo = new JComboBox<>(new String[]{"Yurtiçi (Domestic)", "Yurtdışı (International)"});
        factoryPanel.add(new JLabel("Bölge:"));
        factoryPanel.add(regionCombo);

        configPanel.add(decoratorPanel);
        configPanel.add(bridgePanel);
        configPanel.add(factoryPanel);

        // 3. İşlem Butonları (Facade, Strategy, Memento, Chain, Template)
        JPanel actionPanel = new JPanel(new GridLayout(2, 3));
        
        JButton buyNowBtn = new JButton("Hemen Al (Facade)");
        buyNowBtn.addActionListener(e -> runFacade());

        JButton discountPercentageBtn = new JButton("%10 İndirim (Strategy)");
        discountPercentageBtn.addActionListener(e -> applyPercentageStrategy());
        
        JButton discountFixedAmountBtn = new JButton("200 TL İndirim (Strategy)");
        discountFixedAmountBtn.addActionListener(e -> applyFixedAmountStrategy());

        JButton undoBtn = new JButton("Geri Al (Memento)");
        undoBtn.addActionListener(e -> undoAction());

        JButton finalizeBtn = new JButton("Öde ve Onayla (Bridge/Chain)");
        finalizeBtn.addActionListener(e -> {
            if(currentOrder == null || currentOrder.getItems().isEmpty()) {logArea.append("Sepetiniz boş! "); return;}

            boolean isChainOk = runChain();

            if(isChainOk) {
                applyPackaging(); // Önce paketi hesapla
                shippingAndTax();
                runPayment();     // Sonra öde (Bridge)
                finalizeProcess();
            }
            else {currentOrder = null; selectedItems.clear();};
        });

        JButton visitorBtn = new JButton("Sipariş Ağırlığı (Visitor)");
        visitorBtn.addActionListener(e -> CalculateWeight());

        JButton salesReportBtn = new JButton("Satış Raporu (Template)");
        salesReportBtn.addActionListener(e -> {
            SalesReport reporter = new SalesReport();
            logArea.setText(""); // Temizle
            logArea.append(reporter.generateReport());
        });
        JButton InventoryReportBtn = new JButton("Stok Raporu (Template)");
        InventoryReportBtn.addActionListener(e -> {
            InventoryReporter reporter = new InventoryReporter();
            logArea.setText(""); // Temizle
            logArea.append(reporter.generateReport());
        });

        actionPanel.add(buyNowBtn);
        actionPanel.add(discountPercentageBtn);
        actionPanel.add(discountFixedAmountBtn);
        actionPanel.add(undoBtn);
        actionPanel.add(finalizeBtn);
        actionPanel.add(salesReportBtn);
        actionPanel.add(InventoryReportBtn);
        actionPanel.add(visitorBtn);

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
    private List<Product> selectedItems = new ArrayList<>();

    private void printCart() {
        logArea.append("\nGüncel Sepetiniz: ");
        for (Product product : selectedItems) {
            logArea.append(" " + product.getName() + ",");
        }
        logArea.append("\n");
    }

    private void selectProduct(Product p) {
        // 1. Ürünü listeye ekle (Sepete ekleme mantığı)
        selectedItems.add(p);
        
        // 2. BUILDER: Mevcut tüm ürünlerle siparişi yeniden inşa et 
        currentOrder = new ConcreteOrderBuilder()
                .buildCustomerInfo("Müşteri 1")
                .buildItems(new ArrayList<>(selectedItems)) // Mevcut listenin kopyasını gönderir
                .calculateTotal()
                .getResult();

        logArea.append("\nSepete Eklendi: " + p.getName() + " | Güncel Toplam: " + currentOrder.getTotalAmount() + " TL");
        printCart();
    
        // 3. MEMENTO: Her eklemede durumu kaydet 
        orderHistory.add(currentOrder.saveState());
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

        currentOrder.setTotalAmount(currentOrder.getTotalAmount() + myPackage.getCost());
        logArea.append("Paketleme: " + myPackage.getDescription() + " (Maliyet: " + myPackage.getCost() + " TL)\n");
    }

    private void runFacade() {
        if (currentOrder == null || currentOrder.getItems().isEmpty()) {
            logArea.append("\n[Facade] Hata: Önce bir ürün seçmelisiniz!\n");
            return;
        }
        if(currentOrder.getItems().size() > 1) {logArea.append("\n Hızlı alımda yalnızca tek bir ürün alabilirsiniz!"); return;}
        if (currentOrder != null && currentOrder.isDiscountApplied()) {
            logArea.append("\n[Facade Error] İndirimli ürünler için hızlı satın alma kullanılamaz!\n");
            return;
        }

        // [cite: 32] Façade nesnesini oluştur
        PurchaseFacade facade = new PurchaseFacade();
        
        // Sepetteki en son seçilen (veya ilk) ürünü Façade'a gönder [cite: 8, 32]
        Product selectedProduct = currentOrder.getItems().get(currentOrder.getItems().size() - 1);
        
        // [cite: 32] Façade üzerinden tüm alt sistemleri tek bir çağrıyla çalıştır
        boolean success = facade.buyNow(selectedProduct, "Hizli_Musteri_01");
        
        if (success) {
            logArea.append("\n[Facade] Başarılı: " + selectedProduct.getName() + " için hızlı alım yapıldı.\n");
        } else {
            logArea.append("\n[Facade] Hata: Satın alma işlemi tamamlanamadı.\n");
        }
        currentOrder = null;
        selectedItems.clear();
    }

    private void applyPercentageStrategy() {
        if (currentOrder != null) {
            // Strategy Kullanımı
            orderHistory.add(currentOrder.saveState());
            discountContext.setDiscountStrategy(new PercentageDiscount(0.10));
            currentOrder.setTotalAmount(discountContext.getFinalPrice(currentOrder.getTotalAmount()));
            currentOrder.setDiscountApplied(true);
            logArea.append("Strategy: %10 İndirim uygulandı. Yeni Fiyat: " + currentOrder.getTotalAmount() + " TL\n");
        }
    }

    private void applyFixedAmountStrategy() {
        if (currentOrder != null) {
            // Strategy Kullanımı
            orderHistory.add(currentOrder.saveState());
            discountContext.setDiscountStrategy(new FixedAmountDiscount(200));
            currentOrder.setTotalAmount(discountContext.getFinalPrice(currentOrder.getTotalAmount()));
            currentOrder.setDiscountApplied(true);
            logArea.append("Strategy: 200 TL İndirim uygulandı. Yeni Fiyat: " + currentOrder.getTotalAmount() + " TL\n");
        }
    }

    private void undoAction() {
        // History içindeki undo metodunu çağırarak bir önceki durumu alıyoruz
        OrderMemento previousState = orderHistory.undo();
        
        if (previousState != null && currentOrder != null) {
            // 1. Order nesnesini geri yükle
            currentOrder.restoreState(previousState);
            
            // 2. GUI'de biriktirdiğimiz listeyi memento içeriğiyle senkronize et
            // Sadece referans atama (this.selectedItems = ...) yapma, listeyi güncelle:
            this.selectedItems.clear();
            this.selectedItems.addAll(previousState.getProducts());
            
            // 3. Log yazdır
            logArea.append("\n[Memento] Geri Alındı. Güncel Ürün Sayısı: " + selectedItems.size() + "\n");
            printCart();
            logArea.append("Güncel Tutar: " + currentOrder.getTotalAmount() + " TL\n");
        } else {
            logArea.append("\n[Memento] Daha fazla geri alınacak bir işlem yok!\n");
        }
    }

    private void shippingAndTax() {
        SupplyChainFactory factory;
        if (regionCombo.getSelectedItem().toString().contains("Domestic")) {
            factory = new DomesticFactory();
        } else {
            factory = new InternationalFactory();
        }

        Shipping shipping = factory.createShipping(); // Fabrikadan kargo nesnesi al
        Tax tax = factory.createTax();             // Fabrikadan vergi nesnesi al

        double baseTotal = currentOrder.getTotalAmount();
        double shippingCost = shipping.calculateCost(2.0); // Örnek 2kg ağırlık
        double taxAmount = tax.calculateTax(baseTotal);
        currentOrder.setTotalAmount(baseTotal + shippingCost + taxAmount);

        logArea.append("Bölgeye Özgü Kargo: " + shipping.getShippingType() + " (" + shippingCost + " TL)\n");
        logArea.append("Bölgeye Özgü Vergi Oranı: %" + (tax.getTaxRate() * 100) + "\n");
        logArea.append("Final Ödenecek Tutar: " + currentOrder.getTotalAmount() + " TL\n");
        logArea.append(currentOrder.toString());
    }

    private boolean runChain() {
        if (currentOrder == null) return false;
        // Chain of Responsibility Kullanımı
        OrderHandler stock = new StockCheckHandler();
        OrderHandler payment = new PaymentCheckHandler();
        stock.setNextHandler(payment);
        boolean result = stock.handle(currentOrder);
        if (result) {
            logArea.append("\nChain: Tüm kontroller başarıyla geçildi. Sipariş onaylandı.\n");
        } else {
            // Zincirin nerede koptuğunu Order nesnesinden alıyoruz
            logArea.append("Chain Kontrolü Durduruldu: " + currentOrder.getStatus() + "\n");
            return false;
        }
        return true;
    }

    private void finalizeProcess() {
        for (Product p : currentOrder.getItems()) {
            int currentStock = p.getStock();
            if (currentStock > 0) {
                p.setStock(currentStock - 1);
            }
        }
        String orderId = "ORD-" + System.currentTimeMillis();
        OrderStorage.saveToCSV(orderId, currentOrder.getItems(), "Müşteri_1", currentOrder.getTotalAmount());
        OrderStorage.updateInventoryCSV(CatalogManager.getInstance().getAllProducts());
        logArea.append("Sipariş kaydedildi.\n");

        selectedItems.clear(); // Listeyi boşalt
        currentOrder = null;   // Sipariş nesnesini temizle
        
        // GUI bileşenlerini sıfırla
        giftWrapCb.setSelected(false);
        insuranceCb.setSelected(false);
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
        logArea.append("Bridge: Ödeme işlemi " + gatewayCombo.getSelectedItem() + " üzerinden " + typeCombo.getSelectedItem() + " ile yapıldı.\n");
    }

    private void CalculateWeight() {
        if (currentOrder != null && !currentOrder.getItems().isEmpty()) {
            // Visitor nesnesini oluştur
            OrderAnalysisVisitor analysisVisitor = new OrderAnalysisVisitor();
            
            // Sipariş yapısını ziyaret et (accept metodunu çağırır)
            currentOrder.accept(analysisVisitor);
            
            // Sonucu ekrana yazdır
            logArea.append("\n=== VISITOR ANALIZI ===\n");
            logArea.append(analysisVisitor.getAnalysisReport());
            logArea.append("=======================\n");
        } else {
            logArea.append("Hata: Analiz edilecek ürün yok!\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrderManagementSystem().setVisible(true));
    }
}