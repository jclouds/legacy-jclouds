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

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.*;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Returns a representation of the current session that can serve as a single entry point to the
 * system, as it provides user, admin, and extension (sysadmin) entry links depending on the
 * privileges of the current user.
 * 
 * @author Adrian Cole
 */
@XmlRootElement(namespace = NS, name = "Session")
@XmlAccessorType(XmlAccessType.FIELD)
public class Session {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromSession(this);
   }

   public static class Builder {
      private String user;
      private String org;
      private URI href;
      private Set<Link> links = Sets.newLinkedHashSet();

      /**
       * @see Session#getUser()
       */
      public Builder user(String user) {
         this.user = user;
         return this;
      }

      /**
       * @see Session#getOrg()
       */
      public Builder org(String org) {
         this.org = org;
         return this;
      }

      /**
       * @see Session#getHref()
       */
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see Session#getLinks()
       */
      public Builder links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see Session#getLinks()
       */
      public Builder addLink(Link link) {
         links.add(checkNotNull(link, "link"));
         return this;
      }

      public Session build() {
         return new Session(user, org, href, links);
      }

      public Builder fromSession(Session in) {
         return user(in.getUser()).org(in.getOrg()).href(in.getHref()).links(in.getLinks());
      }
   }

   private Session() {
      // For JAXB and builder use
   }

   private Session(String user, String org, URI href, Set<Link> links) {
      this.user = user;
      this.org = org;
      this.href = href;
      this.links = ImmutableSet.copyOf(links);
   }

   @XmlElement(namespace = NS, name = "Link")
   private Set<Link> links = Sets.newLinkedHashSet();
   @XmlAttribute
   private String user;
   @XmlAttribute
   private String org;
   @XmlAttribute
   private URI href;

   public Set<Link> getLinks() {
      return ImmutableSet.copyOf(links);
   }

   /**
    * 
    * @return the user's login name.
    */
   public String getUser() {
      return user;
   }

   /**
    * 
    * @return is the name of an organization of which the user is a member
    */
   public String getOrg() {
      return org;
   }

   /**
    * 
    * @return a reference to the current session
    */
   public URI getHref() {
      return href;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Session that = Session.class.cast(o);
      return equal(user, that.user) && equal(org, that.org) && equal(href, that.href) && equal(links, that.links);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(org, user, href, links);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("user", user).add("org", org).add("href", href).add("links", links)
               .toString();
   }
}
