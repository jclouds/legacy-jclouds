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
package org.jclouds.compute.strategy.impl;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;

/**
 * @author Adrian Cole
 */
@Singleton
public class ReturnCredentialsBoundToImage implements PopulateDefaultLoginCredentialsForImageStrategy {

   protected final LoginCredentials creds;
   protected final Map<String, Credentials> credentialStore;
   protected final Map<OsFamily, LoginCredentials> osFamilyToCredentials;

   @Inject
   public ReturnCredentialsBoundToImage(@Nullable @Named("image") LoginCredentials creds,
            Map<String, Credentials> credentialStore, Map<OsFamily, LoginCredentials> osFamilyToCredentials) {
      this.creds = creds;
      this.credentialStore = credentialStore;
      this.osFamilyToCredentials = osFamilyToCredentials;
   }

   @Override
   public LoginCredentials apply(Object resourceToAuthenticate) {
      checkState(resourceToAuthenticate instanceof Image, "this is only valid for images, not %s",
               resourceToAuthenticate.getClass().getSimpleName());
      if (creds != null)
         return creds;
      Image image = Image.class.cast(resourceToAuthenticate);
      if (credentialStore.containsKey("image#" + image.getId()))
         return LoginCredentials.fromCredentials(credentialStore.get("image#" + image.getId()));
      if (image.getOperatingSystem() != null && image.getOperatingSystem().getFamily() != null
               && osFamilyToCredentials.containsKey(image.getOperatingSystem().getFamily())) {
         return osFamilyToCredentials.get(image.getOperatingSystem().getFamily());
      } else {
         return LoginCredentials.builder().user("root").build();
      }
   }

   @Override
   public Credentials execute(Object resourceToAuthenticate) {
      return apply(resourceToAuthenticate);
   }
}
