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
package org.jclouds.http.filters;

import static com.google.common.base.Suppliers.ofInstance;
import static com.google.common.collect.Queues.newArrayDeque;
import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;

import java.util.Deque;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BasicAuthenticationTest {
   private static final Credentials credential1 = new Credentials("Aladdin", "open sesame");
   private static final Credentials credential2 = new Credentials("Little", "Mermaid");

   public void testAuth() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://localhost").build();
      request = new BasicAuthentication(ofInstance(credential1)).filter(request);
      assertEquals(request.getFirstHeaderOrNull(AUTHORIZATION), "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
      request = new BasicAuthentication(ofInstance(credential2)).filter(request);
      assertEquals(request.getFirstHeaderOrNull(AUTHORIZATION), "Basic TGl0dGxlOk1lcm1haWQ=");
   }

   public void testAuthWithRuntimePasswordChange() {
      Supplier<Credentials> credentialRotation = new Supplier<Credentials>() {
         Deque<Credentials> rotation = newArrayDeque(asList(credential1, credential2));

         public Credentials get() {
            return rotation.poll();
         }
      };
      BasicAuthentication filter = new BasicAuthentication(credentialRotation);
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://localhost").build();
      request = filter.filter(request);
      assertEquals(request.getFirstHeaderOrNull(AUTHORIZATION), "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
      request = filter.filter(request);
      assertEquals(request.getFirstHeaderOrNull(AUTHORIZATION), "Basic TGl0dGxlOk1lcm1haWQ=");
   }
}
