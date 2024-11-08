package fr.pantheonsorbonne.ufr27.miage.camel;

public enum ProductType {
    LUXURY(20),
    BASE(5);

    private final double vatRate;

    ProductType(double vatRate) {
        this.vatRate = vatRate;
    }

    public double getVatRate() {
        return this.vatRate;
    }

}
