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
package org.jclouds.compute.domain;

import com.google.common.collect.ComparisonChain;

/**
 * Processor (or CPU) as a part of {@link Hardware} of a {@link NodeMetadata}
 * 
 * @author Adrian Cole
 */
public class Processor implements Comparable<Processor> {
   private final double cores;
   private final double speed;

   public Processor(double cores, double speed) {
      this.cores = cores;
      this.speed = speed;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int compareTo(Processor that) {
      if (that instanceof Processor) {
         Processor thatProcessor = Processor.class.cast(that);
         return ComparisonChain.start().compare(this.getCores(), thatProcessor.getCores())
               .compare(this.getSpeed(), thatProcessor.getSpeed()).result();
      } else {
         return -1;
      }
   }

   /**
    * Amount of virtual or physical cores provided
    */
   public double getCores() {
      return cores;
   }

   /**
    * Speed, not necessarily in ghz, but certainly relevant to other processors
    * in the same provider.
    */
   public double getSpeed() {
      return speed;
   }

   @Override
   public String toString() {
      return "[cores=" + cores + ", speed=" + speed + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(cores);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(speed);
      result = prime * result + (int) (temp ^ (temp >>> 32));
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
      Processor other = (Processor) obj;
      if (Double.doubleToLongBits(cores) != Double.doubleToLongBits(other.cores))
         return false;
      if (Double.doubleToLongBits(speed) != Double.doubleToLongBits(other.speed))
         return false;
      return true;
   }

}