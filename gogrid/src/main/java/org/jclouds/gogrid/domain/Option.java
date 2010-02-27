package org.jclouds.gogrid.domain;

/**
 * @author Oleksiy Yarmula
 */
public class Option {

    private long id;
    private String name;
    private String description;


    /**
     * A no-args constructor is required for deserialization
     */
    public Option() {
    }

    public Option(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option option = (Option) o;

        if (id != option.id) return false;
        if (description != null ? !description.equals(option.description) : option.description != null) return false;
        if (name != null ? !name.equals(option.name) : option.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
