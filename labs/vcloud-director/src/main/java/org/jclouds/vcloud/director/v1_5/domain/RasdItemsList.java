/*
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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.ovf.RASD;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Lists;

/**
 * Represents a list of RASD items.
 *
 * <pre>
 * &lt;complexType name="RasdItemsList" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "RasdItemsList")
@XmlType(name = "RasdItemsList")
public class RasdItemsList extends ResourceType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromRasdItemsList(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static abstract class Builder<B extends Builder<B>> extends ResourceType.Builder<B> {

      private List<RASD> items = Lists.newArrayList();

      /**
       * @see RasdItemsList#getItems()
       */
      public B items(List<RASD> items) {
         this.items = checkNotNull(items, "items");
         return self();
      }

      /**
       * @see RasdItemsList#getItems()
       */
      public B item(RASD item) {
         this.items.add(checkNotNull(item, "item"));
         return self();
      }

      @Override
      public RasdItemsList build() {
         RasdItemsList rasdItemsList = new RasdItemsList(this);
         return rasdItemsList;
      }

      public B fromRasdItemsList(RasdItemsList in) {
         return fromResourceType(in).items(in.getItems());
      }
   }

   protected RasdItemsList() {
      // For JAXB and B use
   }

   protected RasdItemsList(Builder<?> builder) {
      super(builder);
      this.items = builder.items;
   }

   @XmlElement(name = "Item")
   protected List<RASD> items = Lists.newArrayList();

   /**
    * A RASD item content.
    */
   public List<RASD> getItems() {
      return items;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      RasdItemsList that = RasdItemsList.class.cast(o);
      return super.equals(that) && equal(this.items, that.items);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), items);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("items", items);
   }

}
