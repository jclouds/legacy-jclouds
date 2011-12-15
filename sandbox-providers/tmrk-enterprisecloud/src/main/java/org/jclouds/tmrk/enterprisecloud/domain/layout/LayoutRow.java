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
import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * LayoutRow is more than a simple wrapper as it extends Resource.
 * <xs:complexType name="LayoutRowType">
 * @author Jason King
 * 
 */
public class LayoutRow extends Resource<LayoutRow> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromLayoutRow(this);
   }

   public static class Builder extends Resource.Builder<LayoutRow> {
      private int index;
      private Set<LayoutGroup> groups = Sets.newLinkedHashSet();

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.layout.LayoutRow#getIndex
       */
      public Builder index(int index) {
         this.index = index;
         return this;
      }

      /**
       * @see Groups#getGroups
       */
      public Builder groups(Set<LayoutGroup> groups) {
         this.groups = Sets.newLinkedHashSet(checkNotNull(groups, "groups"));
         return this;
      }

      @Override
      public LayoutRow build() {
         return new LayoutRow(href, type, name, links, actions, index, groups);
      }

      public Builder fromLayoutRow(LayoutRow in) {
         return fromResource(in).index(in.getIndex()).groups(in.getGroups());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<LayoutRow> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<LayoutRow> in) {
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

   @XmlElement(name = "Index", required = false)
   private int index;

   @XmlElement(name = "Groups", required = false)
   private Groups groups = Groups.builder().build();
   
   private LayoutRow(URI href, String type, String name, Set<Link> links, Set<Action> actions, int index, Set<LayoutGroup> groups) {
      super(href, type, name, links, actions);
      this.index = index;
      this.groups = Groups.builder().groups(groups).build();
   }

   private LayoutRow() {
       //For JAXB
   }

   public int getIndex() {
      return index;
   }

   public Set<LayoutGroup> getGroups() {
      return groups.getGroups();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      LayoutRow layoutRow = (LayoutRow) o;

      if (index != layoutRow.index) return false;
      if (!groups.equals(layoutRow.groups)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + index;
      result = 31 * result + groups.hashCode();
      return result;
   }

   @Override
   public String string() {
      return super.string()+", index="+index+", groups="+groups;
   }
}