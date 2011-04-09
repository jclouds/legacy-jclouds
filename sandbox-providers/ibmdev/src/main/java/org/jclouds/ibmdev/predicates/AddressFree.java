/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ibmdev.predicates;

import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.ibmdev.IBMDeveloperCloudClient;
import org.jclouds.ibmdev.domain.Address;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AddressFree implements Predicate<Address> {

   private final IBMDeveloperCloudClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public AddressFree(IBMDeveloperCloudClient client) {
      this.client = client;
   }

   public boolean apply(Address address) {
      logger.trace("looking for state on address %s", address);
      final String id = address.getId();
      try {
         address = Iterables.find(client.listAddresses(), new Predicate<Address>() {

            @Override
            public boolean apply(Address input) {
               return input.getId().equals(id);
            }
         });
      } catch (NoSuchElementException e) {
         return false;
      }
      logger.trace("%s: looking for address state %s: currently: %s", address.getId(),
               Address.State.FREE, address.getState());
      return address.getState() == Address.State.FREE;

   }

}
