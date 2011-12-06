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
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;

import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <xs:complexType name="ComputePoolCpuUsageDetailSummaryEntry">
 * @author Jason King
 * 
 */
public class ComputePoolCpuUsageDetailSummaryEntry {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   public Builder toBuilder() {
      return new Builder().ComputePoolCpuUsageDetailSummaryEntry(this);
   }

   public static class Builder {
      private Date time;
      private ResourceCapacity value;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolCpuUsageDetailSummaryEntry#getTime
       */
      public Builder time(Date time) {
         this.time = time;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolCpuUsageDetailSummaryEntry#getValue
       */
      public Builder value(ResourceCapacity value) {
         this.value = value;
         return this;
      }

      public ComputePoolCpuUsageDetailSummaryEntry build() {
         return new ComputePoolCpuUsageDetailSummaryEntry(time,value);
      }

      public Builder ComputePoolCpuUsageDetailSummaryEntry(ComputePoolCpuUsageDetailSummaryEntry in) {
         return time(in.getTime()).value(in.getValue());
      }
   }

   @XmlElement(name = "Time", required = true)
   private Date time;

   @XmlElement(name = "Value", required = false)
   private ResourceCapacity value;


   private ComputePoolCpuUsageDetailSummaryEntry(Date time,@Nullable ResourceCapacity value) {
      this.time = checkNotNull(time, "time");
      this.value = value;
   }

   private ComputePoolCpuUsageDetailSummaryEntry() {
       //For JAXB
   }

   public Date getTime() {
      return time;
   }

   public ResourceCapacity getValue() {
      return value;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ComputePoolCpuUsageDetailSummaryEntry that = (ComputePoolCpuUsageDetailSummaryEntry) o;

      if (!time.equals(that.time)) return false;
      if (value != null ? !value.equals(that.value) : that.value != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = time.hashCode();
      result = 31 * result + (value != null ? value.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[time="+ time +", value="+value+"]";
   }

}