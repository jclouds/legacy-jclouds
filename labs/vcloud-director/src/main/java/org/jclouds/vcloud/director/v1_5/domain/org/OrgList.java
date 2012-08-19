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
package org.jclouds.vcloud.director.v1_5.domain.org;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Reference;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * A list of organizations.
 *
 * @author Adrian Cole
 */
@XmlRootElement(name = "OrgList")
public class OrgList extends ForwardingSet<Reference> {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.ORG_LIST;

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromOrgList(this);
   }

   public static class Builder {

      private Set<Reference> orgs = Sets.newLinkedHashSet();

      /**
       * @see OrgList#getOrgs
       */
      public Builder orgs(Set<Reference> orgs) {
         this.orgs = Sets.newLinkedHashSet(checkNotNull(orgs, "orgs"));
         return this;
      }

      /**
       * @see OrgList#getOrgs
       */
      public Builder org(Reference org) {
         orgs.add(checkNotNull(org, "org"));
         return this;
      }

      public OrgList build() {
         return new OrgList(orgs);
      }

      public Builder fromOrgList(OrgList in) {
         return orgs(in.delegate());
      }
   }

   private OrgList() {
      // for JAXB
   }

   private OrgList(Set<Reference> orgs) {
      this.orgs = ImmutableSet.copyOf(orgs);
   }

   @XmlElement(name = "Org")
   private Set<Reference> orgs = Sets.newLinkedHashSet();

   @Override
   protected Set<Reference> delegate() {
      return Collections.unmodifiableSet(orgs);
   }

}
