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
package org.jclouds.route53.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.route53.domain.ResourceRecordSet;

import com.google.common.base.Predicate;

/**
 * Predicates handy when working with ResourceResourceRecordSet Types
 * 
 * @author Adrian Cole
 */
public class ResourceRecordSetPredicates {

   /**
    * matches zones of the given type
    */
   public static Predicate<ResourceRecordSet> typeEquals(final String type) {
      checkNotNull(type, "type must be defined");

      return new Predicate<ResourceRecordSet>() {
         @Override
         public boolean apply(ResourceRecordSet zone) {
            return type.equals(zone.getType());
         }

         @Override
         public String toString() {
            return "typeEquals(" + type + ")";
         }
      };
   }
}
