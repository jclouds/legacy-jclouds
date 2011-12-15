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
package org.jclouds.tmrk.enterprisecloud.domain.layout;

import com.google.common.collect.Sets;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * DeviceLayout is more than a simple wrapper as it extends Resource.
 * <xs:complexType name="DeviceLayoutType">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "DeviceLayout")
public class DeviceLayout extends Resource<DeviceLayout> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromTemplates(this);
   }

   public static class Builder extends Resource.Builder<DeviceLayout> {
      private Set<LayoutRow> rows = Sets.newLinkedHashSet();

      /**
       * @see DeviceLayout#getRows
       */
      public Builder rows(Set<LayoutRow> rows) {
         this.rows =(checkNotNull(rows,"rows"));
         return this;
      }

      @Override
      public DeviceLayout build() {
         return new DeviceLayout(href, type, name, links, actions, rows);
      }

      public Builder fromTemplates(DeviceLayout in) {
         return fromResource(in).rows(in.getRows());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<DeviceLayout> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<DeviceLayout> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder actions(Set<Action> actions) {
         return Builder.class.cast(super.actions(actions));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }

   }

   @XmlElement(name = "Rows", required = false)
   private Rows rows = Rows.builder().build();

   private DeviceLayout(URI href, String type, String name, Set<Link> links, Set<Action> actions, Set<LayoutRow> rows) {
      super(href, type, name, links, actions);
      this.rows = Rows.builder().rows(checkNotNull(rows,"rows")).build();
   }

   private DeviceLayout() {
       //For JAXB
   }

   public Set<LayoutRow> getRows() {
      return rows.getRows();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      DeviceLayout that = (DeviceLayout) o;

      if (!rows.equals(that.rows)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + rows.hashCode();
      return result;
   }

   @Override
   public String string() {
      return super.string()+", rows="+rows;
   }
}