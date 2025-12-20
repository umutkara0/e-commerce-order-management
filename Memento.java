import java.util.ArrayList;
import java.util.List;
// 1. Memento Sınıfı
class OrderMemento {
    private final String status;
    private final List<Product> products;
    private final double total;
    private final boolean isDiscountApplied;

    public OrderMemento(String status, List<Product> products, double total, boolean isDiscountApplied) {
        this.status = status;
        this.products = products;
        this.total = total;
        this.isDiscountApplied = isDiscountApplied;
    }

    public String getStatus() { return status; }
    public List<Product> getProducts() { return products; }
    public double getTotal() { return total; }
    public boolean isDiscountApplied() {return isDiscountApplied;}
}

// 2. Caretaker Sınıfı (Durumları yönetir)
class History {
    private final List<OrderMemento> mementos = new ArrayList<>();

    public void add(OrderMemento memento) {
        mementos.add(memento);
    }

    public OrderMemento undo() {
        if (mementos.size() <= 1) {
            // Sadece başlangıç durumu veya boş durum kalmışsa geri alamazsın
            return null; 
        }
        
        // 1. Listenin en sonundaki (yani şu anki) durumu atıyoruz
        mementos.remove(mementos.size() - 1);
        
        // 2. Bir önceki (gerçek geçmiş) durumu döndürüyoruz
        return mementos.get(mementos.size() - 1);
    }
}