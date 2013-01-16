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
package org.jclouds.fujitsu.fgcp.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.config.SSLModule.TrustAllCerts;
import org.jclouds.location.Provider;

import com.google.common.base.Supplier;

/**
 * TODO this code needs to be completely refactored. It needs to stop using KeyStore of at all possible and definitely
 * the local filesystem. Please look at oauth for examples on how to do this via PEMs.
 */
@Deprecated
@Singleton
public class SSLContextWithKeysSupplier implements Supplier<SSLContext> {
   private final Supplier<KeyStore> keyStore;
   private final TrustManager[] trustManager;
   private final Supplier<Credentials> creds;

   @Inject
   SSLContextWithKeysSupplier(Supplier<KeyStore> keyStore, @Provider Supplier<Credentials> creds, HttpUtils utils,
         TrustAllCerts trustAllCerts) {
      this.keyStore = keyStore;
      this.trustManager = utils.trustAllCerts() ? new TrustManager[] { trustAllCerts } : null;
      this.creds = creds;
   }

   @Override
   public SSLContext get() {
      Credentials currentCreds = checkNotNull(creds.get(), "credential supplier returned null");
      String keyStorePassword = checkNotNull(currentCreds.credential,
            "credential supplier returned null credential (should be keyStorePassword)");
      KeyManagerFactory kmf;
      try {
         kmf = KeyManagerFactory.getInstance("SunX509");
         kmf.init(keyStore.get(), keyStorePassword.toCharArray());
         SSLContext sc = SSLContext.getInstance("TLS");
         sc.init(kmf.getKeyManagers(), trustManager, new SecureRandom());
         return sc;
      } catch (NoSuchAlgorithmException e) {
         throw propagate(e);
      } catch (UnrecoverableKeyException e) {
         throw propagate(e);
      } catch (KeyStoreException e) {
         throw propagate(e);
      } catch (KeyManagementException e) {
         throw propagate(e);
      }
   }
}
