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

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Describes the hardware of a virtual server.
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "servertype")
public class ServerType implements Comparable<ServerType> {
    private String id;

    private String name;

    private String label;

    private String comment;

    private String productId;

    private String productName;

    private String price;

    private String chargeType;

    private String expectedUsage;

    private CPU cpu;

    private Memory memory;

    @XmlElementWrapper(name = "disks")
    @XmlElement(name = "disk")
    private Set<Disk> disks = Sets.newLinkedHashSet();

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getComment() {
        return comment;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getPrice() {
        return price;
    }

    public String getChargeType() {
        return chargeType;
    }

    public String getExpectedUsage() {
        return expectedUsage;
    }

    public CPU getCpu() {
        return cpu;
    }

    public Memory getMemory() {
        return memory;
    }

    public Set<Disk> getDisks() {
        return disks == null ? ImmutableSet.<Disk> of() : ImmutableSet
                .copyOf(disks);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServerType that = ServerType.class.cast(obj);
        return Objects.equal(this.id, that.id);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues().add("id", id)
                .add("name", name).add("label", label).add("comment", comment)
                .add("productId", productId).add("productName", productName)
                .add("price", price).add("chargeType", chargeType)
                .add("expectedUsage", expectedUsage).add("cpu", cpu)
                .add("memory", memory).add("disks", disks).toString();
    }

    @Override
    public int compareTo(ServerType o) {
        return memory == null ? -1 : memory.compareTo(o.memory);
    }

}
