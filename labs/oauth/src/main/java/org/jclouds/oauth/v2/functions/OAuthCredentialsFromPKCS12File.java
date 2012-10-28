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
package org.jclouds.oauth.v2.functions;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import org.jclouds.oauth.v2.domain.OAuthCredentials;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.PrivateKey;

import static org.jclouds.oauth.v2.OAuthConstants.PKCS_CERITIFICATE_KEY_PASSWORD;
import static org.jclouds.oauth.v2.OAuthConstants.PKCS_CERTIFICATE_KEY_NAME;

/**
 * Loads {@link OAuthCredentials} from a PKCS12 keystore file. Needs the key name and the key password in addition to
 * the keystore file location.
 *
 * @author David Alves
 */
@Singleton
public class OAuthCredentialsFromPKCS12File implements Supplier<OAuthCredentials> {

   private final String keystorePath;
   private final String keyName;
   private final String keyPassword;
   private final String identity;

   @Inject
   public OAuthCredentialsFromPKCS12File(@Identity String identity, @Credential String keystorePath,
                                         @Named(PKCS_CERTIFICATE_KEY_NAME)
                                         String keyName, @Named(PKCS_CERITIFICATE_KEY_PASSWORD) String keyPassword) {
      this.identity = identity;
      this.keystorePath = keystorePath;
      this.keyName = keyName;
      this.keyPassword = keyPassword;
   }


   @Override
   public OAuthCredentials get() {
      File file = new File(keystorePath);
      if (!file.exists()) {
         try {
            throw new FileNotFoundException("Private key file not found in: " + keystorePath);
         } catch (FileNotFoundException e) {
            Throwables.propagate(e);
         }
      }

      KeyStore keyStore = null;
      try {
         keyStore = KeyStore.getInstance("PKCS12");
         keyStore.load(new FileInputStream(file), null);
         PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyName, keyPassword.toCharArray());
         return new OAuthCredentials.Builder().identity(identity).credential
                 (keystorePath).privateKey(privateKey).build();
      } catch (Exception e) {
         throw new AuthorizationException("Cannot access private key.", e);
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      OAuthCredentialsFromPKCS12File that = (OAuthCredentialsFromPKCS12File) o;

      if (identity != null ? !identity.equals(that.identity) : that.identity != null) return false;
      if (keyName != null ? !keyName.equals(that.keyName) : that.keyName != null) return false;
      if (keyPassword != null ? !keyPassword.equals(that.keyPassword) : that.keyPassword != null) return false;
      if (keystorePath != null ? !keystorePath.equals(that.keystorePath) : that.keystorePath != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = keystorePath != null ? keystorePath.hashCode() : 0;
      result = 31 * result + (keyName != null ? keyName.hashCode() : 0);
      result = 31 * result + (keyPassword != null ? keyPassword.hashCode() : 0);
      result = 31 * result + (identity != null ? identity.hashCode() : 0);
      return result;
   }
}
