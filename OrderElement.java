// Order classına ve product classına visitor eklemek için
// Element Arayüzü
interface OrderElement {
    void accept(OrderVisitor visitor);
}