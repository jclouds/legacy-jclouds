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
package org.jclouds.ec2.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.crypto.CryptoStreams;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ImportKeyPair.html"
 *      />
 * @author Adrian Cole
 */
@Singleton
public class EncodedRSAPublicKeyToBase64 implements Function<Object, String> {
   private static Predicate<Object> startsWith(String value) {
      return new ToStringStartsWith(value);
   }

   private static final class ToStringStartsWith implements Predicate<Object> {
      private final String value;

      private ToStringStartsWith(String value) {
         this.value = value;
      }

      @Override
      public boolean apply(Object input) {
         return input.toString().startsWith(value);
      }

      public String toString() {
         return "toStringStartsWith(" + value + ")";
      }
   }

   @SuppressWarnings("unchecked")
   private static final Predicate<Object> ALLOWED_MARKERS = Predicates.or(startsWith("ssh-rsa"),
         startsWith("-----BEGIN CERTIFICATE-----"), startsWith("---- BEGIN SSH2 PUBLIC KEY ----"));

   @Override
   public String apply(Object from) {
      checkNotNull(from, "input");
      checkArgument(ALLOWED_MARKERS.apply(from), "must be a ssh public key, conforming to %s ", ALLOWED_MARKERS);
      return CryptoStreams.base64(from.toString().getBytes(Charsets.UTF_8));
   }
}
