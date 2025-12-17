import java.util.ArrayList;
import java.util.List;
// 1. Memento Sınıfı
class OrderMemento {
    private final String status;
    private final double total;

    public OrderMemento(String status, double total) {
        this.status = status;
        this.total = total;
    }

    public String getStatus() { return status; }
    public double getTotal() { return total; }
}

// 2. Caretaker Sınıfı (Durumları yönetir)
class History {
    private final List<OrderMemento> mementos = new ArrayList<>();

    public void add(OrderMemento memento) {
        mementos.add(memento);
        System.out.println("Memento kaydedildi. Toplam kayıt: " + mementos.size());
    }

    public OrderMemento get(int index) {
        if (index >= 0 && index < mementos.size()) {
            return mementos.get(index);
        }
        return null; // veya Exception fırlatılabilir
    }

    public OrderMemento getLast() {
        if (!mementos.isEmpty()) {
            return mementos.get(mementos.size() - 1);
        }
        return null;
    }
}
