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
package org.jclouds.cloudstack.util;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.Constants;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.ApiKeyPair;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.rest.RestContextFactory;

import java.util.Properties;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Andrei Savu
 */
public class ApiKeyPairs {

   private final static String PROVIDER = "cloudstack";

   /**
    * Retrieve the API key pair for a given CloudStack user
    *
    * @param endpoint
    *          CloudStack API endpoint (e.g. http://72.52.126.25/client/api/)
    * @param username
    *          User account name
    * @param password
    *          User password
    * @param domain
    *          Domain name. If empty defaults to ROOT
    * @return
    */
   public static ApiKeyPair getApiKeyPairForUser(String endpoint, String username, String password, String domain) {
      ComputeServiceContext context = null;
      try {
         context = new ComputeServiceContextFactory(setupRestProperties()).
            createContext(PROVIDER, ImmutableSet.<Module>of(), setupProperties(endpoint, username, password, domain));

         CloudStackClient client = CloudStackClient.class.cast(context.getProviderSpecificContext().getApi());
         Set<Account> listOfAccounts = client.getAccountClient().listAccounts();

         domain = (domain.equals("") || domain.equals("/")) ? "ROOT" : domain;
         for (Account account : listOfAccounts) {
            for (User user : account.getUsers()) {
               if (user.getName().equals(username) && user.getDomain().equals(domain)) {
                  return ApiKeyPair.builder().apiKey(user.getApiKey())
                     .secretKey(user.getSecretKey()).build();
               }
            }
         }
         return null;

      } finally {
         if (context != null)
            context.close();
      }
   }

   private static Properties setupRestProperties() {
      return RestContextFactory.getPropertiesFromResource("/rest.properties");
   }

   private static Properties setupProperties(String endpoint, String username, String password, String domain) {
      Properties overrides = new Properties();

      overrides.put(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.put(Constants.PROPERTY_RELAX_HOSTNAME, "true");

      overrides.put("jclouds.cloudstack.credential-type", "passwordCredentials");

      overrides.put(PROVIDER + ".endpoint", checkNotNull(endpoint, "endpoint"));
      overrides.put(PROVIDER + ".identity",
         String.format("%s/%s", checkNotNull(domain, "domain"), checkNotNull(username, "username")));
      overrides.put(PROVIDER + ".credential", checkNotNull(password, "password"));

      return overrides;
   }
}
