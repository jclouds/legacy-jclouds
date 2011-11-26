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
package org.jclouds.tmrk.enterprisecloud.domain.internal;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Actions;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.Links;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base Resource class.
 * has Links and Actions and name (plus href and type are inherited)
 * <xs:complexType name="Resource">
 * @author Jason King
 * 
 */
public class Resource<T extends Resource<T>> extends BaseResource<T> {

   public static <T extends Resource<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromResource(this);
   }

   public static class Builder<T extends Resource<T>> extends BaseResource.Builder<T> {

      protected Set<Link> links = Sets.newLinkedHashSet();
      protected Set<Action> actions = Sets.newLinkedHashSet();
      protected String name;

     /**
       * @see Resource#getLinks
       */
      public Builder<T> links(Set<Link> links) {
         this.links = ImmutableSet.<Link> copyOf(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see Resource#getActions
       */
      public Builder<T> actions(Set<Action> actions) {
         this.actions = ImmutableSet.<Action> copyOf(checkNotNull(actions, "actions"));
         return this;
      }

      /**
       * @see Resource#getActions
       */
      public Builder<T> name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      public Resource<T> build() {
         return new Resource<T>(href, type, name, links, actions);
      }

      /**
       * {@inheritDoc}
       */
      public Builder<T> fromBaseResource(BaseResource<T> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      public Builder<T> fromResource(Resource<T> in) {
         return fromBaseResource(in).links(in.getLinks())
               .actions(in.getActions()).name(in.getName());
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      public Builder<T> fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }
   }

   @XmlElement(name = "Links", required = false)
   protected Links links = Links.builder().build();

   @XmlElement(name = "Actions", required = false)
   protected Actions actions = Actions.builder().build();

   @XmlAttribute(required = false)
   protected String name;

   protected Resource(URI href, String type, @Nullable String name, Set<Link> links, Set<Action> actions) {
      super(href, type);
      this.name = name;
      this.links = Links.builder().links(checkNotNull(links,"links")).build();
      this.actions = Actions.builder().actions(checkNotNull(actions, "actions")).build();
   }

   protected Resource() {
      //For JAXB
   }

   public Set<Link> getLinks() {
      return Collections.unmodifiableSet(links.getLinks());
   }

   public Set<Action> getActions() {
      return Collections.unmodifiableSet(actions.getActions());
   }

   public String getName() {
      return name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      Resource resource = (Resource) o;

      if (actions != null ? !actions.equals(resource.actions) : resource.actions != null)
         return false;
      if (links != null ? !links.equals(resource.links) : resource.links != null)
         return false;
      if (name != null ? !name.equals(resource.name) : resource.name != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (links != null ? links.hashCode() : 0);
      result = 31 * result + (actions != null ? actions.hashCode() : 0);
      result = 31 * result + (name != null ? name.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
     return super.string()+", name="+name+", links="+links+", actions="+actions;
   }
}