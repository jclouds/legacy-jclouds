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
package org.jclouds.azure.management.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Collection;
import java.util.Map;

import javax.inject.Singleton;
import javax.net.ssl.SSLContext;

import org.jclouds.azure.management.AzureManagementApi;
import org.jclouds.azure.management.AzureManagementAsyncApi;
import org.jclouds.azure.management.features.DiskApi;
import org.jclouds.azure.management.features.DiskAsyncApi;
import org.jclouds.azure.management.features.HostedServiceApi;
import org.jclouds.azure.management.features.HostedServiceAsyncApi;
import org.jclouds.azure.management.features.LocationApi;
import org.jclouds.azure.management.features.LocationAsyncApi;
import org.jclouds.azure.management.features.OSImageApi;
import org.jclouds.azure.management.features.OSImageAsyncApi;
import org.jclouds.azure.management.features.OperationApi;
import org.jclouds.azure.management.features.OperationAsyncApi;
import org.jclouds.azure.management.features.RoleApi;
import org.jclouds.azure.management.features.RoleAsyncApi;
import org.jclouds.azure.management.suppliers.SSLContextWithKeysSupplier;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.Pems;
import org.jclouds.io.InputSuppliers;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Configures the Azure Service Management connection.
 * 
 * @author Gerald Pereira
 */
@ConfiguresRestClient
public class AzureManagementRestClientModule extends RestClientModule<AzureManagementApi, AzureManagementAsyncApi> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
            .put(LocationApi.class, LocationAsyncApi.class)
            .put(RoleApi.class, RoleAsyncApi.class)
            .put(HostedServiceApi.class, HostedServiceAsyncApi.class)
            .put(OSImageApi.class, OSImageAsyncApi.class)
            .put(OperationApi.class, OperationAsyncApi.class)
            .put(DiskApi.class, DiskAsyncApi.class)
            .build();

   public AzureManagementRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<Supplier<SSLContext>>() {
      }).to(new TypeLiteral<SSLContextWithKeysSupplier>() {
      });
   }

   /**
    * TODO copied from FGCP, should be put in a common place
    * 
    * @author Dies Koper
    */
   @Provides
   @Singleton
   protected KeyStore provideKeyStore(Crypto crypto, @Identity String cert, @Credential String keyStorePassword)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException,
            InvalidKeySpecException {
      KeyStore keyStore = KeyStore.getInstance("PKCS12");

      File certFile = new File(checkNotNull(cert));
      if (certFile.isFile()) { // cert is path to pkcs12 file

         keyStore.load(new FileInputStream(certFile), keyStorePassword.toCharArray());
      } else { // cert is PEM encoded, containing private key and certs

         // split in private key and certs
         int privateKeyBeginIdx = cert.indexOf("-----BEGIN PRIVATE KEY");
         int privateKeyEndIdx = cert.indexOf("-----END PRIVATE KEY");
         String pemPrivateKey = cert.substring(privateKeyBeginIdx, privateKeyEndIdx + 26);

         String pemCerts = "";
         int certsBeginIdx = 0;

         do {
            certsBeginIdx = cert.indexOf("-----BEGIN CERTIFICATE", certsBeginIdx);

            if (certsBeginIdx >= 0) {
               int certsEndIdx = cert.indexOf("-----END CERTIFICATE", certsBeginIdx) + 26;
               pemCerts += cert.substring(certsBeginIdx, certsEndIdx);
               certsBeginIdx = certsEndIdx;
            }
         } while (certsBeginIdx != -1);

         // parse private key
         KeySpec keySpec = Pems.privateKeySpec(InputSuppliers.of(pemPrivateKey));
         PrivateKey privateKey = crypto.rsaKeyFactory().generatePrivate(keySpec);

         // populate keystore with private key and certs
         CertificateFactory cf = CertificateFactory.getInstance("X.509");
         @SuppressWarnings("unchecked")
         Collection<Certificate> certs = (Collection<Certificate>) cf.generateCertificates(new ByteArrayInputStream(
                  pemCerts.getBytes(Charsets.UTF_8)));
         keyStore.load(null);
         keyStore.setKeyEntry("dummy", privateKey, keyStorePassword.toCharArray(),
                  certs.toArray(new java.security.cert.Certificate[0]));

      }

      return keyStore;
   }
}
