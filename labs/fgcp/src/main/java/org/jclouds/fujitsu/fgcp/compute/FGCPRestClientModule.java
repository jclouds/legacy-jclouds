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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.KeySpec;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.net.ssl.SSLContext;

import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.Pems;
import org.jclouds.date.TimeStamp;
import org.jclouds.fujitsu.fgcp.FGCPApi;
import org.jclouds.fujitsu.fgcp.FGCPAsyncApi;
import org.jclouds.fujitsu.fgcp.handlers.FGCPRetryIfNotProxyAuthenticationFailureHandler;
import org.jclouds.fujitsu.fgcp.http.SSLContextWithKeysSupplier;
import org.jclouds.fujitsu.fgcp.location.SystemAndNetworkSegmentToLocationSupplier;
import org.jclouds.fujitsu.fgcp.services.AdditionalDiskApi;
import org.jclouds.fujitsu.fgcp.services.AdditionalDiskAsyncApi;
import org.jclouds.fujitsu.fgcp.services.BuiltinServerApi;
import org.jclouds.fujitsu.fgcp.services.BuiltinServerAsyncApi;
import org.jclouds.fujitsu.fgcp.services.DiskImageApi;
import org.jclouds.fujitsu.fgcp.services.DiskImageAsyncApi;
import org.jclouds.fujitsu.fgcp.services.FirewallApi;
import org.jclouds.fujitsu.fgcp.services.FirewallAsyncApi;
import org.jclouds.fujitsu.fgcp.services.LoadBalancerApi;
import org.jclouds.fujitsu.fgcp.services.LoadBalancerAsyncApi;
import org.jclouds.fujitsu.fgcp.services.PublicIPAddressApi;
import org.jclouds.fujitsu.fgcp.services.PublicIPAddressAsyncApi;
import org.jclouds.fujitsu.fgcp.services.SystemTemplateApi;
import org.jclouds.fujitsu.fgcp.services.SystemTemplateAsyncApi;
import org.jclouds.fujitsu.fgcp.services.VirtualDCApi;
import org.jclouds.fujitsu.fgcp.services.VirtualDCAsyncApi;
import org.jclouds.fujitsu.fgcp.services.VirtualServerApi;
import org.jclouds.fujitsu.fgcp.services.VirtualServerAsyncApi;
import org.jclouds.fujitsu.fgcp.services.VirtualSystemApi;
import org.jclouds.fujitsu.fgcp.services.VirtualSystemAsyncApi;
import org.jclouds.fujitsu.fgcp.xml.FGCPJAXBParser;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.io.InputSuppliers;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.location.suppliers.implicit.FirstNetwork;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.xml.XMLParser;

import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the FGCP connection. This module is added in FGCPContextBuilder.
 * 
 * @author Dies Koper
 */
