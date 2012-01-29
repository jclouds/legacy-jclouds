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
package org.jclouds.glesys.domain;

import com.google.common.base.Objects;

/**
 * Represents an 'uptime' duration of server in a Glesys cloud
 *
 * @author Adam Lowe
 * @see ServerStatus
 */
public class ServerUptime {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long current;
      private String unit;
      
      public Builder current(long current) {
         this.current = current;
         return this;
      }

      public Builder unit(String unit) {
         this.unit = unit;
         return this;
      }
      
      public ServerUptime build() {
         return new ServerUptime(current, unit);
      }
      
      public Builder fromServerUptime(ServerUptime from) {
         return current(from.getCurrent()).unit(from.getUnit());
      }
   }
   
   private final long current;
   private final String unit;

   public ServerUptime(long current, String unit) {
      this.current = current;
      this.unit = unit;
   }
   
   /**
    * @return the time the server has been up in #unit
    */
   public long getCurrent() {
      return current;
   }

   /**
    * @return the  unit used for #time
    */
   public String getUnit() {
      return unit;
   }


   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      return object instanceof ServerUptime
            && Objects.equal(current, ((ServerUptime) object).getCurrent())
            && Objects.equal(unit, ((ServerUptime) object).getUnit());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(current, unit);
   }

   @Override
   public String toString() {
      return String.format("[current=%d unit=%s]", current, unit);
   }

}