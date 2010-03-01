/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.gogrid.domain;

import com.google.common.primitives.Longs;
import com.google.gson.annotations.SerializedName;

/**
 * @author Oleksiy Yarmula
 */
public class Ip implements Comparable<Ip> {

    private long id;

    private String ip;
    private String subnet;
    @SerializedName("public")
    private boolean isPublic;
    private IpState state;

    /**
     * A no-args constructor is required for deserialization
     */
    public Ip() {
    }

    public Ip(long id, String ip, String subnet, boolean isPublic, IpState state) {
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

    public IpState getState() {
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

    @Override
    public String toString() {
        return "Ip{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", subnet='" + subnet + '\'' +
                ", isPublic=" + isPublic +
                ", state=" + state +
                '}';
    }

    @Override
    public int compareTo(Ip o) {
        return Longs.compare(id, o.getId());
    }
}
