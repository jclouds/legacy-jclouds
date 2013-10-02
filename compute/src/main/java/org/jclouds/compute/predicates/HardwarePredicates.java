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
package org.jclouds.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.domain.Hardware;

import com.google.common.base.Predicate;

/**
 * Container for hardware filters (predicates).
 * 
 * This class has static methods that create customized predicates to use with
 * {@link org.jclouds.compute.ComputeService}.
 * 
 * @author Adrian Cole
 */
public class HardwarePredicates {

   public static Predicate<Hardware> idEquals(final String id) {
      checkNotNull(id, "id must be defined");
      return new Predicate<Hardware>() {
         @Override
         public boolean apply(Hardware hardware) {
            return id.equals(hardware.getId());
         }

         @Override
         public String toString() {
            return "idEquals(" + id + ")";
         }
      };
   }
}
