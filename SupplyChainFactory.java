// Soyut Ürün Arayüzü 1
interface Shipping {
    String getShippingType();
    double calculateCost(double weight);
}

// Somut Ürün 1.1
class DomesticShipping implements Shipping {
    @Override
    public String getShippingType() { return "Yurtiçi Standart Kargo"; }
    @Override
    public double calculateCost(double weight) { return 15.0 + (weight * 0.5); }
}

// Somut Ürün 1.2
class InternationalShipping implements Shipping {
    @Override
    public String getShippingType() { return "Uluslararası Ekspres Kargo"; }
    @Override
    public double calculateCost(double weight) { return 50.0 + (weight * 5.0); }
}

// Soyut Ürün Arayüzü 2
interface Tax {
    double getTaxRate();
    double calculateTax(double amount);
}

// Somut Ürün 2.1
class DomesticTax implements Tax {
    @Override
    public double getTaxRate() { return 0.18; } // %18 KDV
    @Override
    public double calculateTax(double amount) { return amount * 0.18; }
}

// Somut Ürün 2.2
class InternationalTax implements Tax {
    @Override
    public double getTaxRate() { return 0.05; } // %5 Uluslararası İşlem Vergisi
    @Override
    public double calculateTax(double amount) { return amount * 0.05; }
}


// 1. Soyut Fabrika Arayüzü
interface SupplyChainFactory {
    Shipping createShipping();
    Tax createTax();
}

// 2. Somut Fabrika 1
class DomesticFactory implements SupplyChainFactory {
    @Override
    public Shipping createShipping() {
        return new DomesticShipping();
    }
    @Override
    public Tax createTax() {
        return new DomesticTax();
    }
}

// 3. Somut Fabrika 2
class InternationalFactory implements SupplyChainFactory {
    @Override
    public Shipping createShipping() {
        return new InternationalShipping();
    }
    @Override
    public Tax createTax() {
        return new InternationalTax();
    }
}
