/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.chef.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.chef.ChefClient;
import org.jclouds.chef.domain.Client;

import com.google.common.base.Function;

/**
 * 
 * Generates a client relevant for a particular tag
 * 
 * @author Adrian Cole
 */
@Singleton
public class ClientForTag implements Function<String, Client> {
   private final ChefClient chefClient;

   @Inject
   public ClientForTag(ChefClient chefClient) {
      this.chefClient = checkNotNull(chefClient, "chefClient");
   }

   @Override
   public Client apply(String from) {
      String clientName = findNextClientName(chefClient.listClients(), from + "-validator-%02d");
      Client client = chefClient.createClient(clientName);
      // response from create only includes the key
      return new Client(null, null, clientName, clientName, false, client.getPrivateKey());
   }

   private static String findNextClientName(Set<String> clients, String pattern) {
      String clientName;
      Set<String> names = newHashSet(clients);
      int index = 0;
      while (true) {
         clientName = String.format(pattern,index++);
         if (!names.contains(clientName))
            break;
      }
      return clientName;
   }
}