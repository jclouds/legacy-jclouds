/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.gogrid.domain;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * @author Oleksiy Yarmula
 */
public class IpPortPair implements Comparable<IpPortPair> {

    private Ip ip;
    private int port;

    /**
     * A no-args constructor is required for deserialization
     */
    public IpPortPair() {
    }

    public IpPortPair(Ip ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Ip getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IpPortPair that = (IpPortPair) o;

        if (port != that.port) return false;
        if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }

    @Override
    public int compareTo(IpPortPair o) {
        if(ip != null && o.getIp() != null) return Longs.compare(ip.getId(), o.getIp().getId());
        return Ints.compare(port, o.getPort());
    }

   @Override
   public String toString() {
      return "IpPortPair [ip=" + ip + ", port=" + port + "]";
   }
}
