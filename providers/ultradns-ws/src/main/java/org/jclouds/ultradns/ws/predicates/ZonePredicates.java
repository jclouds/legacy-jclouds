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
package org.jclouds.ultradns.ws.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.domain.Zone.Type;

import com.google.common.base.Predicate;

/**
 * Predicates handy when working with Zone Types
 * 
 * @author Adrian Cole
 */
public class ZonePredicates {

   /**
    * matches zones of the given type
    */
   public static Predicate<Zone> typeEquals(final Type type) {
      checkNotNull(type, "type must be defined");

      return new Predicate<Zone>() {
         @Override
         public boolean apply(Zone zone) {
            return type.equals(zone.getType());
         }

         @Override
         public String toString() {
            return "typeEquals(" + type + ")";
         }
      };
   }
}
