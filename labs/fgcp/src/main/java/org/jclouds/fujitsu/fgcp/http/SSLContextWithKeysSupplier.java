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
package org.jclouds.fujitsu.fgcp.http;

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

import org.jclouds.http.HttpUtils;
import org.jclouds.http.config.SSLModule.TrustAllCerts;
import org.jclouds.rest.annotations.Credential;

import com.google.common.base.Supplier;

/**
 * SSLContext supplier with a configured key manager to enable client
 * authentication with certificates.
 * 
 * @author Dies Koper
 */
@Singleton
public class SSLContextWithKeysSupplier implements Supplier<SSLContext> {
   private SSLContext sc;

   @Inject
   SSLContextWithKeysSupplier(KeyStore keyStore,
         @Credential String keyStorePassword, HttpUtils utils,
         TrustAllCerts trustAllCerts) throws NoSuchAlgorithmException,
         KeyStoreException, UnrecoverableKeyException,
         KeyManagementException {

      TrustManager[] trustManager = null;
      if (utils.trustAllCerts()) {
         trustManager = new TrustManager[] { trustAllCerts };
      }
      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(keyStore, keyStorePassword.toCharArray());
      sc = SSLContext.getInstance("TLS");
      sc.init(kmf.getKeyManagers(), trustManager, new SecureRandom());
   }

   @Override
   public SSLContext get() {
      return sc;
   }
}
