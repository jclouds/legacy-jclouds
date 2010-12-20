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

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
public class ServerMetrics {

   public static class Builder {
      protected long txPackets;
      protected long tx;
      protected long rxPackets;
      protected long rx;
      protected Map<String, DriveMetrics> driveMetrics = ImmutableMap.<String, DriveMetrics> of();

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

      public Builder driveMetrics(Map<String, ? extends DriveMetrics> driveMetrics) {
         this.driveMetrics = ImmutableMap.copyOf(checkNotNull(driveMetrics, "driveMetrics"));
         return this;
      }

      public ServerMetrics build() {
         return new ServerMetrics(tx, txPackets, rx, rxPackets, driveMetrics);
      }
   }

   protected final long txPackets;
   protected final long tx;
   protected final long rxPackets;
   protected final long rx;
   protected final Map<String, DriveMetrics> driveMetrics;

   public ServerMetrics(long tx, long txPackets, long rx, long rxPackets, Map<String, DriveMetrics> driveMetrics) {
      this.txPackets = txPackets;
      this.tx = tx;
      this.rxPackets = rxPackets;
      this.rx = rx;
      this.driveMetrics = ImmutableMap.copyOf(checkNotNull(driveMetrics, "driveMetrics"));
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

   /**
    * 
    * @return metrics keyed on device id ex. {@code ide:0:0}
    */
   public Map<String, DriveMetrics> getDriveMetrics() {
      return driveMetrics;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((driveMetrics == null) ? 0 : driveMetrics.hashCode());
      result = prime * result + (int) (rx ^ (rx >>> 32));
      result = prime * result + (int) (rxPackets ^ (rxPackets >>> 32));
      result = prime * result + (int) (tx ^ (tx >>> 32));
      result = prime * result + (int) (txPackets ^ (txPackets >>> 32));
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ServerMetrics other = (ServerMetrics) obj;
      if (driveMetrics == null) {
         if (other.driveMetrics != null)
            return false;
      } else if (!driveMetrics.equals(other.driveMetrics))
         return false;
      if (rx != other.rx)
         return false;
      if (rxPackets != other.rxPackets)
         return false;
      if (tx != other.tx)
         return false;
      if (txPackets != other.txPackets)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[ txPackets=" + txPackets + ", tx=" + tx + ", rxPackets=" + rxPackets + ", rx=" + rx + ", driveMetrics="
            + driveMetrics + "]";
   }

}