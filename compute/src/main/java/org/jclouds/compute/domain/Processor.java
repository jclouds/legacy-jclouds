/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.compute.domain;

import static com.google.common.base.Objects.equal;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
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
      Processor thatProcessor = Processor.class.cast(that);
      return ComparisonChain.start().compare(this.getCores(), thatProcessor.getCores())
            .compare(this.getSpeed(), thatProcessor.getSpeed()).result();
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
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Processor that = Processor.class.cast(o);
      return equal(this.cores, that.cores) && equal(this.speed, that.speed);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(cores, speed);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").omitNullValues().add("cores", cores).add("speed", speed);
   }

}
