/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.predicates.cloud;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import org.jclouds.abiquo.domain.cloud.Conversion;

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.google.common.base.Predicate;

/**
 * Container for {@link VirtualMachineTemplate} filters.
 * 
 * @author Francesc Montserrat
 */
public class ConversionPredicates {

   public static Predicate<Conversion> sourceFormat(final DiskFormatType... formats) {
      checkNotNull(formats, "formats must be defined");

      return new Predicate<Conversion>() {
         @Override
         public boolean apply(final Conversion conversion) {
            return Arrays.asList(formats).contains(conversion.getSourceFormat());
         }
      };
   }

   public static Predicate<Conversion> targetFormat(final DiskFormatType... formats) {
      checkNotNull(formats, "formats must be defined");

      return new Predicate<Conversion>() {
         @Override
         public boolean apply(final Conversion conversion) {
            return Arrays.asList(formats).contains(conversion.getTargetFormat());
         }
      };
   }

   public static Predicate<Conversion> state(final ConversionState... states) {
      checkNotNull(states, "states must be defined");

      return new Predicate<Conversion>() {
         @Override
         public boolean apply(final Conversion conversion) {
            return Arrays.asList(states).contains(conversion.getState());
         }
      };
   }

   public static Predicate<Conversion> compatible(final HypervisorType type) {
      checkNotNull(type, "type must be defined");

      return new Predicate<Conversion>() {
         @Override
         public boolean apply(final Conversion conversion) {
            return type.isCompatible(conversion.getTargetFormat());
         }
      };
   }

}
