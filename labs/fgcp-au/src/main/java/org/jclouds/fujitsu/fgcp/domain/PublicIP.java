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
package org.jclouds.fujitsu.fgcp.domain;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a public IP address.
 * <p>
 * A public IP address can be allocated to a virtual system, then needs to be
 * enabled/attached before it can be mapped to a virtual server by configuring
 * the NAT settings of virtual system's firewall.
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "publicip")
public class PublicIP {
    private String address;
    private String v4v6Flag;
    private String vsysId;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getV4v6Flag() {
        return v4v6Flag;
    }

    public void setV4v6Flag(String v4v6Flag) {
        this.v4v6Flag = v4v6Flag;
    }

    public String getVsysId() {
        return vsysId;
    }

    public void setVsysId(String vsysId) {
        this.vsysId = vsysId;
    }

    @Override
    public String toString() {
        return "PublicIP{" + "address='" + address + '\'' + ", v4v6Flag='"
                + v4v6Flag + '\'' + ", vsysId='" + vsysId + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PublicIP))
            return false;

        PublicIP publicIP = (PublicIP) o;

        if (address != null ? !address.equals(publicIP.address)
                : publicIP.address != null)
            return false;
        if (v4v6Flag != null ? !v4v6Flag.equals(publicIP.v4v6Flag)
                : publicIP.v4v6Flag != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (v4v6Flag != null ? v4v6Flag.hashCode() : 0);
        return result;
    }
}
