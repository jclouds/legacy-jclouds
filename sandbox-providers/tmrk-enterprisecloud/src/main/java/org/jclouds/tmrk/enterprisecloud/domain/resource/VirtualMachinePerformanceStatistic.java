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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;

import javax.xml.bind.annotation.XmlElement;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * VirtualMachinePerformanceStatistic is more than a simple wrapper as it extends BaseResource.
 * <xs:complexType name="VirtualMachinePerformanceStatistic">
 * @author Jason King
 * 
 */
public class VirtualMachinePerformanceStatistic extends Resource<VirtualMachinePerformanceStatistic> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromVirtualMachinePerformanceStatistic(this);
   }

   public static class Builder extends Resource.Builder<VirtualMachinePerformanceStatistic> {
      private Set<PerformanceStatistic> statistics = Sets.newLinkedHashSet();

      /**
       * @see VirtualMachinePerformanceStatistic#getStatistics
       */
      public Builder statistics(Set<PerformanceStatistic> statistics) {
         this.statistics =(checkNotNull(statistics,"statistics"));
         return this;
      }

      @Override
      public VirtualMachinePerformanceStatistic build() {
         return new VirtualMachinePerformanceStatistic(href, type, name, links, actions, statistics);
      }

      public Builder fromVirtualMachinePerformanceStatistic(VirtualMachinePerformanceStatistic in) {
         return fromResource(in).statistics(in.getStatistics());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<VirtualMachinePerformanceStatistic> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<VirtualMachinePerformanceStatistic> in) {
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

   @XmlElement(name = "PerformanceStatistic", required = false)
   private Set<PerformanceStatistic> statistics = Sets.newLinkedHashSet();

   private VirtualMachinePerformanceStatistic(URI href, String type, String name, Set<Link> links, Set<Action> actions, Set<PerformanceStatistic> statistics) {
      super(href, type, name, links, actions);
      this.statistics = ImmutableSet.copyOf(statistics);
   }

   private VirtualMachinePerformanceStatistic() {
       //For JAXB
   }

   public Set<PerformanceStatistic> getStatistics() {
      return Collections.unmodifiableSet(statistics);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      VirtualMachinePerformanceStatistic templates = (VirtualMachinePerformanceStatistic) o;

      if (statistics != null ? !statistics.equals(templates.statistics) : templates.statistics != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (statistics != null ? statistics.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", statistics="+ statistics;
   }
}