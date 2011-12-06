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
package org.jclouds.tmrk.enterprisecloud.domain.resource.memory;

import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps individual ComputePoolMemoryUsageDetailSummaryEntry elements.
 * <xs:complexType name="MemoryUsageDetails">
 * @author Jason King
 */
public class MemoryUsageDetails {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromMemoryUsageDetails(this);
   }

   public static class Builder {

       private Set<ComputePoolMemoryUsageDetailSummaryEntry> entries = Sets.newLinkedHashSet();

       /**
        * @see MemoryUsageDetails#getEntries
        */
       public Builder entries(Set<ComputePoolMemoryUsageDetailSummaryEntry> entries) {
          this.entries = Sets.newLinkedHashSet(checkNotNull(entries, "entries"));
          return this;
       }

       public Builder addEntry(ComputePoolMemoryUsageDetailSummaryEntry entry) {
          entries.add(checkNotNull(entry,"entry"));
          return this;
       }

       public MemoryUsageDetails build() {
           return new MemoryUsageDetails(entries);
       }

       public Builder fromMemoryUsageDetails(MemoryUsageDetails in) {
         return entries(in.getEntries());
       }
   }

   private MemoryUsageDetails() {
      //For JAXB and builder use
   }

   private MemoryUsageDetails(Set<ComputePoolMemoryUsageDetailSummaryEntry> entries) {
      this.entries = Sets.newLinkedHashSet(entries);
   }

   @XmlElement(name = "MemoryUsageDetail", required=false)
   private Set<ComputePoolMemoryUsageDetailSummaryEntry> entries = Sets.newLinkedHashSet();

   public Set<ComputePoolMemoryUsageDetailSummaryEntry> getEntries() {
      return Collections.unmodifiableSet(entries);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      MemoryUsageDetails tasks1 = (MemoryUsageDetails) o;

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
