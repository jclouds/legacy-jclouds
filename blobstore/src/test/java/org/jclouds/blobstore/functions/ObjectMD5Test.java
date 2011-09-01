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
package org.jclouds.blobstore.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.jclouds.crypto.CryptoStreams;
import org.jclouds.encryption.internal.JCECrypto;
import org.jclouds.http.HttpMessage;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ObjectMD5Test {
   private ObjectMD5 fn;

   public ObjectMD5Test() throws NoSuchAlgorithmException, CertificateException {
      fn = new ObjectMD5(new JCECrypto());
   }

   @Test
   public void testAlreadyHasMD5() {
      Payload payload = Payloads.newPayload("foo");
      payload.getContentMetadata().setContentMD5(new byte[] {});

      HttpMessage payloadEnclosing = HttpMessage.builder().payload(payload).build();

      assertEquals(fn.apply(payloadEnclosing), new byte[] {});
   }

   @Test
   public void testMD5PayloadEnclosing() throws IOException {
      Payload payload = Payloads.newPayload("foo");

      HttpMessage payloadEnclosing = HttpMessage.builder().payload(payload).build();

      assertEquals(fn.apply(payloadEnclosing), CryptoStreams.md5("foo".getBytes()));
   }

   @Test
   public void testMD5String() throws IOException {
      assertEquals(fn.apply("foo"), CryptoStreams.md5("foo".getBytes()));
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      fn.apply(null);
   }
}
