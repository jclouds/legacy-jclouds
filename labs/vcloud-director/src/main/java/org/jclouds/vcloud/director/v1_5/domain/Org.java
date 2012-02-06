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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NS;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Retrieves a list of organizations.
 * 
 * @author Adrian Cole
 */
@XmlRootElement(namespace = NS, name = "Org")
public class Org extends BaseNamedResource<Org> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromOrg(this);
   }

   public static class Builder extends BaseNamedResource.Builder<Org> {

      private String id;
      private String description;
      private String fullName;
      private Set<Link> links = Sets.newLinkedHashSet();

      /**
       * @see Org#getId
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see Org#getDescription
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see Org#getFullName
       */
      public Builder fullName(String fullName) {
         this.fullName = fullName;
         return this;
      }

      /**
       * @see Org#getOrgs
       */
      public Builder links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see Org#getOrgs
       */
      public Builder addLink(Link org) {
         links.add(checkNotNull(org, "org"));
         return this;
      }

      public Org build() {
         return new Org(href, type, name, id, description, fullName, links);
      }

      public Builder fromOrg(Org in) {
         return id(in.getId()).description(in.getDescription()).fullName(in.getFullName()).links(in.getLinks());
      }

      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }
      
   }

   private Org() {
      // For JAXB and builder use
   }

   private Org(URI href, String type, String name, String id, String description, String fullName, Set<Link> links) {
      super(href, type, name);
      this.id = id;
      this.description = description;
      this.fullName = fullName;
      this.links = ImmutableSet.copyOf(links);
   }

   @XmlAttribute
   private String id;
   @XmlElement(namespace = NS, name = "Description")
   private String description;
   @XmlElement(namespace = NS, name = "FullName")
   private String fullName;
   @XmlElement(namespace = NS, name = "Link")
   private Set<Link> links = Sets.newLinkedHashSet();

   /**
    * 
    * @return id of the org
    */
   public String getId() {
      return id;
   }

   /**
    * 
    * @return description of the org
    */
   public String getDescription() {
      return description;
   }

   /**
    * 
    * @return fullName of the org
    */
   public String getFullName() {
      return fullName;
   }

   /**
    * TODO
    */
   public Set<Link> getLinks() {
      return ImmutableSet.copyOf(links);
   }

   @Override
   public boolean equals(Object o) {
      if (!super.equals(o))
         return false;
      Org that = Org.class.cast(o);
      return equal(id, that.id) && equal(description, that.description) && equal(fullName, that.fullName)
               && equal(links, that.links);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(id, description, fullName, links);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("id", id).add("description", description).add("fullName", fullName).add("links", links);
   }
}