@ConfiguresRestClient
public class FGCPRestClientModule extends
      RestClientModule<FGCPApi, FGCPAsyncApi> {

   @Resource
   Logger logger = Logger.NULL;

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap
         .<Class<?>, Class<?>> builder()
         //
         .put(VirtualDCApi.class, VirtualDCAsyncApi.class)
         .put(VirtualSystemApi.class, VirtualSystemAsyncApi.class)
         .put(VirtualServerApi.class, VirtualServerAsyncApi.class)
         .put(AdditionalDiskApi.class, AdditionalDiskAsyncApi.class)
         .put(SystemTemplateApi.class, SystemTemplateAsyncApi.class)
         .put(DiskImageApi.class, DiskImageAsyncApi.class)
         .put(BuiltinServerApi.class, BuiltinServerAsyncApi.class)
         .put(FirewallApi.class, FirewallAsyncApi.class)
         .put(LoadBalancerApi.class, LoadBalancerAsyncApi.class)
         .put(PublicIPAddressApi.class, PublicIPAddressAsyncApi.class)
         .build();

   public FGCPRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Override
   protected void bindErrorHandlers() {
      // bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseAWSErrorFromXmlContent.class);
      // bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseAWSErrorFromXmlContent.class);
      // bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseAWSErrorFromXmlContent.class);
   }

   @Override
   protected void installLocations() {
      super.installLocations();
      bind(ImplicitLocationSupplier.class).to(FirstNetwork.class).in(
            Scopes.SINGLETON);
      bind(LocationsSupplier.class).to(
            SystemAndNetworkSegmentToLocationSupplier.class).in(
            Scopes.SINGLETON);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(
            FGCPRetryIfNotProxyAuthenticationFailureHandler.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(XMLParser.class).to(FGCPJAXBParser.class);
      bind(new TypeLiteral<Supplier<SSLContext>>() {
      }).to(new TypeLiteral<SSLContextWithKeysSupplier>() {
      });
   }

   @Provides
   @TimeStamp
   protected Calendar provideCalendar() {
      return Calendar.getInstance();
   }

   /*
    * 
    * @Provides
    * 
    * @Singleton protected KeyStore
    * provideKeyStore(@Named(Constants.PROPERTY_IDENTITY) String
    * keyStoreFilename, @Named(Constants.PROPERTY_CREDENTIAL) String
    * keyStorePassword) throws KeyStoreException { KeyStore keyStore =
    * KeyStore.getInstance("pkcs12");
    * 
    * try { FileInputStream is = new
    * FileInputStream(checkNotNull(keyStoreFilename,
    * Constants.PROPERTY_IDENTITY)); keyStore.load(is,
    * checkNotNull(keyStorePassword,
    * Constants.PROPERTY_CREDENTIAL).toCharArray()); } catch (Exception e) { //
    * expecting IOException, NoSuchAlgorithmException, CertificateException
    * logger.error(e, "Keystore could not be opened: %s", keyStoreFilename); }
    * return keyStore; }
    * 
    * @Provides
    * 
    * @Singleton protected PrivateKey provideKey(Provider<KeyStore>
    * keyStoreProvider, @Named(Constants.PROPERTY_CREDENTIAL) String
    * keyPassword) throws KeyStoreException, NoSuchAlgorithmException,
    * UnrecoverableKeyException { KeyStore keyStore = keyStoreProvider.get();
    * if (keyStore == null) return null;
    * 
    * // retrieving 1st alias in keystore as expecting only one String alias =
    * checkNotNull(keyStore.aliases().nextElement(),
    * "first alias in keystore"); return (PrivateKey) keyStore.getKey(alias,
    * checkNotNull(keyPassword, Constants.PROPERTY_CREDENTIAL).toCharArray());
    * }
    */
   /*
    * maybe we can provide two authentication methods:
    * 
    * 1. same as DeltaCloud: User passes a folder name as identity and cert
    * password as credential Note: pass relative path (e.g. cert's path:
    * c:\jclouds\certs\dkoper\UserCert.p12: user passes 'dkoper': provider
    * impl. finds it under e.g. $USER_DIR or $CURRENT_DIR or pass absolute path
    * 2. no file access for GAE: User passes cert in PEM format (converted from
    * UserCert.p12 using openssl?) as identity and cert password as credential
    */
   @Provides
   @Singleton
   protected KeyStore provideKeyStore(Crypto crypto, @Identity String cert,
         @Credential String keyStorePassword) {
      KeyStore keyStore = null;
      try {
         keyStore = KeyStore.getInstance("PKCS12");

         // System.out.println("cert: " + cert);
         // System.out.println("pwd : " + keyStorePassword);
         File certFile = new File(checkNotNull(cert));
         if (certFile.isFile()) { // cert is path to pkcs12 file

            keyStore.load(new FileInputStream(certFile),
                  keyStorePassword.toCharArray());
         } else { // cert is PEM encoded, containing private key and certs

            // System.out.println("cert:\n" + cert);
            // split in private key and certs
            int privateKeyBeginIdx = cert.indexOf("-----BEGIN PRIVATE KEY");
            int privateKeyEndIdx = cert.indexOf("-----END PRIVATE KEY");
            String pemPrivateKey = cert.substring(privateKeyBeginIdx,
                  privateKeyEndIdx + 26);
            // System.out.println("***************");
            // System.out.println("pemPrivateKey:\n" + pemPrivateKey);
            // System.out.println("***************");

            String pemCerts = "";
            int certsBeginIdx = 0;

            do {
               certsBeginIdx = cert.indexOf("-----BEGIN CERTIFICATE",
                     certsBeginIdx);
               // System.out.println("begin:" + certsBeginIdx);

               if (certsBeginIdx >= 0) {
                  int certsEndIdx = cert.indexOf("-----END CERTIFICATE",
                        certsBeginIdx) + 26;
                  // System.out.println("end  :" + certsEndIdx);
                  pemCerts += cert.substring(certsBeginIdx, certsEndIdx);
                  certsBeginIdx = certsEndIdx;
               }
            } while (certsBeginIdx != -1);
            // System.out.println("***************");
            // System.out.println("pemCerts:\n" + pemCerts);
            // System.out.println("***************");

            /*
             * String pemCerts = "-----BEGIN "; Splitter pemSplitter =
             * Splitter.on("-----BEGIN ");
             * 
             * for (String part : pemSplitter.split(cert)) {
             * System.out.println("***************");
             * System.out.println("Part:\n" + part);
             * System.out.println("***************");
             * 
             * if (part.startsWith("PRIVATE KEY")
             */
            /* || part.startsWith("RSA PRIVATE KEY)" *//*
                                             * ) {
                                             * 
                                             * int certEndIdx =
                                             * part.lastIndexOf
                                             * ("-----END");
                                             * pemPrivateKey +=
                                             * part.substring(0,
                                             * certEndIdx + 26);
                                             * // take up to next
                                             * "-----" (i.e.
                                             * "-----END") //
                                             * Splitter
                                             * keySplitter =
                                             * Splitter
                                             * .on("-----").
                                             * omitEmptyStrings
                                             * ().trimResults();
                                             * //
                                             * Iterator<String>
                                             * iter =
                                             * keySplitter.
                                             * split(part
                                             * ).iterator(); //
                                             * String keyName =
                                             * iter.next() +
                                             * "-----\n"; //
                                             * pemPrivateKey +=
                                             * keyName; ////
                                             * System.out
                                             * .println
                                             * ("Skipping: '" +
                                             * iter.next() +
                                             * "'"); //
                                             * pemPrivateKey +=
                                             * iter.next(); //
                                             * pemPrivateKey +=
                                             * "\n-----END " +
                                             * keyName;
                                             * System.out.println
                                             * (
                                             * "/////////////////"
                                             * );
                                             * System.out.println
                                             * (
                                             * "pemPrivateKey:\n"
                                             * + pemPrivateKey);
                                             * System
                                             * .out.println(
                                             * "/////////////////"
                                             * ); } else if
                                             * (part.startsWith
                                             * ("CERTIFICATE")) {
                                             * 
                                             * // take up to next
                                             * "-----" (i.e.
                                             * "-----END") // or
                                             * take up to last
                                             * END CERTIFICATE?
                                             * int certEndIdx =
                                             * part.lastIndexOf (
                                             * "----- END CERTIFICATE"
                                             * ); // pemCerts +=
                                             * part. // Splitter
                                             * keySplitter =
                                             * Splitter
                                             * .on("-----").
                                             * omitEmptyStrings
                                             * (); // pemCerts +=
                                             * keySplitter
                                             * .split(part)
                                             * .iterator
                                             * ().next(); //
                                             * pemCerts +=
                                             * "-----BEGIN "; }
                                             * else { // ignore
                                             * the fluff in
                                             * between (Bag
                                             * Attributes, etc.)
                                             * } }
                                             */

            // parse private key
            KeySpec keySpec = Pems.privateKeySpec(InputSuppliers
                  .of(pemPrivateKey));
            PrivateKey privateKey = crypto.rsaKeyFactory().generatePrivate(
                  keySpec);

            // populate keystore with private key and certs
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            @SuppressWarnings("unchecked")
            Collection<Certificate> certs = (Collection<Certificate>) cf
                  .generateCertificates(new ByteArrayInputStream(pemCerts
                        .getBytes(Charsets.UTF_8)));
            keyStore.load(null);
            keyStore.setKeyEntry("dummy", privateKey,
                  keyStorePassword.toCharArray(),
                  certs.toArray(new java.security.cert.Certificate[0]));

            // System.out.println("private key: " + privateKey.getFormat() +
            // "; "
            // + privateKey.getAlgorithm() + "; class: " +
            // privateKey.getClass().getName());// + "; " + new
            // String(privateKey.getEncoded()));

         }
      } catch (Exception e) {
         /*
          * KeyStoreException, IOException, NoSuchAlgorithmException,
          * CertificateException, InvalidKeySpecException
          */
         throw new AuthorizationException("Error loading certificate", e);
      }

      return keyStore;
   }

}
