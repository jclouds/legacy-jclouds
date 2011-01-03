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

import java.util.Date;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * 
 * @author Adrian Cole
 */
public class ServerInfo extends Server {

   public static class Builder extends Server.Builder {
      protected ServerStatus status;
      protected Date started;
      protected String user;
      protected ServerMetrics metrics;

      public Builder status(ServerStatus status) {
         this.status = status;
         return this;
      }

      public Builder started(Date started) {
         this.started = started;
         return this;
      }

      public Builder user(String user) {
         this.user = user;
         return this;
      }

      public Builder metrics(ServerMetrics metrics) {
         this.metrics = metrics;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder cpu(int cpu) {
         return Builder.class.cast(super.cpu(cpu));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder smp(Integer smp) {
         return Builder.class.cast(super.smp(smp));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder mem(int mem) {
         return Builder.class.cast(super.mem(mem));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder persistent(boolean persistent) {
         return Builder.class.cast(super.persistent(persistent));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder devices(Map<String, ? extends Device> devices) {
         return Builder.class.cast(super.devices(devices));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder bootDeviceIds(Iterable<String> bootDeviceIds) {
         return Builder.class.cast(super.bootDeviceIds(bootDeviceIds));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder nics(Iterable<NIC> nics) {
         return Builder.class.cast(super.nics(nics));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder vnc(VNC vnc) {
         return Builder.class.cast(super.vnc(vnc));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder description(String description) {
         return Builder.class.cast(super.description(description));
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

      public ServerInfo build() {
         return new ServerInfo(uuid, name, cpu, smp, mem, persistent, devices, bootDeviceIds, use, nics, vnc,
               description, status, started, user, metrics);
      }
   }

   protected final ServerStatus status;
   @Nullable
   protected final Date started;
   @Nullable
   protected final String user;
   protected final ServerMetrics metrics;

   public ServerInfo(String uuid, String name, int cpu, Integer smp, int mem, boolean persistent,
         Map<String, ? extends Device> devices, Iterable<String> bootDeviceIds, Iterable<String> use,
         Iterable<NIC> nics, VNC vnc, String description, ServerStatus status, Date started, String user,
         @Nullable ServerMetrics metrics) {
      super(uuid, name, cpu, smp, mem, persistent, devices, bootDeviceIds, use, nics, vnc, description);
      this.status = status;
      this.started = started;
      this.user = user;
      this.metrics = metrics;
   }

   /**
    * 
    * @return active | stopped | paused | dumped | dead
    */
   public ServerStatus getStatus() {
      return status;
   }

   // TODO undocumented
   public Date getStarted() {
      return started;
   }

   /**
    * 
    * @return metrics, if the server is running, or null
    */
   @Nullable
   public ServerMetrics getMetrics() {
      return metrics;
   }

   // TODO undocumented
   /**
    * 
    * @return owner of the server.
    */
   public String getUser() {
      return user;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((metrics == null) ? 0 : metrics.hashCode());
      result = prime * result + ((started == null) ? 0 : started.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((user == null) ? 0 : user.hashCode());
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
      if (metrics == null) {
         if (other.metrics != null)
            return false;
      } else if (!metrics.equals(other.metrics))
         return false;
      if (started == null) {
         if (other.started != null)
            return false;
      } else if (!started.equals(other.started))
         return false;
      if (status != other.status)
         return false;
      if (user == null) {
         if (other.user != null)
            return false;
      } else if (!user.equals(other.user))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[cpu=" + cpu + ", smp=" + smp + ", mem=" + mem + ", persistent=" + persistent + ", devices=" + devices
            + ", bootDeviceIds=" + bootDeviceIds + ", nics=" + nics + ", vnc=" + vnc + ", uuid=" + uuid + ", name="
            + name + ", use=" + use + ", status=" + status + ", started=" + started + ", user=" + user + ", metrics="
            + metrics + "]";
   }

}