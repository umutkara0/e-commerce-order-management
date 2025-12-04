import java.util.List;

public class CreationalPatternsMain {
    public static void main(String[] args) {
        // 1. Singleton Testi
        System.out.println("--- 1. SINGLETON TESTI ---");
        CatalogManager manager1 = CatalogManager.getInstance();
        CatalogManager manager2 = CatalogManager.getInstance();
        System.out.println("Aynı nesne mi? " + (manager1 == manager2)); // true olmalı
        Product phone = manager1.getProductById("P101");
        System.out.println("Ürün: " + phone.getName());
        System.out.println("--------------------------\n");


        // 2. Abstract Factory Testi
        System.out.println("--- 2. ABSTRACT FACTORY TESTI ---");
        SupplyChainFactory domesticFactory = new DomesticFactory();
        Shipping domesticShipping = domesticFactory.createShipping();
        Tax domesticTax = domesticFactory.createTax();

        System.out.println("Yurtiçi Kargo: " + domesticShipping.getShippingType());
        System.out.println("Yurtiçi Vergi Oranı: " + domesticTax.getTaxRate());
        System.out.println("--------------------------\n");


        // 3. Builder Testi
        System.out.println("--- 3. BUILDER TESTI ---");
        List<Product> cartItems = List.of(manager1.getProductById("P101"), manager1.getProductById("P103"));

        Order order = new ConcreteOrderBuilder()
            .buildCustomerInfo("Ali Veli")
            .buildItems(cartItems)
            .buildShippingAddress("Çorum, Türkiye")
            .calculateTotal() // Bu adımda Builder deseni ile hesaplama yapılır
            .getResult();

        System.out.println(order.toString());
    }
}
