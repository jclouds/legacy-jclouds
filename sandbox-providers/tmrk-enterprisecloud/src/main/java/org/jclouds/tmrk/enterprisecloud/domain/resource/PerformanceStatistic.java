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

import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <xs:complexType name="PerformanceStatistic">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "PerformanceStatistic")
public class PerformanceStatistic {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromPerformanceStatistic(this);
   }

   public static class Builder {
      private Date time;
      private ResourceCapacity used;

      /**
       * @see PerformanceStatistic#getTime
       */
      public Builder time(Date time) {
         this.time =(checkNotNull(time,"time"));
         return this;
      }

      /**
       * @see PerformanceStatistic#getUsed
       */
      public Builder used(ResourceCapacity used) {
         this.used =(checkNotNull(used,"used"));
         return this;
      }
      
      public PerformanceStatistic build() {
         return new PerformanceStatistic(time,used);
      }

      public Builder fromPerformanceStatistic(PerformanceStatistic in) {
         return time(in.getTime()).used(in.getUsed());
      }
   }

   @XmlElement(name = "Time", required = true)
   private Date time;

   @XmlElement(name = "Used", required = false)
   private ResourceCapacity used;
   
   private PerformanceStatistic(Date time, ResourceCapacity used) {
      this.time = checkNotNull(time, "time");
      this.used = used;
   }

   private PerformanceStatistic() {
       //For JAXB
   }

   public Date getTime() {
      return time;
   }

   public ResourceCapacity getUsed() {
      return used;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      PerformanceStatistic that = (PerformanceStatistic) o;

      if (!time.equals(that.time)) return false;
      if (used != null ? !used.equals(that.used) : that.used != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = time.hashCode();
      result = 31 * result + (used != null ? used.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[time="+time+", used="+used+"]";
   }
}