// 1. Component Arayüzü
interface Packaging {
    double getCost();
    String getDescription();
}

// 2. Concrete Component
class BasicPackaging implements Packaging {
    @Override
    public double getCost() {
        return 10.0; // Temel paketleme maliyeti
    }

    @Override
    public String getDescription() {
        return "Standart Kutu Paketleme";
    }
}

// 3. Abstract Decorator
abstract class PackageDecorator implements Packaging {
    protected Packaging wrappedPackage;

    public PackageDecorator(Packaging wrappedPackage) {
        this.wrappedPackage = wrappedPackage;
    }

    // Decorator, varsayılan olarak sarmaladığı nesnenin metotlarını çağırır.
    @Override
    public double getCost() {
        return wrappedPackage.getCost();
    }

    @Override
    public String getDescription() {
        return wrappedPackage.getDescription();
    }
}

// 4. Concrete Decorator 1
class GiftWrapDecorator extends PackageDecorator {
    public GiftWrapDecorator(Packaging wrappedPackage) {
        super(wrappedPackage);
    }

    @Override
    public double getCost() {
        return super.getCost() + 5.0; // Hediye paketi ek maliyeti
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Hediye Paketi Eklendi";
    }
}

// 5. Concrete Decorator 2
class InsuranceDecorator extends PackageDecorator {
    public InsuranceDecorator(Packaging wrappedPackage) {
        super(wrappedPackage);
    }

    @Override
    public double getCost() {
        return super.getCost() + 20.0; // Kargo sigortası ek maliyeti
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Tam Sigortalı";
    }
}
