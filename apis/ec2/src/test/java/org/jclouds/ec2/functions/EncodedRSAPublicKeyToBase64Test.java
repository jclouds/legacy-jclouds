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

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.testng.annotations.Test;

/**
 * Tests behavior of {@code EncodedRSAPublicKeyToBase64}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class EncodedRSAPublicKeyToBase64Test {
   EncodedRSAPublicKeyToBase64 function = new EncodedRSAPublicKeyToBase64();

   public void testAllowedMarkers() throws IOException {
      assertEquals(function.apply("-----BEGIN CERTIFICATE-----"), "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0t");
      assertEquals(function.apply("ssh-rsa"), "c3NoLXJzYQ==");
      assertEquals(function.apply("---- BEGIN SSH2 PUBLIC KEY ----"), "LS0tLSBCRUdJTiBTU0gyIFBVQkxJQyBLRVkgLS0tLQ==");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testDisallowedMarkersIllegalArgument() throws IOException {
      function.apply("ssh-dsa");
   }
}
