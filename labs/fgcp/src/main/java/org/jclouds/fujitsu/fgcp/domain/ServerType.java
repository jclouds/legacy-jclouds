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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Describes the hardware of a virtual server.
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "servertype")
public class ServerType {
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

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    public String getExpectedUsage() {
        return expectedUsage;
    }

    public void setExpectedUsage(String expectedUsage) {
        this.expectedUsage = expectedUsage;
    }

    public CPU getCpu() {
        return cpu;
    }

    public void setCpu(CPU cpu) {
        this.cpu = cpu;
    }

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public Set<Disk> getDisks() {
        return disks == null ? ImmutableSet.<Disk> of() : ImmutableSet
                .copyOf(disks);
    }

    public void setDisks(Set<Disk> disks) {
        this.disks = disks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServerType))
            return false;

        ServerType that = (ServerType) o;

        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;
        if (productId != null ? !productId.equals(that.productId)
                : that.productId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (productId != null ? productId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ServerType{" + "id='" + id + '\'' + ", name='" + name + '\''
                + ", label='" + label + '\'' + ", comment='" + comment + '\''
                + ", productId='" + productId + '\'' + ", productName='"
                + productName + '\'' + ", price='" + price + '\''
                + ", chargeType='" + chargeType + '\'' + ", expectedUsage='"
                + expectedUsage + '\'' + ", cpu=" + cpu + ", memory=" + memory
                + ", disks=" + disks + '}';
    }
}
