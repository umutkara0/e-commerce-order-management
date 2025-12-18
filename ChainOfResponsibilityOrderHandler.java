
interface OrderHandler {
    void setNextHandler(OrderHandler handler);
    boolean handle(Order order); // true: Başarılı, false: Zincir kırıldı/Başarısız
}

// 2. Abstract Handler (Zincirleme mantığını yönetir)
abstract class AbstractOrderHandler implements OrderHandler {
    protected OrderHandler nextHandler;

    @Override
    public void setNextHandler(OrderHandler handler) {
        this.nextHandler = handler;
    }

    @Override
    public boolean handle(Order order) {
        if (nextHandler != null) {
            return nextHandler.handle(order);
        }
        return true; // Zincirin sonu
    }
    
    protected abstract boolean process(Order order);
}

// 3. Concrete Handler 1: Stok Kontrolü
class StockCheckHandler extends AbstractOrderHandler {
    @Override
    protected boolean process(Order order) {
        // Gerçekte InventoryService çağrılır
        boolean stocked = true; 
        for(Product p : order.getItems()) {
            if(p.getStock() == 0) {
                stocked = false;
                order.setStatus("HATA: " + p.getName() + " stokta yok!");
                break;
            }
        }
        System.out.println("Chain: Stok Kontrolü -> " + (stocked ? "OK" : "YETERSİZ"));
        if (!stocked) {
            return false;
        }
        return true;
    }

    @Override
    public boolean handle(Order order) {
        if (!process(order)) return false;
        return super.handle(order);
    }
}

// 4. Concrete Handler 2: Ödeme Kontrolü
class PaymentCheckHandler extends AbstractOrderHandler {
    @Override
    protected boolean process(Order order) {
        // Gerçekte Bridge/Façade çağrılır
        boolean paymentOK = true; 
        System.out.println("Chain: Ödeme Kontrolü -> " + (paymentOK ? "OK" : "HATALI"));
        if (!paymentOK) {
            order.setStatus("HATA: Ödeme Başarısız!");
            return false;
        }
        return true;
    }

    @Override
    public boolean handle(Order order) {
        if (!process(order)) return false;
        return super.handle(order);
    }
}