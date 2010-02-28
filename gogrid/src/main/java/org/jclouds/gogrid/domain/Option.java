package org.jclouds.gogrid.domain;

/**
 * @author Oleksiy Yarmula
 */
public class Option {

    private Long id;
    private String name;
    private String description;


    /**
     * A no-args constructor is required for deserialization
     */
    public Option() {
    }

    public Option(Long id) {
        this(id, null, null);
    }

    public Option(String name) {
        this(null, name, null);
    }

    public Option(Long id, String name, String description) {
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

        if (description != null ? !description.equals(option.description) : option.description != null) return false;
        if (id != null ? !id.equals(option.id) : option.id != null) return false;
        if (name != null ? !name.equals(option.name) : option.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
