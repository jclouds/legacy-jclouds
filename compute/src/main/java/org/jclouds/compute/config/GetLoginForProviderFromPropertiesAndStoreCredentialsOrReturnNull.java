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
package org.jclouds.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.domain.Credentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Provider;

import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public class GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull implements
      javax.inject.Provider<Credentials> {
   private final ValueOfConfigurationKeyOrNull config;
   private final String provider;
   private final Map<String, Credentials> credentialStore;

   @Inject
   public GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull(@Provider String provider,
         ValueOfConfigurationKeyOrNull config, Map<String, Credentials> credentialStore) {
      this.config = checkNotNull(config, "config");
      this.provider = checkNotNull(provider, "provider");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
   }

   @Override
   @Nullable
   public Credentials get() {
      if (credentialStore.containsKey("image"))
         return credentialStore.get("image");
      Credentials creds = null;
      String loginUser = config.apply(provider + ".login-user");
      if (loginUser == null)
         loginUser = config.apply("jclouds.login-user");
      if (loginUser != null) {
         int pos = loginUser.indexOf(':');
         if (pos != -1) {
            creds = new Credentials(loginUser.substring(0, pos), loginUser.substring(pos + 1));
         } else
            creds = new Credentials(loginUser, null);
         credentialStore.put("image", creds);
      }
      return creds;
   }

}