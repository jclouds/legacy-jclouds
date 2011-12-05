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
package org.jclouds.tmrk.enterprisecloud.domain;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * <xs:complexType name="Location">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "Location")
public class Location extends Resource<Location> {


   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromTask(this);
   }

   public static class Builder extends Resource.Builder<Location> {
      private String friendlyName;
      private String locode;
      private String iso3166;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.Location#getFriendlyName
       */
      public Builder friendlyName(String friendlyName) {
         this.friendlyName = friendlyName;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.Location#getLocode
       */
      public Builder locode(String locode) {
         this.locode = locode;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.Location#getIso3166
       */
      public Builder iso3166(String iso3166) {
         this.iso3166 = iso3166;
         return this;
      }

      @Override
      public Location build() {
         return new Location(href, type, name, links,
               actions, friendlyName, locode, iso3166);
      }

      public Builder fromTask(Location in) {
         return fromResource(in).friendlyName(in.getFriendlyName()).locode(in.getLocode()).iso3166(in.getIso3166());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<Location> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<Location> in) {
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

   @XmlElement(name = "FriendlyName", required = true)
   private String friendlyName;

   @XmlElement(name = "Locode", required = false)
   private String locode;

   @XmlElement(name = "ISO3166", required = false)
   private String iso3166;

   private Location(URI href, String type, String name, Set<Link> links, Set<Action> actions, @Nullable String friendlyName, @Nullable String locode, @Nullable String iso3166) {
      super(href, type, name, links, actions);
      this.friendlyName = friendlyName;
      this.locode = locode;
      this.iso3166 = iso3166;
   }

   private Location() {
      //For JAXB
   }

   public String getFriendlyName() {
      return friendlyName;
   }

   public String getLocode() {
      return locode;
   }

   public String getIso3166() {
      return iso3166;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      Location location = (Location) o;

      if (friendlyName != null ? !friendlyName.equals(location.friendlyName) : location.friendlyName != null)
         return false;
      if (iso3166 != null ? !iso3166.equals(location.iso3166) : location.iso3166 != null)
         return false;
      if (locode != null ? !locode.equals(location.locode) : location.locode != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (friendlyName != null ? friendlyName.hashCode() : 0);
      result = 31 * result + (locode != null ? locode.hashCode() : 0);
      result = 31 * result + (iso3166 != null ? iso3166.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", friendlyName="+friendlyName+", locode="+locode+", iso3166="+iso3166;
   }
}