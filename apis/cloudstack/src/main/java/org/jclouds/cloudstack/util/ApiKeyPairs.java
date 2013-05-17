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
package org.jclouds.cloudstack.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.cloudstack.CloudStackApiMetadata;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.ApiKeyPair;
import org.jclouds.cloudstack.domain.User;

/**
 * @author Andrei Savu
 */
public class ApiKeyPairs {

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
    * @throws NoSuchElementException, AuthorizationException
    * @return
    */
   public static ApiKeyPair loginToEndpointAsUsernameInDomainWithPasswordAndReturnApiKeyPair(
         URI endpoint, String username, String password, String domain) {
      CloudStackContext context = null;
      try {
         Properties overrides = new Properties();
         overrides.put(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
         overrides.put(Constants.PROPERTY_RELAX_HOSTNAME, "true");
         overrides.put("jclouds.cloudstack.credential-type", "passwordCredentials");
         
         context =  ContextBuilder.newBuilder(new CloudStackApiMetadata())
               .endpoint(checkNotNull(endpoint, "endpoint").toASCIIString())
               .credentials(String.format("%s/%s", checkNotNull(domain, "domain"), checkNotNull(username, "username")), password)
               .overrides(overrides).build(CloudStackContext.class);

         CloudStackClient client = context.unwrap(CloudStackApiMetadata.CONTEXT_TOKEN).getApi();
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
         throw new NoSuchElementException("Unable to find API keypair for user " + username);

      } finally {
         if (context != null)
            context.close();
      }
   }

}
