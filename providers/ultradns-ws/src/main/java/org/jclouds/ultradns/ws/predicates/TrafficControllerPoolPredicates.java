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
package org.jclouds.ultradns.ws.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.ultradns.ws.domain.TrafficControllerPool;
import org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecordDetail;

import com.google.common.base.Predicate;

/**
 * Predicates handy when working with TrafficControllerPool Types
 * 
 * @author Adrian Cole
 */
public class TrafficControllerPoolPredicates {

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
         return input != null && id.equals(input.getId());
      }

      @Override
      public String toString() {
         return "IdEqualTo(" + id + ")";
      }
   }

   public static Predicate<TrafficControllerPoolRecordDetail> recordIdEqualTo(String recordId) {
      return new RecordIdEqualToPredicate(recordId);
   }

   private static final class RecordIdEqualToPredicate implements Predicate<TrafficControllerPoolRecordDetail> {
      private final String recordId;

      public RecordIdEqualToPredicate(String recordId) {
         this.recordId = checkNotNull(recordId, "recordId");
      }

      @Override
      public boolean apply(TrafficControllerPoolRecordDetail input) {
         return input != null && recordId.equals(input.getId());
      }

      @Override
      public String toString() {
         return "RecordIdEqualTo(" + recordId + ")";
      }
   }
}
