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
package org.jclouds.azure.management.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.Pems;
import org.jclouds.domain.Credentials;
import org.jclouds.io.InputSuppliers;
import org.jclouds.location.Provider;

import com.google.common.base.Charsets;
import com.google.common.base.Supplier;

/**
 * TODO this code needs to be completely refactored. It needs to stop using KeyStore of at all possible and definitely
 * the local filesystem. Please look at oauth for examples on how to do this via PEMs.
 */
@Deprecated
@Singleton
public class KeyStoreSupplier implements Supplier<KeyStore> {
   private final Crypto crypto;
   private final Supplier<Credentials> creds;

   @Inject
   KeyStoreSupplier(Crypto crypto, @Provider Supplier<Credentials> creds) {
      this.crypto = crypto;
      this.creds = creds;
   }

   @Override
   public KeyStore get() {
      Credentials currentCreds = checkNotNull(creds.get(), "credential supplier returned null");
      String cert = checkNotNull(currentCreds.identity, "credential supplier returned null identity (should be cert)");
      String keyStorePassword = checkNotNull(currentCreds.credential,
            "credential supplier returned null credential (should be keyStorePassword)");
      try {
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
      } catch (NoSuchAlgorithmException e) {
         throw propagate(e);
      } catch (KeyStoreException e) {
         throw propagate(e);
      } catch (CertificateException e) {
         throw propagate(e);
      } catch (FileNotFoundException e) {
         throw propagate(e);
      } catch (IOException e) {
         throw propagate(e);
      } catch (InvalidKeySpecException e) {
         throw propagate(e);
      }
   }
}
