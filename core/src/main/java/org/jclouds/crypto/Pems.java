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

import static com.google.common.base.Charsets.US_ASCII;
import static com.google.common.base.Joiner.on;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Splitter.fixedLength;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.base.Throwables.propagateIfInstanceOf;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.io.ByteStreams.readBytes;
import static com.google.common.io.Closeables.closeQuietly;
import static org.jclouds.crypto.ASN1Codec.decodeRSAPrivateKey;
import static org.jclouds.crypto.ASN1Codec.decodeRSAPublicKey;
import static org.jclouds.crypto.ASN1Codec.encode;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
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
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import org.jclouds.io.InputSuppliers;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteProcessor;
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

   private static class PemProcessor<T> implements ByteProcessor<T> {
      private interface ResultParser<T> {
         T parseResult(byte[] bytes) throws IOException;
      }

      private final ByteArrayOutputStream out = new ByteArrayOutputStream();
      private final Map<String, ResultParser<T>> parsers;

      private PemProcessor(Map<String, ResultParser<T>> parsers) {
         this.parsers = checkNotNull(parsers, "parsers");
      }

      @Override
      public boolean processBytes(byte[] buf, int off, int len) {
         out.write(buf, off, len);
         return true;
      }

      @Override
      public T getResult() {
         Pem pem = PemReader.INSTANCE.apply(new String(out.toByteArray(), US_ASCII));
         String beginMarker = "-----BEGIN " + pem.type + "-----";
         checkState(parsers.containsKey(beginMarker), "Invalid PEM: no parsers for marker %s in %s", beginMarker,
               parsers.keySet());
         try {
            return parsers.get(beginMarker).parseResult(pem.content);
         } catch (IOException e) {
            throw new IllegalStateException("Invalid PEM : " + pem, e);
         }
      }
   }

   /**
    * Parsed PEM format
    * 
    * <pre>
    *  -----BEGIN RSA PRIVATE KEY-----
    *  Proc-Type: 4,ENCRYPTED
    *  DEK-Info: DES-EDE3-CBC,3F17F5316E2BAC89
    * 
    *  ...base64 encoded data...
    *  -----END RSA PRIVATE KEY-----
    * 
    * </pre>
    * 
    */
   private static enum PemReader implements Function<CharSequence, Pem> {
      INSTANCE;
      private static final String BEGIN = "-----BEGIN ";
      private static final String END = "-----END ";

      @Override
      public Pem apply(CharSequence chars) {
         checkNotNull(chars, "chars");
         BufferedReader reader = null;
         try {
           reader  =  new BufferedReader(new StringReader(chars.toString()));
            Optional<String> begin = skipUntilBegin(reader);
            checkArgument(begin.isPresent(), "chars %s doesn't contain % line", chars, BEGIN);
            String line = begin.get().substring(BEGIN.length());
            String type = line.substring(0, line.indexOf('-'));
            StringBuilder encoded = new StringBuilder();

            boolean reachedEnd = false;
            while ((line = reader.readLine()) != null) {
               if (line.indexOf(':') >= 0) { // skip headers
                  continue;
               }
               if (line.indexOf(END + type) != -1) {
                  reachedEnd = true;
                  break;
               }
               encoded.append(line.trim());
            }

            checkArgument(reachedEnd, "chars %s doesn't contain % line", chars, END);
            return new Pem(type, base64().decode(encoded.toString()));
         } catch (IOException e) {
            throw new IllegalStateException(String.format("io exception reading %s", chars), e);
         } finally {
            closeQuietly(reader);
         }

      }

      private static Optional<String> skipUntilBegin(BufferedReader reader) throws IOException {
         String line = reader.readLine();
         while (line != null && !line.startsWith(BEGIN)) {
            line = reader.readLine();
         }
         return Optional.fromNullable(line);
      }
   }

   private static final class Pem {

      private final String type;
      private final byte[] content;

      private Pem(String type, byte[] content) {
         this.type = checkNotNull(type, "type");
         this.content = checkNotNull(content, "content");
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
         return readBytes(supplier, processor);
      } catch (RuntimeException e) {
         propagateIfInstanceOf(e.getCause(), IOException.class);
         propagateIfInstanceOf(e, IOException.class);
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
            new PemProcessor<KeySpec>(ImmutableMap.<String, PemProcessor.ResultParser<KeySpec>> of(
                  PRIVATE_PKCS1_MARKER, DecodeRSAPrivateCrtKeySpec.INSTANCE, PRIVATE_PKCS8_MARKER,
                  new PemProcessor.ResultParser<KeySpec>() {
                     @Override
                     public KeySpec parseResult(byte[] bytes) throws IOException {
                        return new PKCS8EncodedKeySpec(bytes);
                     }

                  })));
   }

   /**
    * Decode PKCS#1 encoded private key into RSAPrivateCrtKeySpec.
    * 
    * @param keyBytes
    *           Encoded PKCS#1 rsa key.
    */
   private static enum DecodeRSAPrivateCrtKeySpec implements PemProcessor.ResultParser<KeySpec> {
      INSTANCE;

      @Override
      public KeySpec parseResult(byte[] bytes) throws IOException {
         return decodeRSAPrivateKey(bytes);
      }
   }

   /**
    * Executes {@link Pems#privateKeySpec(InputSupplier)} on the string which contains an encoded private key in PEM
    * format.
    * 
    * @param pem
    *           private key in pem encoded format.
    * @see Pems#privateKeySpec(InputSupplier)
    */
   public static KeySpec privateKeySpec(String pem) {
      try {
         return privateKeySpec(InputSuppliers.of(pem));
      } catch (IOException e) {
         throw propagate(e);
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
            new PemProcessor<KeySpec>(ImmutableMap.<String, PemProcessor.ResultParser<KeySpec>> of(PUBLIC_PKCS1_MARKER,
                  DecodeRSAPublicKeySpec.INSTANCE, PUBLIC_X509_MARKER, new PemProcessor.ResultParser<KeySpec>() {

                     @Override
                     public X509EncodedKeySpec parseResult(byte[] bytes) throws IOException {
                        return new X509EncodedKeySpec(bytes);
                     }

                  })));
   }

   /**
    * Decode PKCS#1 encoded public key into RSAPublicKeySpec.
    * <p>
    * Keys here can be in two different formats. They can have the algorithm encoded, or they can have only the modulus
    * and the public exponent.
    * <p>
    * The latter is not a valid PEM encoded file, but it is a valid DER encoded RSA key, so this method should also
    * support it.
    * 
    * @param keyBytes
    *           Encoded PKCS#1 rsa key.
    */
   private static enum DecodeRSAPublicKeySpec implements PemProcessor.ResultParser<KeySpec> {
      INSTANCE;
      @Override
      public KeySpec parseResult(byte[] bytes) throws IOException {
         return decodeRSAPublicKey(bytes);
      }
   }

   /**
    * Executes {@link Pems#publicKeySpec(InputSupplier)} on the string which contains an encoded public key in PEM
    * format.
    * 
    * @param pem
    *           public key in pem encoded format.
    * @see Pems#publicKeySpec(InputSupplier)
    */
   public static KeySpec publicKeySpec(String pem) throws IOException {
      return publicKeySpec(InputSuppliers.of(pem));
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
      final CertificateFactory certs = certFactory != null ? certFactory : CertificateFactory.getInstance("X.509");
      try {
         return fromPem(
               supplier,
               new PemProcessor<X509Certificate>(ImmutableMap.<String, PemProcessor.ResultParser<X509Certificate>> of(
                     CERTIFICATE_X509_MARKER, new PemProcessor.ResultParser<X509Certificate>() {

                        @Override
                        public X509Certificate parseResult(byte[] bytes) throws IOException {
                           try {
                              return (X509Certificate) certs.generateCertificate(new ByteArrayInputStream(bytes));
                           } catch (CertificateException e) {
                              throw new RuntimeException(e);
                           }
                        }

                     })));
      } catch (RuntimeException e) {
         propagateIfInstanceOf(e.getCause(), CertificateException.class);
         throw e;
      }
   }

   /**
    * Executes {@link Pems#x509Certificate(InputSupplier, CertificateFactory)} on the string which contains an X.509
    * certificate in PEM format.
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
    */
   public static String pem(PublicKey key) {
      String marker = key instanceof RSAPublicKey ? PUBLIC_PKCS1_MARKER : PUBLIC_X509_MARKER;
      return pem(key.getEncoded(), marker);
   }

   /**
    * encodes the {@link PrivateKey} to PEM format.
    */
   public static String pem(PrivateKey key) {
      String marker = key instanceof RSAPrivateCrtKey ? PRIVATE_PKCS1_MARKER : PRIVATE_PKCS8_MARKER;
      return pem(key instanceof RSAPrivateCrtKey ? encode(RSAPrivateCrtKey.class.cast(key)) : key.getEncoded(), marker);
   }

   private static String pem(byte[] encoded, String marker) {
      StringBuilder builder = new StringBuilder();
      builder.append(marker).append('\n');
      builder.append(on('\n').join(fixedLength(64).split(base64().encode(encoded)))).append('\n');
      builder.append(marker.replace("BEGIN", "END")).append('\n');
      return builder.toString();
   }

}
