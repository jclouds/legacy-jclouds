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
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ComputePoolCpuUsage is more than a simple wrapper as it extends Resource.
 * <xs:complexType name="ComputePoolCpuUsage">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "ComputePoolCpuUsage")
public class ComputePoolCpuUsage extends Resource<ComputePoolCpuUsage> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromComputePoolCpuUsage(this);
   }

   public static class Builder extends Resource.Builder<ComputePoolCpuUsage> {
      private Date startTime;
      private Date endTime;
      private CpuUsageDetails details;

      /**
       * @see ComputePoolCpuUsage#getStartTime
       */
      public Builder startTime(Date startTime) {
         this.startTime =(checkNotNull(startTime,"startTime"));
         return this;
      }

      /**
       * @see ComputePoolCpuUsage#getEndTime
       */
      public Builder endTime(Date endTime) {
         this.endTime =(checkNotNull(endTime,"endTime"));
         return this;
      }

      /**
       * @see ComputePoolCpuUsage#getDetails
       */
      public Builder details(CpuUsageDetails details) {
         this.details =(checkNotNull(details,"details"));
         return this;
      }

      @Override
      public ComputePoolCpuUsage build() {
         return new ComputePoolCpuUsage(href, type, name, links, actions, startTime, endTime, details);
      }

      public Builder fromComputePoolCpuUsage(ComputePoolCpuUsage in) {
         return fromResource(in).startTime(in.getStartTime()).endTime(in.getEndTime()).details(in.getDetails());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<ComputePoolCpuUsage> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<ComputePoolCpuUsage> in) {
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

   @XmlElement(name = "CpuUsageDetailSummary", required = false)
   private CpuUsageDetails details;

   private ComputePoolCpuUsage(URI href, String type, String name, Set<Link> links, Set<Action> actions, Date startTime, Date endTime, @Nullable CpuUsageDetails details) {
      super(href, type, name, links, actions);
      this.startTime = checkNotNull(startTime, "startTime");
      this.endTime = checkNotNull(endTime, "endTime");
      this.details = details;
   }

   private ComputePoolCpuUsage() {
       //For JAXB
   }

   public Date getStartTime() {
      return startTime;
   }

   public Date getEndTime() {
      return endTime;
   }

   public CpuUsageDetails getDetails() {
      return details;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      ComputePoolCpuUsage that = (ComputePoolCpuUsage) o;

      if (details != null ? !details.equals(that.details) : that.details != null)
         return false;
      if (!endTime.equals(that.endTime)) return false;
      if (!startTime.equals(that.startTime)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + startTime.hashCode();
      result = 31 * result + endTime.hashCode();
      result = 31 * result + (details != null ? details.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", startTime="+startTime+", endTime="+endTime+", details="+details;
   }
}