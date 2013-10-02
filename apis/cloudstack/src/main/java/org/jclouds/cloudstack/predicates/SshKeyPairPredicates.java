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
package org.jclouds.cloudstack.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.cloudstack.domain.SshKeyPair;

import com.google.common.base.Predicate;

/**
 * Predicates handy when working with SshKeyPairs
 * 
 * @author Adam Lowe
 * @author Andrew Bayer
 */

public class SshKeyPairPredicates {


   /**
    * matches name of the given key pair
    * 
    * @param name
    * @return predicate that matches name
    */
   public static Predicate<SshKeyPair> nameMatches(final Predicate<String> name) {
      checkNotNull(name, "name must be defined");

      return new Predicate<SshKeyPair>() {
         @Override
         public boolean apply(SshKeyPair ext) {
            return name.apply(ext.getName());
         }

         @Override
         public String toString() {
            return "nameMatches(" + name + ")";
         }
      };
   }
   
   /**
    * matches name of the given keypair starts with the specified prefix
    *
    * @param name the prefix you are looking for
    * @return the predicate
    */
   public static Predicate<SshKeyPair> nameEquals(final String name) {
      checkNotNull(name, "name must be defined");

      return new Predicate<SshKeyPair>() {
         @Override
         public boolean apply(SshKeyPair ext) {
            return ext.getName() != null && ext.getName().equals(name);
         }

         @Override
         public String toString() {
            return "nameEquals(" + name + ")";
         }
      };
   }
}
