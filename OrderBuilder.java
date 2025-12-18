import java.util.ArrayList;
import java.util.List;

// 1. Kompleks Nesne: Order
class Order implements OrderElement {
    private String customerInfo;
    private List<Product> items;
    private double totalAmount;
    private String shippingAddress;
    private String status = "PENDING"; // Durum bilgisi Memento için önemli

    // Yalnızca Builder tarafından çağrılabilmesi için private veya package private yapıcı metot.
    Order() {
        this.items = new ArrayList<>();
    }

    // Setter metotları builder tarafından kullanılacak
    void setCustomerInfo(String customerInfo) { this.customerInfo = customerInfo; }
    void setItems(List<Product> items) { this.items = items; }
    void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    // YENİ DURUM SETTER
    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }
    public List<Product> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }

    // MEMENTO DESENİ METOTLARI
    public OrderMemento saveState() {
        System.out.println("Order: Durum kaydediliyor...");
        return new OrderMemento(this.status, this.totalAmount);
    }

    public void restoreState(OrderMemento memento) {
        this.status = memento.getStatus();
        this.totalAmount = memento.getTotal();
        System.out.println("Order: Durum geri yüklendi. Yeni Durum: " + this.status);
        System.out.println("Order: Durum geri yüklendi. Yeni Tutar: " + this.totalAmount);
    }

    // VISITOR DESENİ METODU
    @Override
    public void accept(OrderVisitor visitor) {
        // Ziyaretçinin her bir öğeyi ziyaret etmesini sağla
        for (Product item : items) {
            item.accept(visitor);
        }
        // Siparişin kendisini de ziyaret ettirebiliriz
        visitor.visit(this); 
    }

    // Display metodu (İşlevi göstermek için)
    @Override
    public String toString() {
        return "--- SIPARIŞ DETAYLARI ---\n" +
               "Müşteri: " + customerInfo + "\n" +
               "Ürün Sayısı: " + items.size() + "\n" +
               "Adres: " + shippingAddress + "\n" +
               "Toplam Tutar: " + totalAmount + " TL\n" +
               "--------------------------";
    }
}

// 2. Builder Arayüzü
interface OrderBuilder {
    OrderBuilder buildCustomerInfo(String info);
    OrderBuilder buildItems(List<Product> items);
    OrderBuilder calculateTotal();
    OrderBuilder buildShippingAddress(String address);
    Order getResult();
}

// 3. Concrete Builder
class ConcreteOrderBuilder implements OrderBuilder {
    private final Order order;
    private List<Product> itemsToBuild; // Toplam hesaplamak için tutulur

    public ConcreteOrderBuilder() {
        this.order = new Order();
        this.itemsToBuild = new ArrayList<>();
    }

    @Override
    public OrderBuilder buildCustomerInfo(String info) {
        order.setCustomerInfo(info);
        return this;
    }

    @Override
    public OrderBuilder buildItems(List<Product> items) {
        this.itemsToBuild = new ArrayList<>(items);
        order.setItems(this.itemsToBuild);
        return this;
    }

    @Override
    public OrderBuilder calculateTotal() {
        double total = itemsToBuild.stream()
                                  .mapToDouble(Product::getPrice)
                                  .sum();
        // Buraya ek olarak kargo, vergi hesaplamaları eklenebilir.
        order.setTotalAmount(total);
        return this;
    }

    @Override
    public OrderBuilder buildShippingAddress(String address) {
        order.setShippingAddress(address);
        return this;
    }

    @Override
    public Order getResult() {
        // Kontrol eklemek iyi bir uygulamadır (Örn: Müşteri bilgisi girilmiş mi?)
        return order;
    }
}