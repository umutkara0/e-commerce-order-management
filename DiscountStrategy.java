// 1. Strategy Arayüzü
interface DiscountStrategy {
    double calculateDiscount(double orderAmount);
}

// 2. Concrete Strategy 1: Yüzdesel İndirim
class PercentageDiscount implements DiscountStrategy {
    private final double percentage;

    public PercentageDiscount(double percentage) { this.percentage = percentage; }

    @Override
    public double calculateDiscount(double orderAmount) {
        return orderAmount * percentage;
    }
}

// 3. Concrete Strategy 2: Sabit Tutar İndirimi
class FixedAmountDiscount implements DiscountStrategy {
    private final double fixedAmount;

    public FixedAmountDiscount(double fixedAmount) { this.fixedAmount = fixedAmount; }

    @Override
    public double calculateDiscount(double orderAmount) {
        return Math.min(orderAmount, fixedAmount); // İndirim tutarı siparişten fazla olamaz
    }
}

// Bağlam (Context) sınıfı (basitleştirilmiş)
class DiscountContext {
    private DiscountStrategy strategy;

    public void setDiscountStrategy(DiscountStrategy strategy) {
        this.strategy = strategy;
    }

    public double getFinalPrice(double amount) {
        if (strategy == null) {
            return amount;
        }
        double discount = strategy.calculateDiscount(amount);
        return amount - discount;
    }
}