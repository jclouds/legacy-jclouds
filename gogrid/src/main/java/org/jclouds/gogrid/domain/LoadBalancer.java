/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.gogrid.domain;

import java.util.Set;

import com.google.common.primitives.Longs;
import com.google.gson.annotations.SerializedName;

/**
 * @author Oleksiy Yarmula
 */
public class LoadBalancer implements Comparable<LoadBalancer> {

    private long id;
    private String name;
    private String description;
    @SerializedName("virtualip")
    private IpPortPair virtualIp;
    @SerializedName("realiplist")
    private Set<IpPortPair> realIpList;
    private LoadBalancerType type;
    private LoadBalancerPersistenceType persistence;
    private LoadBalancerOs os;
    private LoadBalancerState state;

    /**
     * A no-args constructor is required for deserialization
     */
    public LoadBalancer() {
    }

    public LoadBalancer(long id, String name, String description,
                        IpPortPair virtualIp, Set<IpPortPair> realIpList, LoadBalancerType type,
                        LoadBalancerPersistenceType persistence, LoadBalancerOs os,
                        LoadBalancerState state) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.virtualIp = virtualIp;
        this.realIpList = realIpList;
        this.type = type;
        this.persistence = persistence;
        this.os = os;
        this.state = state;
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

    public IpPortPair getVirtualIp() {
        return virtualIp;
    }

    public Set<IpPortPair> getRealIpList() {
        return realIpList;
    }

    public LoadBalancerType getType() {
        return type;
    }

    public LoadBalancerPersistenceType getPersistence() {
        return persistence;
    }

    public LoadBalancerOs getOs() {
        return os;
    }

    public LoadBalancerState getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoadBalancer that = (LoadBalancer) o;

        if (id != that.id) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (os != null ? !os.equals(that.os) : that.os != null) return false;
        if (persistence != null ? !persistence.equals(that.persistence) : that.persistence != null) return false;
        if (realIpList != null ? !realIpList.equals(that.realIpList) : that.realIpList != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (virtualIp != null ? !virtualIp.equals(that.virtualIp) : that.virtualIp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (virtualIp != null ? virtualIp.hashCode() : 0);
        result = 31 * result + (realIpList != null ? realIpList.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (persistence != null ? persistence.hashCode() : 0);
        result = 31 * result + (os != null ? os.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(LoadBalancer o) {
        return Longs.compare(id, o.getId());
    }
}
