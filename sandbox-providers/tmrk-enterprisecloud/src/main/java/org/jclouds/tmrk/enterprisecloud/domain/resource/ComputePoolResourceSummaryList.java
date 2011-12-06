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
package org.jclouds.tmrk.enterprisecloud.domain.resource;

import com.google.common.collect.Sets;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ComputePoolResourceSummaryList is more than a simple wrapper as it extends Resource.
 * <xs:complexType name="ComputePoolResourceSummaryList">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "ComputePoolResourceSummaryList")
public class ComputePoolResourceSummaryList extends Resource<ComputePoolResourceSummaryList> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromSummaryList(this);
   }

   public static class Builder extends Resource.Builder<ComputePoolResourceSummaryList> {
      private Set<ComputePoolResourceSummary> summaries = Sets.newLinkedHashSet();

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummaryList#getComputePoolResourceSummaries
       */
      public Builder summaries(Set<ComputePoolResourceSummary> summaries) {
         this.summaries =(checkNotNull(summaries,"summaries"));
         return this;
      }

      @Override
      public ComputePoolResourceSummaryList build() {
         return new ComputePoolResourceSummaryList(href, type, name, links, actions, summaries);
      }

      public Builder fromSummaryList(ComputePoolResourceSummaryList in) {
         return fromResource(in).summaries(in.getComputePoolResourceSummaries());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<ComputePoolResourceSummaryList> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<ComputePoolResourceSummaryList> in) {
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

   @XmlElement(name = "ComputePoolResourceSummary")
   private Set<ComputePoolResourceSummary> summaries = Sets.newLinkedHashSet();

   private ComputePoolResourceSummaryList(URI href, String type, String name, Set<Link> links, Set<Action> actions, Set<ComputePoolResourceSummary> summaries) {
      super(href, type, name, links, actions);
      this.summaries = checkNotNull(summaries,"summaries");
   }

   private ComputePoolResourceSummaryList() {
       //For JAXB
   }

   public Set<ComputePoolResourceSummary> getComputePoolResourceSummaries() {
      return summaries;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      ComputePoolResourceSummaryList that = (ComputePoolResourceSummaryList) o;

      if (!summaries.equals(that.summaries)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + summaries.hashCode();
      return result;
   }

   @Override
   public String string() {
      return super.string()+", summaries="+summaries;
   }
}