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
package org.jclouds.vcloud.director.v1_5.domain;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Admin representation of the container for meta data (key-value pair) associated to different
 * entities in the system.
 *             
 * <pre>
 * &lt;complexType name="AdminCatalog">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}CatalogType">
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "AdminCatalog")
@XmlType(name = "AdminCatalogType")
public class AdminCatalog extends Catalog {
   
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromAdminCatalog(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Catalog.Builder<B> {
      
      @Override
      public AdminCatalog build() {
         return new AdminCatalog(this);
      }

      public B fromAdminCatalog(AdminCatalog in) {
         return fromCatalogType(in);
      }
   }

   @SuppressWarnings("unused")
   private AdminCatalog() {
      // For JAXB
   }
   
   protected AdminCatalog(Builder<?> builder) {
      super(builder);
   }
}
