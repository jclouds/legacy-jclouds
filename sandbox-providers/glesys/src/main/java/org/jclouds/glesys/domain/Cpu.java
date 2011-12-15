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
import org.jclouds.javax.annotation.Nullable;

/**
 * Detailed information on Server cpu usage
 *
 * @author Adam Lowe
 * @see ServerStatus
 */

public class Cpu {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private double system;
      private double user;
      private Double nice;
      private double idle;
      private String unit;

      public Builder system(double system) {
         this.system = system;
         return this;
      }

      public Builder user(double user) {
         this.user = user;
         return this;
      }

      public Builder nice(Double nice) {
         this.nice = nice;
         return this;
      }

      public Builder idle(double idle) {
         this.idle = idle;
         return this;
      }

      public Builder unit(String unit) {
         this.unit = unit;
         return this;
      }

      public Cpu build() {
         return new Cpu(system, user, nice, idle, unit);
      }

      public Builder fromCpu(Cpu in) {
         return system(in.getSystem()).user(in.getUser()).nice(in.getNice()).idle(in.getIdle()).unit(in.getUnit());
      }
   }

   private final double system;
   private final double user;
   private final Double nice;
   private final double idle;
   private final String unit;

   public Cpu(double system, double user, @Nullable Double nice, double idle, String unit) {
      this.system = system;
      this.user = user;
      this.nice = nice;
      this.idle = idle;
      this.unit = unit;
   }

   /**
    * @return the system time in use in #unit
    */
   public double getSystem() {
      return system;
   }

   /**
    * @return the user time in use in #unit
    */
   public double getUser() {
      return user;
   }

   /**
    * @return the nice setting
    */
   public Double getNice() {
      return nice;
   }

   /**
    * @return the idle time in #unit
    */
   public double getIdle() {
      return idle;
   }

   /**
    * @return the unit used
    */
   public String getUnit() {
      return unit;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Cpu) {
         Cpu other = (Cpu) object;
         return Objects.equal(system, other.system)
               && Objects.equal(user, other.user)
               && Objects.equal(nice, other.nice)
               && Objects.equal(idle, other.idle)
               && Objects.equal(unit, other.unit);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(system, user, nice, idle, unit);
   }
   
   @Override
   public String toString() {
      return String.format("[system=%f, user=%f, nice=%f, idle=%f, unit=%s]",
            system, user, nice, idle, unit);
   }
}
