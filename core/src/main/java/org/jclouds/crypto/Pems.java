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
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import net.oauth.signature.pem.PEMReader;
import net.oauth.signature.pem.PKCS1EncodedKeySpec;

import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.jclouds.crypto.Pems.PemProcessor.ResultParser;
import org.jclouds.crypto.pem.PKCS1EncodedPublicKeySpec;
import org.jclouds.io.InputSuppliers;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.InputSupplier;

/**
 * Reads and writes PEM encoded Strings and Streams
 * 
 * @author Adrian Cole
 */
@Beta
public class Pems {
   public static final String PRIVATE_PKCS1_MARKER = "-----BEGIN RSA PRIVATE KEY-----";
   public static final String PRIVATE_PKCS8_MARKER = "-----BEGIN PRIVATE KEY-----";
   public static final String CERTIFICATE_X509_MARKER = "-----BEGIN CERTIFICATE-----";
   public static final String PUBLIC_X509_MARKER = "-----BEGIN PUBLIC KEY-----";
   public static final String PUBLIC_PKCS1_MARKER = "-----BEGIN RSA PUBLIC KEY-----";

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
               throw new IOException(String.format("Invalid PEM file: no parsers for marker %s in %s",
                     reader.getBeginMarker(), parsers.keySet()));
            }
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
   }

   /**
    * Returns the object of generic type {@code T} that is pem encoded in the
    * supplier.
    * 
    * @param supplier
    *           the input stream factory
    * @param marker
    *           header that begins the PEM block
    * @param processor
    *           how to parser the object from a byte array
    * @return the object of generic type {@code T} which was PEM encoded in the
    *         stream
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
      return fromPem(
            supplier,
            new PemProcessor<KeySpec>(ImmutableMap.<String, ResultParser<KeySpec>> of(PRIVATE_PKCS1_MARKER,
                  new ResultParser<KeySpec>() {

                     public KeySpec parseResult(byte[] bytes) throws IOException {
                        return (new PKCS1EncodedKeySpec(bytes)).getKeySpec();
                     }

                  }, PRIVATE_PKCS8_MARKER, new ResultParser<KeySpec>() {

                     public KeySpec parseResult(byte[] bytes) throws IOException {
                        return new PKCS8EncodedKeySpec(bytes);
                     }

                  })));
   }

   /**
    * Executes {@link Pems#privateKeySpec(InputSupplier)} on the string which
    * contains an encoded private key in PEM format.
    * 
    * @param pem
    *           private key in pem encoded format.
    * @see Pems#privateKeySpec(InputSupplier)
    */
   public static KeySpec privateKeySpec(String pem) {
      try {
         return privateKeySpec(InputSuppliers.of(pem));
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   /**
    * Returns the {@link KeySpec} that is pem encoded in the supplier.
    * 
    * @param supplier
    *           the input stream factory
    * 
    * @return the {@link KeySpec} which was PEM encoded in the stream
    * @throws IOException
    *            if an I/O error occurs
    */
   public static KeySpec publicKeySpec(InputSupplier<? extends InputStream> supplier) throws IOException {
      return fromPem(
            supplier,
            new PemProcessor<KeySpec>(ImmutableMap.<String, ResultParser<KeySpec>> of(PUBLIC_PKCS1_MARKER,
                  new ResultParser<KeySpec>() {

                     public KeySpec parseResult(byte[] bytes) throws IOException {
                        return (new PKCS1EncodedPublicKeySpec(bytes)).getKeySpec();
                     }

                  }, PUBLIC_X509_MARKER, new ResultParser<KeySpec>() {

                     public X509EncodedKeySpec parseResult(byte[] bytes) throws IOException {
                        return new X509EncodedKeySpec(bytes);
                     }

                  })));
   }

   /**
    * Executes {@link Pems#publicKeySpec(InputSupplier)} on the string which
    * contains an encoded public key in PEM format.
    * 
    * @param pem
    *           public key in pem encoded format.
    * @see Pems#publicKeySpec(InputSupplier)
    */
   public static KeySpec publicKeySpec(String pem) throws IOException {
      return publicKeySpec(InputSuppliers.of(pem));
   }

   /**
    * Returns the {@link X509EncodedKeySpec} that is pem encoded in the
    * supplier.
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
         return fromPem(
               supplier,
               new PemProcessor<X509Certificate>(ImmutableMap.<String, ResultParser<X509Certificate>> of(
                     CERTIFICATE_X509_MARKER, new ResultParser<X509Certificate>() {

                        public X509Certificate parseResult(byte[] bytes) throws IOException {
                           try {
                              return (X509Certificate) finalCertFactory.generateCertificate(new ByteArrayInputStream(
                                    bytes));
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
    * Executes {@link Pems#x509Certificate(InputSupplier, CertificateFactory)}
    * on the string which contains an X.509 certificate in PEM format.
    * 
    * @param pem
    *           certificate in pem encoded format.
    * @see Pems#x509Certificate(InputSupplier, CertificateFactory)
    */
   public static X509Certificate x509Certificate(String pem) throws IOException, CertificateException {
      return x509Certificate(InputSuppliers.of(pem), null);
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
   public static String pem(X509Certificate cert) throws CertificateEncodingException {
      String marker = CERTIFICATE_X509_MARKER;
      return pem(cert.getEncoded(), marker);
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
   public static String pem(PublicKey key) {
      String marker = key instanceof RSAPublicKey ? PUBLIC_PKCS1_MARKER : PUBLIC_X509_MARKER;
      return pem(key.getEncoded(), marker);
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
   // TODO: understand why pem isn't passing SshKeysTest.testCanGenerate where
   // keys are checked to match.
   public static String pem(PrivateKey key) {
      String marker = key instanceof RSAPrivateCrtKey ? PRIVATE_PKCS1_MARKER : PRIVATE_PKCS8_MARKER;
      return pem(key instanceof RSAPrivateCrtKey ? getEncoded(RSAPrivateCrtKey.class.cast(key)) : key.getEncoded(),
            marker);
   }

   // TODO find a way to do this without using bouncycastle
   public static byte[] getEncoded(RSAPrivateCrtKey key) {
      RSAPrivateKeyStructure keyStruct = new RSAPrivateKeyStructure(key.getModulus(), key.getPublicExponent(),
            key.getPrivateExponent(), key.getPrimeP(), key.getPrimeQ(), key.getPrimeExponentP(),
            key.getPrimeExponentQ(), key.getCrtCoefficient());

      ByteArrayOutputStream bOut = new ByteArrayOutputStream();
      ASN1OutputStream aOut = new ASN1OutputStream(bOut);

      try {
         aOut.writeObject(keyStruct);
         aOut.close();
      } catch (IOException e) {
         Throwables.propagate(e);
      }

      return bOut.toByteArray();
   }

   private static String pem(byte[] key, String marker) {
      return pem(key, marker, 64);
   }

   static String pem(byte[] key, String marker, int length) {
      return new StringBuilder(marker + "\n")
            .append(Joiner.on('\n').join(Splitter.fixedLength(length).split(CryptoStreams.base64(key))))
            .append("\n" + marker.replace("BEGIN", "END") + "\n").toString().trim();
   }

}
