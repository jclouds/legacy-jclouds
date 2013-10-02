/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.dynect.v3.domain.rdata;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Map;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;

/**
 * Corresponds to the binary representation of the {@code SSHFP} (SSH
 * Fingerprint) RData
 * 
 * <h4>Example</h4>
 * 
 * <pre>
 * SSHFPData rdata = SSHFPData.builder().algorithm(2).fptype(1).fingerprint(&quot;123456789abcdef67890123456789abcdef67890&quot;)
 *       .build();
 * // or shortcut
 * SSHFPData rdata = SSHFPData.createDSA(&quot;123456789abcdef67890123456789abcdef67890&quot;);
 * </pre>
 * 
 * @see <a href="http://www.rfc-editor.org/rfc/rfc4255.txt">RFC 4255</a>
 */
public class SSHFPData extends ForwardingMap<String, Object> {

   /**
    * @param fingerprint
    *           {@code DSA} {@code SHA-1} fingerprint
    */
   public static SSHFPData createDSA(String fingerprint) {
      return builder().algorithm(2).fptype(1).fingerprint(fingerprint).build();
   }

   /**
    * @param fingerprint
    *           {@code RSA} {@code SHA-1} fingerprint
    */
   public static SSHFPData createRSA(String fingerprint) {
      return builder().algorithm(1).fptype(1).fingerprint(fingerprint).build();
   }

   private final int algorithm;
   private final int fptype;
   private final String fingerprint;

   @ConstructorProperties({ "algorithm", "fptype", "fingerprint" })
   private SSHFPData(int algorithm, int fptype, String fingerprint) {
      checkArgument(algorithm >= 0, "algorithm of %s must be unsigned", fingerprint);
      this.algorithm = algorithm;
      checkArgument(fptype >= 0, "fptype of %s must be unsigned", fingerprint);
      this.fptype = fptype;
      this.fingerprint = checkNotNull(fingerprint, "fingerprint");
      this.delegate = ImmutableMap.<String, Object> builder()
            .put("algorithm", algorithm)
            .put("fptype", fptype)
            .put("fingerprint", fingerprint).build();
   }

   /**
    * This algorithm number octet describes the algorithm of the public key.
    * 
    * @return most often {@code 1} for {@code RSA} or {@code 2} for {@code DSA}.
    */
   public int getAlgorithm() {
      return algorithm;
   }

   /**
    * The fingerprint fptype octet describes the message-digest algorithm used
    * to calculate the fingerprint of the public key.
    * 
    * @return most often {@code 1} for {@code SHA-1}
    */
   public int getType() {
      return fptype;
   }

   /**
    * The fingerprint calculated over the public key blob.
    */
   public String getFingerprint() {
      return fingerprint;
   }

   public static final class Builder {
      private int algorithm;
      private int fptype;
      private String fingerprint;

      /**
       * @see SSHFPData#getAlgorithm()
       */
      public SSHFPData.Builder algorithm(int algorithm) {
         this.algorithm = algorithm;
         return this;
      }

      /**
       * @see SSHFPData#getType()
       */
      public SSHFPData.Builder fptype(int fptype) {
         this.fptype = fptype;
         return this;
      }

      /**
       * @see SSHFPData#getFingerprint()
       */
      public SSHFPData.Builder fingerprint(String fingerprint) {
         this.fingerprint = fingerprint;
         return this;
      }

      public SSHFPData build() {
         return new SSHFPData(algorithm, fptype, fingerprint);
      }
   }

   public static SSHFPData.Builder builder() {
      return new Builder();
   }

   // transient to avoid serializing by default, for example in json
   private final transient ImmutableMap<String, Object> delegate;

   @Override
   protected Map<String, Object> delegate() {
      return delegate;
   }
}
