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

import org.jclouds.ultradns.ws.domain.TrafficControllerPool;
import org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecord;

import com.google.common.base.Predicate;

/**
 * Predicates handy when working with TrafficControllerPool Types
 * 
 * @author Adrian Cole
 */
public class TrafficControllerPoolPredicates {

   /**
    * evaluates to true if the input {@link TrafficControllerPool} exists
    * with {@link TrafficControllerPool#getId() id} corresponding to the
    * {@code id} parameter.
    * 
    * @param id
    *           the {@link TrafficControllerPool#getId() id} of the
    *           desired pool record
    */
   public static Predicate<TrafficControllerPool> idEqualTo(String id) {
      return new IdEqualToPredicate(id);
   }

   private static final class IdEqualToPredicate implements Predicate<TrafficControllerPool> {
      private final String id;

      public IdEqualToPredicate(String id) {
         this.id = checkNotNull(id, "id");
      }

      @Override
      public boolean apply(TrafficControllerPool input) {
         if (input == null)
            return false;
         return id.equals(input.getId());
      }

      @Override
      public String toString() {
         return "IdEqualTo(" + id + ")";
      }
   }

   /**
    * evaluates to true if the input {@link TrafficControllerPoolRecord} exists
    * with {@link TrafficControllerPoolRecord#getId() id} corresponding to the
    * {@code recordId} parameter.
    * 
    * @param recordId
    *           the {@link TrafficControllerPoolRecord#getId() id} of the
    *           desired pool record
    */
   public static Predicate<TrafficControllerPoolRecord> recordIdEqualTo(String recordId) {
      return new RecordIdEqualToPredicate(recordId);
   }

   private static final class RecordIdEqualToPredicate implements Predicate<TrafficControllerPoolRecord> {
      private final String id;

      public RecordIdEqualToPredicate(String id) {
         this.id = checkNotNull(id, "recordId");
      }

      @Override
      public boolean apply(TrafficControllerPoolRecord input) {
         if (input == null)
            return false;
         return id.equals(input.getId());
      }

      @Override
      public String toString() {
         return "RecordIdEqualTo(" + id + ")";
      }
   }
}
