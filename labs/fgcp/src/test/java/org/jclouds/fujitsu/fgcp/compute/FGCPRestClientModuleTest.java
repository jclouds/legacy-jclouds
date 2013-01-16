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
package org.jclouds.fujitsu.fgcp.compute;

import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import org.jclouds.crypto.Crypto;
import org.jclouds.domain.Credentials;
import org.jclouds.fujitsu.fgcp.suppliers.KeyStoreSupplier;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Dies Koper
 */
@Test(groups = "unit", testName = "FGCPRestClientModuleTest")
public class FGCPRestClientModuleTest {

   protected FGCPRestClientModule module;
   protected Crypto crypto;

   @BeforeTest
   protected void createCrypto() {
      Injector i = Guice.createInjector();
      crypto = i.getInstance(Crypto.class);
   }

   @BeforeTest
   protected void createRestClientModule() {
      Injector i = Guice.createInjector();
      module = i.getInstance(FGCPRestClientModule.class);
   }

   public void testKeyStoreAsPkcs12() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException,
         KeyStoreException, CertificateException {
      assertNotNull(crypto);
      assertNotNull(module);

      // self-signed dummy cert:
      // keytool -genkey -alias test-fgcp -keyalg RSA -keysize 1024 -validity 5475 -dname "CN=localhost" -keystore
      // jclouds-test-fgcp.p12 -storepass jcloudsjclouds -storetype pkcs12
      String cert = "/certs/jclouds-test-fgcp.p12";
      URL url = this.getClass().getResource(cert);
      String certPath = url.getFile();

      KeyStore ks = new KeyStoreSupplier(crypto, Suppliers.ofInstance(new Credentials(certPath, "jcloudsjclouds")))
            .get();

      assertNotNull(ks.getCertificate("test-fgcp"), "cert with alias");
   }

   /*
    * public void testKeyStoreAsPEM() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException,
    * KeyStoreException, CertificateException { assertNotNull(crypto); assertNotNull(module);
    * 
    * //openssl pkcs12 -nodes -in jclouds-test-fgcp.p12 -out jclouds-test-fgcp.pem // String privKeyFilename =
    * "D:\\UserCert.pem.pkcs12-nodes";//_nobags"; String cert = "/certs/jclouds-test-fgcp.pem"; String keyPassword =
    * "jcloudsjclouds";
    * 
    * URL url = this.getClass().getResource(cert); String certPath = url.getFile(); Scanner scanner = new Scanner(new
    * File(certPath)); String content = scanner.useDelimiter("\\A").next();
    * 
    * KeyStore ks = module.provideKeyStore(crypto, content, keyPassword);
    * 
    * assertNotNull(ks.getCertificate("test-fgcp"), "cert with alias"); }
    */
}
