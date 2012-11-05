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
package org.jclouds.fujitsu.fgcp.domain;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Holds information on the system disk image of a virtual server, including the
 * OS and software pre-installed on it.
 * 
 * @author Dies Koper
 */
public class Image {
   private String id;

   private String serverCategory;

   private String serverApplication;

   private String cpuBit;

   private float sysvolSize;

   private int numOfMaxDisk;

   private int numOfMaxNic;

   @XmlElementWrapper(name = "softwares")
   @XmlElement(name = "software")
   private Set<Software> software = Sets.newLinkedHashSet();

   public String getId() {
      return id;
   }

   public String getServerCategory() {
      return serverCategory;
   }

   public String getServerApplication() {
      return serverApplication;
   }

   public String getCpuBit() {
      return cpuBit;
   }

   public float getSysvolSize() {
      return sysvolSize;
   }

   public int getNumOfMaxDisk() {
      return numOfMaxDisk;
   }

   public int getNumOfMaxNic() {
      return numOfMaxNic;
   }

   public Set<Software> getSoftware() {
      return software == null ? ImmutableSet.<Software> of() : ImmutableSet
            .copyOf(software);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Image that = Image.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id)
            .add("serverCategory", serverCategory)
            .add("serverApplication", serverApplication)
            .add("cpuBit", cpuBit).add("sysvolSize", sysvolSize)
            .add("numOfMaxDisk", numOfMaxDisk)
            .add("numOfMaxNic", numOfMaxNic).toString();
   }
}
