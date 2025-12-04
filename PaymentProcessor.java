// 1. Implementor Arayüzü (PaymentImpl)
interface PaymentImpl {
    void executeTransaction(double amount);
}

// 2. Concrete Implementor 1
class StripeGateway implements PaymentImpl {
    @Override
    public void executeTransaction(double amount) {
        System.out.printf("Stripe Gateway: %.2f TL'lik ödeme işleniyor.\n", amount);
        // Gerçek API çağrıları burada olurdu
    }
}

// 3. Concrete Implementor 2
class PayPalGateway implements PaymentImpl {
    @Override
    public void executeTransaction(double amount) {
        System.out.printf("PayPal Gateway: %.2f TL transfer ediliyor.\n", amount);
    }
}

// 4. Abstraction Sınıfı
abstract class PaymentProcessor {
    protected PaymentImpl paymentImplementation;

    public PaymentProcessor(PaymentImpl implementation) {
        this.paymentImplementation = implementation;
    }

    public abstract void processPayment(double amount);
}

// 5. Refined Abstraction 1
class CreditCardPayment extends PaymentProcessor {
    public CreditCardPayment(PaymentImpl implementation) {
        super(implementation);
    }

    @Override
    public void processPayment(double amount) {
        System.out.println("Kredi Kartı Ödemesi başlatıldı...");
        this.paymentImplementation.executeTransaction(amount);
    }
}

// 6. Refined Abstraction 2
class BankTransferPayment extends PaymentProcessor {
    public BankTransferPayment(PaymentImpl implementation) {
        super(implementation);
    }

    @Override
    public void processPayment(double amount) {
        System.out.println("Banka Havalesi Ödemesi başlatıldı...");
        this.paymentImplementation.executeTransaction(amount);
    }
}
