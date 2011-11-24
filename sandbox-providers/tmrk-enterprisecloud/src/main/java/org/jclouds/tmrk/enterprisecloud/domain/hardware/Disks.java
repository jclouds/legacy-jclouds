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
package org.jclouds.tmrk.enterprisecloud.domain.hardware;

import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps individual Disk elements.
 * Needed because parsing is done with JAXB and it does not handle Generic collections
 * <xs:complexType name="Disks">
 * @author Jason King
 */
public class Disks {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromDisks(this);
   }

   public static class Builder {

       private Set<VirtualDisk> disks = Sets.newLinkedHashSet();

       /**
        * @see Disks#getVirtualDisks()
        */
       public Builder disks(Set<VirtualDisk> disks) {
          this.disks = Sets.newLinkedHashSet(checkNotNull(disks, "disks"));
          return this;
       }

       public Builder addDisk(VirtualDisk disk) {
          disks.add(checkNotNull(disk,"disk"));
          return this;
       }

       public Disks build() {
           return new Disks(disks);
       }

       public Builder fromDisks(Disks in) {
         return disks(in.getVirtualDisks());
       }
   }

    @XmlElement(name = "Disk")
    private LinkedHashSet<VirtualDisk> disks = Sets.newLinkedHashSet();

    private Disks() {
      //For JAXB and builder use
    }

    private Disks(Set<VirtualDisk> disks) {
       this.disks = Sets.newLinkedHashSet(disks);
    }

    public Set<VirtualDisk> getVirtualDisks() {
        return Collections.unmodifiableSet(disks);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Disks disks1 = (Disks) o;

        if (!disks.equals(disks1.disks)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return disks.hashCode();
    }

    public String toString() {
        return "["+ disks.toString()+"]";
    }

}
