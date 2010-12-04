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

package org.jclouds.elasticstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class ServerInfo extends Item {

   public static class Builder extends Item.Builder {
      protected int cpu;
      protected Integer smp;
      protected ServerStatus status;
      protected int mem;
      protected boolean persistent;
      protected Date started;
      protected Set<? extends Device> devices = ImmutableSet.of();
      protected Set<String> bootDeviceIds = ImmutableSet.of();
      protected List<NIC> nics = ImmutableList.of();
      protected String user;
      protected VNC vnc;
      // TODO undocumented
      protected String description;
      protected long txPackets;
      protected long tx;
      protected long rxPackets;
      protected long rx;

      public Builder status(ServerStatus status) {
         this.status = status;
         return this;
      }

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

      public Builder started(Date started) {
         this.started = started;
         return this;
      }

      public Builder devices(Iterable<? extends Device> devices) {
         this.devices = ImmutableSet.copyOf(checkNotNull(devices, "devices"));
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

      public Builder user(String user) {
         this.user = user;
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

      public Builder txPackets(long txPackets) {
         this.txPackets = txPackets;
         return this;
      }

      public Builder tx(long tx) {
         this.tx = tx;
         return this;
      }

      public Builder rxPackets(long rxPackets) {
         this.rxPackets = rxPackets;
         return this;
      }

      public Builder rx(long rx) {
         this.rx = rx;
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
      public Builder tags(Iterable<String> tags) {
         return Builder.class.cast(super.tags(tags));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder userMetadata(Map<String, String> userMetadata) {
         return Builder.class.cast(super.userMetadata(userMetadata));
      }

      public ServerInfo build() {
         return new ServerInfo(uuid, name, cpu, smp, mem, status, persistent, started, devices, tags, bootDeviceIds,
               userMetadata, nics, user, vnc, description, tx, txPackets, rx, rxPackets);
      }
   }

   protected final int cpu;
   protected final Integer smp;
   protected final int mem;
   protected final ServerStatus status;
   protected final boolean persistent;
   @Nullable
   protected final Date started;
   protected final Set<? extends Device> devices;
   protected final Set<String> bootDeviceIds;
   @Nullable
   protected final String user;
   protected final List<NIC> nics;
   protected final VNC vnc;
   @Nullable
   private final String description;
   protected final long txPackets;
   protected final long tx;
   protected final long rxPackets;
   protected final long rx;

   public ServerInfo(@Nullable String uuid, String name, int cpu, @Nullable Integer smp, int mem, ServerStatus status,
         boolean persistent, @Nullable Date started, Iterable<? extends Device> devices,
         Iterable<String> bootDeviceIds, Iterable<String> tags, Map<String, String> userMetadata, Iterable<NIC> nics,
         @Nullable String user, VNC vnc, String description, long tx, long txPackets, long rx, long rxPackets) {
      super(uuid, name, tags, userMetadata);
      this.cpu = cpu;
      this.smp = smp;
      this.mem = mem;
      this.status = status;
      this.persistent = persistent;
      this.started = started;
      this.devices = ImmutableSet.copyOf(checkNotNull(devices, "devices"));
      this.bootDeviceIds = ImmutableSet.copyOf(checkNotNull(bootDeviceIds, "bootDeviceIds"));
      this.nics = ImmutableList.copyOf(checkNotNull(nics, "nics"));
      this.user = user;
      this.vnc = checkNotNull(vnc, "vnc");
      this.description = description;
      this.txPackets = txPackets;
      this.tx = tx;
      this.rxPackets = rxPackets;
      this.rx = rx;
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
    * @return active | stopped | paused | dumped | dead
    */
   public ServerStatus getStatus() {
      return status;
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
    * @return set of devices present
    */
   public Set<? extends Device> getDevices() {
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

   // TODO undocumented
   public Date getStarted() {
      return started;
   }

   // TODO undocumented
   public long getTxPackets() {
      return txPackets;
   }

   // TODO undocumented
   public long getTx() {
      return tx;
   }

   // TODO undocumented
   public long getRxPackets() {
      return rxPackets;
   }

   // TODO undocumented
   public long getRx() {
      return rx;
   }

   // TODO undocumented
   /**
    * 
    * @return owner of the server.
    */
   public String getUser() {
      return user;
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
      result = prime * result + (int) (rx ^ (rx >>> 32));
      result = prime * result + (int) (rxPackets ^ (rxPackets >>> 32));
      result = prime * result + ((smp == null) ? 0 : smp.hashCode());
      result = prime * result + ((started == null) ? 0 : started.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + (int) (tx ^ (tx >>> 32));
      result = prime * result + (int) (txPackets ^ (txPackets >>> 32));
      result = prime * result + ((user == null) ? 0 : user.hashCode());
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
      ServerInfo other = (ServerInfo) obj;
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
      if (rx != other.rx)
         return false;
      if (rxPackets != other.rxPackets)
         return false;
      if (smp == null) {
         if (other.smp != null)
            return false;
      } else if (!smp.equals(other.smp))
         return false;
      if (started == null) {
         if (other.started != null)
            return false;
      } else if (!started.equals(other.started))
         return false;
      if (status != other.status)
         return false;
      if (tx != other.tx)
         return false;
      if (txPackets != other.txPackets)
         return false;
      if (user == null) {
         if (other.user != null)
            return false;
      } else if (!user.equals(other.user))
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
      return "[uuid=" + uuid + ", name=" + name + ", tags=" + tags + ", userMetadata=" + userMetadata
            + ", cpu=" + cpu + ", smp=" + smp + ", mem=" + mem + ", status=" + status + ", persistent=" + persistent
            + ", started=" + started + ", devices=" + devices + ", bootDeviceIds=" + bootDeviceIds + ", user=" + user
            + ", nics=" + nics + ", vnc=" + vnc + ", description=" + description + ", txPackets=" + txPackets + ", tx="
            + tx + ", rxPackets=" + rxPackets + ", rx=" + rx + "]";
   }

}