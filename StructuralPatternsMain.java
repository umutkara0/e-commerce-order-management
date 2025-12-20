// public class StructuralPatternsMain {
//     public static void main(String[] args) {
//         // Yaratımsal desenlerden bir Product alalım
//         Product laptop = CatalogManager.getInstance().getProductById("P102");
//         if (laptop == null) {
//             System.out.println("Ürün bulunamadı.");
//             return;
//         }

//         // 1. DECORATOR TESTI
//         System.out.println("\n--- 1. DECORATOR TESTI (Paketleme) ---");
//         Packaging basicPackage = new BasicPackaging();
//         System.out.printf("Temel Paket: %s (%.2f TL)\n", basicPackage.getDescription(), basicPackage.getCost());
        
//         // Hediye paketi ekle
//         Packaging fancyPackage = new GiftWrapDecorator(basicPackage);
//         System.out.printf("Hediye Paket: %s (%.2f TL)\n", fancyPackage.getDescription(), fancyPackage.getCost());

//         // Sigorta ve Hediye Paketi ekle (Zincirleme Dekorasyon)
//         Packaging insuredFancyPackage = new InsuranceDecorator(fancyPackage);
//         System.out.printf("Sigortalı + Hediye: %s (%.2f TL)\n", insuredFancyPackage.getDescription(), insuredFancyPackage.getCost());
        
//         // 2. BRIDGE TESTI
//         System.out.println("\n--- 2. BRIDGE TESTI (Ödeme) ---");
        
//         // Kredi Kartı ödeme türü ile PayPal geçidini kullan
//         PaymentImpl paypal = new PayPalGateway();
//         PaymentProcessor ccViaPayPal = new CreditCardPayment(paypal);
//         ccViaPayPal.processPayment(100.0);

//         // Havale ödeme türü ile Stripe geçidini kullan
//         PaymentImpl stripe = new StripeGateway();
//         PaymentProcessor transferViaStripe = new BankTransferPayment(stripe);
//         transferViaStripe.processPayment(500.0);

//         // 3. FAÇADE TESTI
//         System.out.println("\n--- 3. FAÇADE TESTI (Hemen Satın Al) ---");
//         PurchaseFacade facade = new PurchaseFacade();
//         facade.buyNow(laptop, "user_001");
//     }
// }