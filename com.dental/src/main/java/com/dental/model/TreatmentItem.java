package com.dental.model;

public class TreatmentItem {
    private String name;
    private double cost;

    public TreatmentItem() {
    }

    public TreatmentItem(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return name + " - Rs " + cost;
    }
}
