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

import static com.google.common.base.Predicates.alwaysTrue;

import org.jclouds.cloudstack.domain.GuestIPType;
import org.jclouds.cloudstack.domain.NetworkOffering;
import org.jclouds.cloudstack.domain.TrafficType;

import com.google.common.base.Predicate;

/**
 * 
 * @author Adrian Cole
 */
public class NetworkOfferingPredicates {

   /**
    * 
    * @return true, if the offering supports creation of GuestVirtual Networks
    */
   public static Predicate<NetworkOffering> supportsGuestVirtualNetworks() {
      return new Predicate<NetworkOffering>() {

         @Override
         public boolean apply(NetworkOffering arg0) {
            return arg0.getTrafficType() == TrafficType.GUEST && arg0.getGuestIPType() == GuestIPType.VIRTUAL;
         }

         @Override
         public String toString() {
            return "supportsGuestVirtualNetworks()";
         }
      };
   }

   /**
    * 
    * @return always returns true.
    */
   public static Predicate<NetworkOffering> any() {
      return alwaysTrue();
   }
}
