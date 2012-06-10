/*
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
package org.jclouds.openstack.nova.v2_0.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;

import com.google.common.base.Predicate;

/**
 * Predicates handy when working with SecurityGroups
 * 
 * @author Adrian Cole
 */

public class SecurityGroupPredicates {

   /**
    * matches name of the given security group
    * 
    * @param name
    * @return predicate that matches name
    */
   public static Predicate<SecurityGroup> nameEquals(final String name) {
      checkNotNull(name, "name must be defined");

      return new Predicate<SecurityGroup>() {
         @Override
         public boolean apply(SecurityGroup ext) {
            return name.equals(ext.getName());
         }

         @Override
         public String toString() {
            return "nameEquals(" + name + ")";
         }
      };
   }

   /**
    * matches name of the given security group
    * 
    * @param name
    * @return predicate that matches name
    */
   public static Predicate<SecurityGroup> nameMatches(final Predicate<String> name) {
      checkNotNull(name, "name must be defined");

      return new Predicate<SecurityGroup>() {
         @Override
         public boolean apply(SecurityGroup ext) {
            return name.apply(ext.getName());
         }

         @Override
         public String toString() {
            return "nameMatches(" + name + ")";
         }
      };
   }
}
