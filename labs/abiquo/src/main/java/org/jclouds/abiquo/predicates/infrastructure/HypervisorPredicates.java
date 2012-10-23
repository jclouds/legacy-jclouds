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

package org.jclouds.abiquo.predicates.infrastructure;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import com.abiquo.model.enumerator.HypervisorType;
import com.google.common.base.Predicate;

/**
 * Container for {@link HypervisorType} filters.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
public class HypervisorPredicates {
   public static Predicate<HypervisorType> type(final HypervisorType... types) {
      checkNotNull(types, "types must be defined");

      return new Predicate<HypervisorType>() {
         @Override
         public boolean apply(final HypervisorType type) {
            return Arrays.asList(types).contains(type);
         }
      };
   }
}
