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
package org.jclouds.ec2.functions;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.BaseEncoding.base64;

import javax.inject.Singleton;

import org.jclouds.util.Predicates2;

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
   private static final Predicate<String> ALLOWED_MARKERS = Predicates.or(
      Predicates2.startsWith("ssh-rsa"),
      Predicates2.startsWith("-----BEGIN CERTIFICATE-----"),
      Predicates2.startsWith("---- BEGIN SSH2 PUBLIC KEY ----"));

   @Override
   public String apply(Object from) {
      String fromString = checkNotNull(from, "input").toString();
      checkArgument(ALLOWED_MARKERS.apply(fromString), "must be a ssh public key, conforming to %s ", ALLOWED_MARKERS);
      return base64().encode(fromString.getBytes(UTF_8));
   }
}
