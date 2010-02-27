package org.jclouds.gogrid.domain;

/**
 * @author Oleksiy Yarmula
 */
public class Ip {

    private long id;

    private String ip;
    private String subnet;

    private boolean isPublic;
    private Option state;

    /**
     * A no-args constructor is required for deserialization
     */
    public Ip() {
    }

    public Ip(long id, String ip, String subnet, boolean isPublic, Option state) {
        this.id = id;
        this.ip = ip;
        this.subnet = subnet;
        this.isPublic = isPublic;
        this.state = state;
    }

    public long getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public String getSubnet() {
        return subnet;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public Option getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ip ip1 = (Ip) o;

        if (id != ip1.id) return false;
        if (isPublic != ip1.isPublic) return false;
        if (!ip.equals(ip1.ip)) return false;
        if (state != null ? !state.equals(ip1.state) : ip1.state != null) return false;
        if (subnet != null ? !subnet.equals(ip1.subnet) : ip1.subnet != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + ip.hashCode();
        result = 31 * result + (subnet != null ? subnet.hashCode() : 0);
        result = 31 * result + (isPublic ? 1 : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }
}
