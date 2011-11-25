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
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Actions;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.Links;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * Location of a Rest resource
 * 
 * @author Adrian Cole
 * 
 */
public class BaseResource<T extends BaseResource<T>> {

   public static <T extends BaseResource<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromResource(this);
   }

   public static class Builder<T extends BaseResource<T>> {

      protected String type;
      protected URI href;
      protected Set<Link> links = Sets.newLinkedHashSet();
      protected Set<Action> actions = Sets.newLinkedHashSet();

      /**
       * @see BaseResource#getType
       */
      public Builder<T> type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see BaseResource#getHref
       */
      public Builder<T> href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see BaseResource#getLinks
       */
      public Builder links(Set<Link> links) {
         this.links = ImmutableSet.<Link> copyOf(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see BaseResource#getActions
       */
      public Builder actions(Set<Action> actions) {
         this.actions = ImmutableSet.<Action> copyOf(checkNotNull(actions, "actions"));
         return this;
      }

      public BaseResource<T> build() {
         return new BaseResource<T>(href, type, links, actions);
      }

      public Builder<T> fromResource(BaseResource<T> in) {
         return type(in.getType()).href(in.getHref()).links(in.getLinks()).actions(in.getActions());
      }

      public Builder<T> fromAttributes(Map<String, String> attributes) {
         return href(URI.create(attributes.get("href"))).type(attributes.get("type"));
      }
      
   }

   @XmlAttribute
   protected String type;

   @XmlAttribute
   protected URI href;

   @XmlElement(name = "Links", required = false)
   protected Links links = Links.builder().build();

   @XmlElement(name = "Actions", required = false)
   protected Actions actions = Actions.builder().build();

   public BaseResource(URI href, String type, Set<Link> links, Set<Action> actions) {
      this.type = checkNotNull(type, "type");
      this.href = checkNotNull(href, "href");
      this.links = Links.builder().links(checkNotNull(links,"links")).build();
      this.actions = Actions.builder().actions(checkNotNull(actions, "actions")).build();
   }

   protected BaseResource() {
      //For JAXB
   }

   /**
    * 
    * @return type definition, type, expressed as an HTTP Content-Type
    */
   public String getType() {
      return type;
   }

   /**
    * 
    * @return an opaque reference and should never be parsed
    */
   public URI getHref() {
      return href;
   }

   /**
    * @return the links related to this object
    */
   public Set<Link> getLinks() {
      return links.getLinks();
   }

   /**
    * @return the actions available from this object
    */
   public Set<Action> getActions() {
      return actions.getActions();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      BaseResource that = (BaseResource) o;

      if (actions != null ? !actions.equals(that.actions) : that.actions != null)
         return false;
      if (href != null ? !href.equals(that.href) : that.href != null)
         return false;
      if (links != null ? !links.equals(that.links) : that.links != null)
         return false;
      if (type != null ? !type.equals(that.type) : that.type != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = type != null ? type.hashCode() : 0;
      result = 31 * result + (href != null ? href.hashCode() : 0);
      result = 31 * result + (links != null ? links.hashCode() : 0);
      result = 31 * result + (actions != null ? actions.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return String.format("[%s]",string());
   }

   protected String string() {
       return "href="+href+", type="+type+", links="+links+", actions="+actions;
   }
}