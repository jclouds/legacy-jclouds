/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.http.filters;

import static com.google.common.base.Suppliers.ofInstance;
import static org.jclouds.http.filters.BasicAuthentication.basic;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link AbiquoAuthentication} filter.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "AbiquoAuthenticationTest")
public class AbiquoAuthenticationTest {

   public void testBasicAuthentication() throws NoSuchAlgorithmException, CertificateException {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).build();

      AbiquoAuthentication filter = new AbiquoAuthentication(ofInstance(new Credentials("identity", "credential")), false);
      HttpRequest filtered = filter.filter(request);
      HttpRequest expected = request.toBuilder()
            .replaceHeader(HttpHeaders.AUTHORIZATION, basic("identity", "credential")).build();

      assertFalse(filtered.getHeaders().containsKey(HttpHeaders.COOKIE));
      assertEquals(filtered, expected);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testBasicAuthenticationWithoutIdentity() throws NoSuchAlgorithmException, CertificateException {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).build();

      AbiquoAuthentication filter = new AbiquoAuthentication(ofInstance(new Credentials(null, "credential")), false);
      filter.filter(request);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testBasicAuthenticationWithoutCredential() throws NoSuchAlgorithmException, CertificateException {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).build();

      AbiquoAuthentication filter = new AbiquoAuthentication(ofInstance(new Credentials("identity", null)), false);
      filter.filter(request);
   }

   public void testTokenAuthentication() throws NoSuchAlgorithmException, CertificateException {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).build();

      AbiquoAuthentication filter = new AbiquoAuthentication(ofInstance(new Credentials("token-identity", "token")),
            true);
      HttpRequest filtered = filter.filter(request);
      HttpRequest expected = request.toBuilder()
            .replaceHeader(HttpHeaders.COOKIE, AbiquoAuthentication.tokenAuth("token")).build();

      assertFalse(filtered.getHeaders().containsKey(HttpHeaders.AUTHORIZATION));
      assertEquals(filtered, expected);
   }
}
