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
package org.jclouds.tmrk.enterprisecloud.domain.resource.cpu;

import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps individual ComputePoolCpuUsageDetailSummaryEntry elements.
 * <xs:complexType name="CpuUsageDetails">
 * @author Jason King
 */
public class CpuUsageDetails {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromCpuUsageDetails(this);
   }

   public static class Builder {

       private Set<ComputePoolCpuUsageDetailSummaryEntry> entries = Sets.newLinkedHashSet();

       /**
        * @see CpuUsageDetails#getEntries
        */
       public Builder entries(Set<ComputePoolCpuUsageDetailSummaryEntry> entries) {
          this.entries = Sets.newLinkedHashSet(checkNotNull(entries, "entries"));
          return this;
       }

       public Builder addEntry(ComputePoolCpuUsageDetailSummaryEntry entry) {
          entries.add(checkNotNull(entry,"entry"));
          return this;
       }

       public CpuUsageDetails build() {
           return new CpuUsageDetails(entries);
       }

       public Builder fromCpuUsageDetails(CpuUsageDetails in) {
         return entries(in.getEntries());
       }
   }

   private CpuUsageDetails() {
      //For JAXB and builder use
   }

   private CpuUsageDetails(Set<ComputePoolCpuUsageDetailSummaryEntry> entries) {
      this.entries = Sets.newLinkedHashSet(entries);
   }

   @XmlElement(name = "CpuUsageDetail", required=false)
   private Set<ComputePoolCpuUsageDetailSummaryEntry> entries = Sets.newLinkedHashSet();

   public Set<ComputePoolCpuUsageDetailSummaryEntry> getEntries() {
      return Collections.unmodifiableSet(entries);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      CpuUsageDetails tasks1 = (CpuUsageDetails) o;

      if (!entries.equals(tasks1.entries)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return entries.hashCode();
   }

   public String toString() {
      return "["+ entries.toString()+"]";
   }
}
