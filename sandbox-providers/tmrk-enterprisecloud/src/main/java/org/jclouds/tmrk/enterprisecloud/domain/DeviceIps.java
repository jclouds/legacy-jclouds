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
package org.jclouds.tmrk.enterprisecloud.domain;

import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.Preconditions2.checkNotEmpty;

/**
 * <xs:complexType name="DeviceIps">
 * Wraps individual IpAddresses
 * @author Jason King
 */
public class DeviceIps {

    private LinkedHashSet<String> ipAddresses = Sets.newLinkedHashSet();

    protected DeviceIps() {
        // For JAXB
    }

    @XmlElement(name = "IpAddress")
    void setIpAddress(String ipAddress) {
        checkNotNull(ipAddress,"ipAddress");
        checkNotEmpty(ipAddress, "ipAddress");
        this.ipAddresses.add(ipAddress);
    }

    public Set<String> getIpAddresses() {
        return Collections.unmodifiableSet(ipAddresses);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceIps deviceIps = (DeviceIps) o;

        if (!ipAddresses.equals(deviceIps.ipAddresses)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return ipAddresses.hashCode();
    }

    public String toString() {
        return "["+ ipAddresses.toString()+"]";
    }

}
