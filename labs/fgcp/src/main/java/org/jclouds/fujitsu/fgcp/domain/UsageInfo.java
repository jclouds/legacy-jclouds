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
 * Describes the usage by a virtual system.
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "usageinfo")
public class UsageInfo {
   @XmlElement(name = "vsysId")
   private String systemId;
   @XmlElement(name = "vsysName")
   private String systemName;

   @XmlElementWrapper(name = "products")
   @XmlElement(name = "product")
   private Set<Product> products = Sets.newLinkedHashSet();

   /**
    * @return the systemId
    */
   public String getSystemId() {
      return systemId;
   }

   /**
    * @return the systemName
    */
   public String getSystemName() {
      return systemName;
   }

   /**
    * @return the products
    */
   public Set<Product> getProducts() {
      return products == null ? ImmutableSet.<Product> of() : ImmutableSet
            .copyOf(products);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(systemId, systemName, products);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      UsageInfo that = UsageInfo.class.cast(obj);
      return Objects.equal(this.systemId, that.systemId)
            && Objects.equal(this.systemName, that.systemName)
            && Objects.equal(this.products, that.products);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("systemId", systemId).add("systemName", systemName)
            .add("products", products).toString();
   }
}
