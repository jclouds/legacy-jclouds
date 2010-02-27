package org.jclouds.gogrid.domain;

/**
 * @author Oleksiy Yarmula
 */
public class BillingToken {

    private long id;
    private String name;
    private double price;

    /**
     * A no-args constructor is required for deserialization
     */
    public BillingToken() {
    }

    public BillingToken(long id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BillingToken that = (BillingToken) o;

        if (id != that.id) return false;
        if (Double.compare(that.price, price) != 0) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        temp = price != +0.0d ? Double.doubleToLongBits(price) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
