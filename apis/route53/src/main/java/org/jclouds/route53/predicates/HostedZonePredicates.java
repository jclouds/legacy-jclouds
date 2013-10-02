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

import org.jclouds.route53.domain.HostedZone;

import com.google.common.base.Predicate;

/**
 * Predicates handy when working with HostedZones
 * 
 * @author Adrian Cole
 */
public class HostedZonePredicates {

   /**
    * matches zones of the given name
    */
   public static Predicate<HostedZone> nameEquals(final String name) {
      checkNotNull(name, "name must be defined");

      return new Predicate<HostedZone>() {
         @Override
         public boolean apply(HostedZone zone) {
            return name.equals(zone.getName());
         }

         @Override
         public String toString() {
            return "nameEquals(" + name + ")";
         }
      };
   }
}
