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

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.ovf.RASD;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Represents a list of RASD items.
 *
 * <pre>
 * &lt;complexType name="RasdItemsList" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlType(name = "RasdItemsList")
public class RasdItemsList extends ResourceType<RasdItemsList> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromRasdItemsList(this);
   }

   public static class Builder extends ResourceType.Builder<RasdItemsList> {

      private List<RASD> items = Lists.newArrayList();

      /**
       * @see RasdItemsList#getItems()
       */
      public Builder items(List<RASD> items) {
         this.items = checkNotNull(items, "items");
         return this;
      }

      /**
       * @see RasdItemsList#getItems()
       */
      public Builder item(RASD item) {
         this.items.add(checkNotNull(item, "item"));
         return this;
      }

      @Override
      public RasdItemsList build() {
         RasdItemsList rasdItemsList = new RasdItemsList(href, type, links, items);
         return rasdItemsList;
      }

      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         super.links(Sets.newLinkedHashSet(checkNotNull(links, "links")));
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         super.link(link);
         return this;
      }

      @Override
      public Builder fromResourceType(ResourceType<RasdItemsList> in) {
         return Builder.class.cast(super.fromResourceType(in));
      }

      public Builder fromRasdItemsList(RasdItemsList in) {
         return fromResourceType(in).items(in.getItems());
      }
   }

   protected RasdItemsList() {
      // For JAXB and builder use
   }

   public RasdItemsList(URI href, String type, Set<Link> links, List<RASD> items) {
      super(href, type, links);
      this.items = items;
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
