// 1. Visitor Arayüzü
interface OrderVisitor {
    void visit(Product product);
    void visit(Order order); // Yeni eklenen Order ziyareti
}

// 2. Concrete Visitor: Fiyatları tekrar hesaplamak için
class RecalculatePriceVisitor implements OrderVisitor {
    private double newTotal = 0.0;
    
    @Override
    public void visit(Product product) {
        // Ürün fiyatına indirim veya ek vergi ekleme mantığı burada uygulanabilir.
        newTotal += product.getPrice();
        System.out.println("Ziyaretçi: Ürün " + product.getName() + " eklendi.");
    }

    @Override
    public void visit(Order order) {
        // Toplamı güncelledikten sonra siparişin toplamını set et
        order.setTotalAmount(newTotal);
        System.out.printf("Ziyaretçi: Yeni Sipariş Toplamı hesaplandı: %.2f TL\n", newTotal);
    }

    public double getNewTotal() { return newTotal; }
}
