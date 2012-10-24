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
package org.jclouds.http.filters;

import static org.testng.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.encryption.internal.JCECrypto;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BasicAuthenticationTest {

   private static final String USER = "Aladdin";
   private static final String PASSWORD = "open sesame";

   public void testAuth() throws NoSuchAlgorithmException, CertificateException {
      BasicAuthentication filter = new BasicAuthentication(USER, PASSWORD, new JCECrypto(null));
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://localhost").build();
      request = filter.filter(request);
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.AUTHORIZATION), "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
   }

}
