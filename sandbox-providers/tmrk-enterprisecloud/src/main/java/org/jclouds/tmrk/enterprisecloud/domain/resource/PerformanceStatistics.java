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
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * PerformanceStatistics is more than a simple wrapper as it extends BaseResource.
 * <xs:complexType name="PerformanceStatistics">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "PerformanceStatistics")
public class PerformanceStatistics extends Resource<PerformanceStatistics> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromPerformanceStatistics(this);
   }

   public static class Builder extends Resource.Builder<PerformanceStatistics> {
      private Date startTime;
      private Date endTime;
      private VirtualMachinePerformanceStatistics statistics;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.PerformanceStatistics#getStartTime
       */
      public Builder startTime(Date startTime) {
         this.startTime =(checkNotNull(startTime,"startTime"));
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.PerformanceStatistics#getEndTime
       */
      public Builder endTime(Date endTime) {
         this.endTime =(checkNotNull(endTime,"endTime"));
         return this;
      }
      
      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.PerformanceStatistics#getStatistics
       */
      public Builder statistics(VirtualMachinePerformanceStatistics statistics) {
         this.statistics =(checkNotNull(statistics,"statistics"));
         return this;
      }

      @Override
      public PerformanceStatistics build() {
         return new PerformanceStatistics(href, type, name, links, actions, startTime, endTime, statistics);
      }

      public Builder fromPerformanceStatistics(PerformanceStatistics in) {
         return fromResource(in).startTime(in.getStartTime()).endTime(in.getEndTime()).statistics(in.getStatistics());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<PerformanceStatistics> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<PerformanceStatistics> in) {
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

   @XmlElement(name = "StartTime", required = true)
   private Date startTime;
   
   @XmlElement(name = "EndTime", required = true)
   private Date endTime;
   
   @XmlElement(name = "VirtualMachines", required = false)
   private VirtualMachinePerformanceStatistics statistics;

   private PerformanceStatistics(URI href, String type, String name, Set<Link> links, Set<Action> actions, 
                                 Date startTime, Date endTime, @Nullable VirtualMachinePerformanceStatistics statistics) {
      super(href, type, name, links, actions);
      this.startTime = startTime;
      this.endTime = endTime;
      this.statistics = statistics;
   }

   private PerformanceStatistics() {
       //For JAXB
   }

   public Date getStartTime() {
      return startTime;
   }

   public Date getEndTime() {
      return endTime;
   }

   public VirtualMachinePerformanceStatistics getStatistics() {
      return statistics;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      PerformanceStatistics that = (PerformanceStatistics) o;

      if (!endTime.equals(that.endTime)) return false;
      if (!startTime.equals(that.startTime)) return false;
      if (statistics != null ? !statistics.equals(that.statistics) : that.statistics != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + startTime.hashCode();
      result = 31 * result + endTime.hashCode();
      result = 31 * result + (statistics != null ? statistics.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", startTime="+ startTime+", endTime="+ endTime+", statistics="+ statistics;
   }
}