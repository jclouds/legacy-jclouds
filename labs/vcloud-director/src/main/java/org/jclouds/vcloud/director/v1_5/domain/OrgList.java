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

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * A list of organizations.
 * 
 * @author Adrian Cole
 */
@XmlRootElement(namespace = NS, name = "OrgList")
public class OrgList {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromOrgList(this);
   }

   public static class Builder {

      private Set<Link> orgs = Sets.newLinkedHashSet();

      /**
       * @see OrgList#getOrgs
       */
      public Builder orgs(Set<Link> orgs) {
         this.orgs = Sets.newLinkedHashSet(checkNotNull(orgs, "orgs"));
         return this;
      }

      /**
       * @see OrgList#getOrgs
       */
      public Builder addOrg(Link org) {
         orgs.add(checkNotNull(org, "org"));
         return this;
      }

      public OrgList build() {
         return new OrgList(orgs);
      }

      public Builder fromOrgList(OrgList in) {
         return orgs(in.getOrgs());
      }
   }

   private OrgList() {
      // For JAXB and builder use
   }

   private OrgList(Set<Link> orgs) {
      this.orgs = ImmutableSet.copyOf(orgs);
   }

   @XmlElement(namespace = NS, name = "Org")
   private Set<Link> orgs = Sets.newLinkedHashSet();

   public Set<Link> getOrgs() {
      return ImmutableSet.copyOf(orgs);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      OrgList that = OrgList.class.cast(o);
      return equal(orgs, that.orgs);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(orgs);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("orgs", orgs).toString();
   }
}
