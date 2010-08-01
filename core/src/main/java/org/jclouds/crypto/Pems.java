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
package org.jclouds.crypto;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import javax.annotation.Nullable;

import net.oauth.signature.pem.PEMReader;
import net.oauth.signature.pem.PKCS1EncodedKeySpec;

import org.jclouds.crypto.Pems.PemProcessor.ResultParser;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

/**
 * Reads and writes PEM encoded Strings and Streams
 * 
 * @author Adrian Cole
 */
@Beta
public class Pems {

   public static class PemProcessor<T> implements com.google.common.io.ByteProcessor<T> {
      public interface ResultParser<T> {
         T parseResult(byte[] bytes) throws IOException;
      }

      private final ByteArrayOutputStream out = new ByteArrayOutputStream();
      private final Map<String, ResultParser<T>> parsers;

      public PemProcessor(Map<String, ResultParser<T>> parsers) {
         this.parsers = checkNotNull(parsers, "parsers");
      }

      public boolean processBytes(byte[] buf, int off, int len) {
         out.write(buf, off, len);
         return true;
      }

      public T getResult() {
         try {
            PEMReader reader = new PEMReader(out.toByteArray());
            byte[] bytes = reader.getDerBytes();
            if (parsers.containsKey(reader.getBeginMarker())) {
               return parsers.get(reader.getBeginMarker()).parseResult(bytes);
            } else {
               throw new IOException(String.format("Invalid PEM file: no parsers for marker %s in %s", reader
                        .getBeginMarker(), parsers.keySet()));
            }
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
   }

   /**
    * Returns the object of generic type {@code T} that is pem encoded in the supplier.
    * 
    * @param supplier
    *           the input stream factory
    * @param marker
    *           header that begins the PEM block
    * @param processor
    *           how to parser the object from a byte array
    * @return the object of generic type {@code T} which was PEM encoded in the stream
    * @throws IOException
    *            if an I/O error occurs
    */
   public static <T> T fromPem(InputSupplier<? extends InputStream> supplier, PemProcessor<T> processor)
            throws IOException {
      try {
         return com.google.common.io.ByteStreams.readBytes(supplier, processor);
      } catch (RuntimeException e) {
         if (e.getCause() != null && e.getCause() instanceof IOException) {
            throw (IOException) e.getCause();
         }
         throw e;
      }
   }

   /**
    * Returns the {@link RSAPrivateKeySpec} that is pem encoded in the supplier.
    * 
    * @param supplier
    *           the input stream factory
    * 
    * @return the {@link RSAPrivateKeySpec} which was PEM encoded in the stream
    * @throws IOException
    *            if an I/O error occurs
    */
   public static KeySpec privateKeySpec(InputSupplier<? extends InputStream> supplier) throws IOException {
      return fromPem(supplier, new PemProcessor<KeySpec>(ImmutableMap.<String, ResultParser<KeySpec>> of(
               PEMReader.PRIVATE_PKCS1_MARKER, new ResultParser<KeySpec>() {

                  public KeySpec parseResult(byte[] bytes) throws IOException {
                     return (new PKCS1EncodedKeySpec(bytes)).getKeySpec();
                  }

               }, PEMReader.PRIVATE_PKCS8_MARKER, new ResultParser<KeySpec>() {

                  public KeySpec parseResult(byte[] bytes) throws IOException {
                     return new PKCS8EncodedKeySpec(bytes);
                  }

               })));
   }

   /**
    * Returns the {@link X509EncodedKeySpec} that is pem encoded in the supplier.
    * 
    * @param supplier
    *           the input stream factory
    * 
    * @return the {@link X509EncodedKeySpec} which was PEM encoded in the stream
    * @throws IOException
    *            if an I/O error occurs
    */
   public static X509EncodedKeySpec publicKeySpec(InputSupplier<? extends InputStream> supplier) throws IOException {
      return fromPem(supplier, new PemProcessor<X509EncodedKeySpec>(ImmutableMap
               .<String, ResultParser<X509EncodedKeySpec>> of(PEMReader.PUBLIC_X509_MARKER,
                        new ResultParser<X509EncodedKeySpec>() {

                           public X509EncodedKeySpec parseResult(byte[] bytes) throws IOException {
                              return new X509EncodedKeySpec(bytes);
                           }

                        })));
   }

   /**
    * Returns the {@link X509EncodedKeySpec} that is pem encoded in the supplier.
    * 
    * @param supplier
    *           the input stream factory
    * @param certFactory
    *           or null to use default
    * 
    * @return the {@link X509EncodedKeySpec} which was PEM encoded in the stream
    * @throws IOException
    *            if an I/O error occurs
    * @throws CertificateException
    */
   public static X509Certificate x509Certificate(InputSupplier<? extends InputStream> supplier,
            @Nullable CertificateFactory certFactory) throws IOException, CertificateException {
      final CertificateFactory finalCertFactory = certFactory != null ? certFactory : CertificateFactory
               .getInstance("X.509");
      try {
         return fromPem(supplier, new PemProcessor<X509Certificate>(ImmutableMap
                  .<String, ResultParser<X509Certificate>> of(PEMReader.CERTIFICATE_X509_MARKER,
                           new ResultParser<X509Certificate>() {

                              public X509Certificate parseResult(byte[] bytes) throws IOException {
                                 try {
                                    return (X509Certificate) finalCertFactory
                                             .generateCertificate(new ByteArrayInputStream(bytes));
                                 } catch (CertificateException e) {
                                    throw new RuntimeException(e);
                                 }
                              }

                           })));
      } catch (RuntimeException e) {
         if (e.getCause() != null && e.getCause() instanceof CertificateException) {
            throw (CertificateException) e.getCause();
         }
         throw e;
      }
   }

   /**
    * encodes the {@link X509Certificate} to PEM format.
    * 
    * @param cert
    *           what to encode
    * @return the PEM encoded certificate
    * @throws IOException
    * @throws CertificateEncodingException
    */
   public static String pem(X509Certificate cert) throws IOException, CertificateEncodingException {
      return new StringBuilder("-----BEGIN CERTIFICATE-----\n").append(
               CryptoStreams.base64Encode(ByteStreams.newInputStreamSupplier(cert.getEncoded()))).append(
               "\n-----END CERTIFICATE-----\n").toString();
   }

   /**
    * encodes the {@link PublicKey} to PEM format.
    * 
    * @param cert
    *           what to encode
    * @return the PEM encoded public key
    * @throws IOException
    * @throws CertificateEncodingException
    */
   public static String pem(PublicKey key) throws IOException {
      return new StringBuilder("-----BEGIN PUBLIC KEY-----\n").append(
               CryptoStreams.base64Encode(ByteStreams.newInputStreamSupplier(key.getEncoded()))).append(
               "\n-----END PUBLIC KEY-----\n").toString();
   }

   /**
    * encodes the {@link PrivateKey} to PEM format. Note
    * 
    * @param cert
    *           what to encode
    * @return the PEM encoded private key
    * @throws IOException
    * @throws CertificateEncodingException
    */
   public static String pem(PrivateKey key) throws IOException {
      return new StringBuilder("-----BEGIN PRIVATE KEY-----\n").append(
               CryptoStreams.base64Encode(ByteStreams.newInputStreamSupplier(key.getEncoded()))).append(
               "\n-----END PRIVATE KEY-----\n").toString();
   }

}
