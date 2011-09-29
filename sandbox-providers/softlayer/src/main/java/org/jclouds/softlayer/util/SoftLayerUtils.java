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
package org.jclouds.softlayer.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jclouds.softlayer.domain.VirtualGuest;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utils for the SoftLayer project
 * @author Jason King
 */
public class SoftLayerUtils {

   /**
    * Finds the virtual guest with given hostname and domainName
    * @param allGuests the VirtualGuests to search
    * @param hostname the virtual guest's hostname
    * @param domainName the virtual guest's domain name
    * @return the matching virtual guest or null if not found
    * @throws IllegalStateException if more than one match is found
    */
   public static VirtualGuest findVirtualGuest(Iterable<VirtualGuest> allGuests,
                                               final String hostname, final String domainName) {

      checkNotNull(allGuests,"allGuests");
      checkNotNull(hostname,"hostname");
      checkNotNull(domainName,"domainName");

      Iterable<VirtualGuest> guests = Iterables.filter(allGuests, new Predicate<VirtualGuest>() {

         @Override
         public boolean apply(VirtualGuest arg0) {
            return hostname.equals(arg0.getHostname()) && domainName.equals(arg0.getDomain());
         }

      });
      switch (Iterables.size(guests)) {
         case 0:
            return null;
         case 1:
            return Iterables.get(guests, 0);
         default:
            throw new IllegalStateException(String.format(
                     "expected only 1 virtual guest with hostname %s and domainname %s: %s", hostname, domainName,
                     guests));
      }
   }
}
