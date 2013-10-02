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
package org.jclouds.dynect.v3.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.dynect.v3.domain.RecordId;

import com.google.common.base.Predicate;

/**
 * Predicates handy when working with Records
 * 
 * @author Adrian Cole
 */
public class RecordPredicates {

   /**
    * matches type of the given record
    * 
    * @param type
    * @return predicate that matches type
    */
   public static <R extends RecordId> Predicate<R> typeEquals(final String type) {
      checkNotNull(type, "type must be defined");

      return new Predicate<R>() {
         @Override
         public boolean apply(R record) {
            return type.equals(record.getType());
         }

         @Override
         public String toString() {
            return "typeEquals(" + type + ")";
         }
      };
   }
}
