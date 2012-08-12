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

import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Objects;

/**
 * Represents a product for which usage information can be queried.
 * 
 * @author Dies Koper
 */
public class Product {
    @XmlElement(name = "productName")
    private String name;

    private String unitName;

    private String usedPoints;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the unitName
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * @return the usedPoints
     */
    public String getUsedPoints() {
        return usedPoints;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, unitName, usedPoints);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Product that = Product.class.cast(obj);
        return Objects.equal(this.name, that.name)
                && Objects.equal(this.unitName, that.unitName)
                && Objects.equal(this.usedPoints, that.usedPoints);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues().add("name", name)
                .add("unitName", unitName).add("usedPoints", usedPoints)
                .toString();
    }
}
