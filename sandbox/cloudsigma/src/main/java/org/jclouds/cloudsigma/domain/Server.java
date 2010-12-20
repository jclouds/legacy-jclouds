/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudsigma.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class Server extends Item {

   public static class Builder extends Item.Builder {
      protected int cpu;
      protected Integer smp;
      protected int mem;
      protected boolean persistent;
      protected Map<String, ? extends Device> devices = ImmutableMap.of();
      protected Set<String> bootDeviceIds = ImmutableSet.of();
      protected List<NIC> nics = ImmutableList.of();
      protected VNC vnc;
      // TODO cloudsigma specific
      protected String description;

      public Builder cpu(int cpu) {
         this.cpu = cpu;
         return this;
      }

      public Builder smp(Integer smp) {
         this.smp = smp;
         return this;
      }

      public Builder mem(int mem) {
         this.mem = mem;
         return this;
      }

      public Builder persistent(boolean persistent) {
         this.persistent = persistent;
         return this;
      }

      public Builder devices(Map<String, ? extends Device> devices) {
         this.devices = ImmutableMap.copyOf(checkNotNull(devices, "devices"));
         return this;
      }

      public Builder bootDeviceIds(Iterable<String> bootDeviceIds) {
         this.bootDeviceIds = ImmutableSet.copyOf(checkNotNull(bootDeviceIds, "bootDeviceIds"));
         return this;
      }

      public Builder nics(Iterable<NIC> nics) {
         this.nics = ImmutableList.copyOf(checkNotNull(nics, "nics"));
         return this;
      }

      public Builder vnc(VNC vnc) {
         this.vnc = vnc;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder uuid(String uuid) {
         return Builder.class.cast(super.uuid(uuid));
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
      public Builder use(Iterable<String> use) {
         return Builder.class.cast(super.use(use));
      }

      public Server build() {
         return new Server(uuid, name, cpu, smp, mem, persistent, devices, bootDeviceIds, use, nics, vnc, description);
      }

      public static Builder fromServer(Server in) {
         return new Builder().uuid(in.getUuid()).name(in.getName()).cpu(in.getCpu()).mem(in.getMem())
               .persistent(in.isPersistent()).description(in.getDescription()).devices(in.getDevices())
               .bootDeviceIds(in.getBootDeviceIds()).use(in.getUse()).nics(in.getNics()).vnc(in.getVnc());
      }
   }

   protected final int cpu;
   protected final Integer smp;
   protected final int mem;
   protected final boolean persistent;
   @Nullable
   protected final Map<String, Device> devices;
   protected final Set<String> bootDeviceIds;
   protected final List<NIC> nics;
   protected final VNC vnc;
   @Nullable
   private final String description;

   public Server(@Nullable String uuid, String name, int cpu, @Nullable Integer smp, int mem, boolean persistent,
         Map<String, ? extends Device> devices, Iterable<String> bootDeviceIds, Iterable<String> use,
         Iterable<NIC> nics, VNC vnc, String description) {
      super(uuid, name, use);
      this.cpu = cpu;
      this.smp = smp;
      this.mem = mem;
      this.persistent = persistent;
      this.devices = ImmutableMap.copyOf(checkNotNull(devices, "devices"));
      this.bootDeviceIds = ImmutableSet.copyOf(checkNotNull(bootDeviceIds, "bootDeviceIds"));
      this.nics = ImmutableList.copyOf(checkNotNull(nics, "nics"));
      this.vnc = checkNotNull(vnc, "vnc");
      this.description = description;
   }

   /**
    * 
    * @return CPU quota in core MHz.
    */
   public int getCpu() {
      return cpu;
   }

   /**
    * 
    * @return number of virtual processors or null if calculated based on cpu.
    */
   public Integer getSmp() {
      return smp;
   }

   /**
    * 
    * @return virtual memory size in MB.
    */
   public int getMem() {
      return mem;
   }

   /**
    * 
    * @return 'true' means that server will revert to a 'stopped' status on server stop or shutdown,
    *         rather than being destroyed automatically.
    */
   public boolean isPersistent() {
      return persistent;
   }

   /**
    * 
    * @return devices present, mapped by id
    */
   public Map<String, Device> getDevices() {
      return devices;
   }

   /**
    * 
    * @return ids of the devices to boot, e.g. ide:0:0 or ide:1:0
    * @see Device#getId()
    */
   public Set<String> getBootDeviceIds() {
      return bootDeviceIds;
   }

   public List<NIC> getNics() {
      return nics;
   }

   public VNC getVnc() {
      return vnc;
   }

   // TODO undocumented
   public String getDescription() {
      return description;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((bootDeviceIds == null) ? 0 : bootDeviceIds.hashCode());
      result = prime * result + cpu;
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((devices == null) ? 0 : devices.hashCode());
      result = prime * result + mem;
      result = prime * result + ((nics == null) ? 0 : nics.hashCode());
      result = prime * result + (persistent ? 1231 : 1237);
      result = prime * result + ((smp == null) ? 0 : smp.hashCode());
      result = prime * result + ((vnc == null) ? 0 : vnc.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      Server other = (Server) obj;
      if (bootDeviceIds == null) {
         if (other.bootDeviceIds != null)
            return false;
      } else if (!bootDeviceIds.equals(other.bootDeviceIds))
         return false;
      if (cpu != other.cpu)
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (devices == null) {
         if (other.devices != null)
            return false;
      } else if (!devices.equals(other.devices))
         return false;
      if (mem != other.mem)
         return false;
      if (nics == null) {
         if (other.nics != null)
            return false;
      } else if (!nics.equals(other.nics))
         return false;
      if (persistent != other.persistent)
         return false;
      if (smp == null) {
         if (other.smp != null)
            return false;
      } else if (!smp.equals(other.smp))
         return false;
      if (vnc == null) {
         if (other.vnc != null)
            return false;
      } else if (!vnc.equals(other.vnc))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[uuid=" + uuid + ", name=" + name + ", use=" + use + ", cpu=" + cpu + ", smp=" + smp + ", mem=" + mem
            + ", persistent=" + persistent + ", devices=" + devices + ", bootDeviceIds=" + bootDeviceIds + ", nics="
            + nics + ", vnc=" + vnc + ", description=" + description + "]";
   }

}