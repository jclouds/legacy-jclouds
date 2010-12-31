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

package org.jclouds.util;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import org.jclouds.crypto.Pems;
import org.jclouds.domain.Credentials;



/**
 * 
 * 
 * @author Adrian Cole
 */
public class CredentialUtils {

   public static Credentials overrideCredentialsIfSupplied(Credentials defaultCredentials,
         @Nullable Credentials overridingCredentials) {
      if (overridingCredentials == null)
         return defaultCredentials;
      String identity = overridingCredentials.identity != null ? overridingCredentials.identity : checkNotNull(
            defaultCredentials, "defaultCredentials").identity;
      String credential = overridingCredentials.credential != null ? overridingCredentials.credential : checkNotNull(
            defaultCredentials, "defaultCredentials").credential;
   
      return new Credentials(identity, credential);
   }

   public static boolean isPrivateKeyCredential(Credentials credentials) {
      return credentials != null
            && credentials.credential != null
            && (credentials.credential.startsWith(Pems.PRIVATE_PKCS1_MARKER) || credentials.credential
                  .startsWith(Pems.PRIVATE_PKCS8_MARKER));
   }


}
