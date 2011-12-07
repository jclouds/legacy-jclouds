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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * <xs:complexType name="ComputePoolPerformanceStatistics">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "ComputePoolPerformanceStatistics")
public class ComputePoolPerformanceStatistics extends Resource<ComputePoolPerformanceStatistics> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromComputePoolResourceSummary(this);
   }

   public static class Builder extends Resource.Builder<ComputePoolPerformanceStatistics> {
      ComputePoolPerformanceStatistic hourly;
      ComputePoolPerformanceStatistic daily;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolPerformanceStatistics#getHourly
       */
      public Builder hourly(ComputePoolPerformanceStatistic hourly) {
         this.hourly = hourly;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolPerformanceStatistics#getDaily
       */
      public Builder daily(ComputePoolPerformanceStatistic daily) {
         this.daily = daily;
         return this;
      }

      @Override
      public ComputePoolPerformanceStatistics build() {
         return new ComputePoolPerformanceStatistics(href, type, name, links, actions, hourly, daily);
      }

      public Builder fromComputePoolResourceSummary(ComputePoolPerformanceStatistics in) {
         return fromResource(in).hourly(in.getHourly()).daily(in.getDaily());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<ComputePoolPerformanceStatistics> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<ComputePoolPerformanceStatistics> in) {
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

   @XmlElement(name = "Hourly", required = false)
   ComputePoolPerformanceStatistic hourly;

   @XmlElement(name = "Daily", required = false)
   ComputePoolPerformanceStatistic daily;

   private ComputePoolPerformanceStatistics(URI href, String type, String name, Set<Link> links, Set<Action> actions,
                                            @Nullable ComputePoolPerformanceStatistic hourly, @Nullable ComputePoolPerformanceStatistic daily) {
      super(href, type, name, links, actions);
      this.hourly = hourly;
      this.daily = daily;
   }

   private ComputePoolPerformanceStatistics() {
       //For JAXB
   }

   public ComputePoolPerformanceStatistic getHourly() {
      return hourly;
   }

   public ComputePoolPerformanceStatistic getDaily() {
      return daily;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      ComputePoolPerformanceStatistics that = (ComputePoolPerformanceStatistics) o;

      if (daily != null ? !daily.equals(that.daily) : that.daily != null)
         return false;
      if (hourly != null ? !hourly.equals(that.hourly) : that.hourly != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (hourly != null ? hourly.hashCode() : 0);
      result = 31 * result + (daily != null ? daily.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", hourly="+hourly+", daily="+daily;
   }
}