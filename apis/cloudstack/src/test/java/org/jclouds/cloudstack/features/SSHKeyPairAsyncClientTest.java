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
package org.jclouds.cloudstack.features;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLEncoder;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.ListSSHKeyPairsOptions;
import org.jclouds.crypto.SshKeys;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.testng.annotations.Test;

import com.google.common.base.Functions;

/**
 * Tests behavior of {@code SSHKeyPairAsyncClient}
 *
 * @author Vijay Kiran
 */
@Test(groups = "unit", testName = "SSHKeyPairAsyncClientTest")
public class SSHKeyPairAsyncClientTest extends BaseCloudStackAsyncClientTest<SSHKeyPairAsyncClient> {

   public void testListSSHKeyPairs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SSHKeyPairAsyncClient.class.getMethod("listSSHKeyPairs", ListSSHKeyPairsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listSSHKeyPairs&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListSSHKeyPairsOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SSHKeyPairAsyncClient.class.getMethod("listSSHKeyPairs", ListSSHKeyPairsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, ListSSHKeyPairsOptions.Builder.name("jclouds"));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listSSHKeyPairs&listAll=true&name=jclouds HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetSSHKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SSHKeyPairAsyncClient.class.getMethod("getSSHKeyPair", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "jclouds-keypair");

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listSSHKeyPairs&listAll=true&name=jclouds-keypair HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest,
            Functions.compose(IdentityFunction.INSTANCE, IdentityFunction.INSTANCE).getClass());
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testRegisterSSHKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SSHKeyPairAsyncClient.class.getMethod("registerSSHKeyPair", String.class, String.class);
      String publicKey = URLEncoder.encode(SshKeys.generate().get("public"), "UTF-8");
      HttpRequest httpRequest = processor.createRequest(method, "jclouds-keypair", publicKey);
      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=registerSSHKeyPair&name=jclouds-keypair&publickey="
                  + publicKey
                  + " HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      assertEquals(httpRequest.getFilters().size(), 1);
      assertEquals(httpRequest.getFilters().get(0).getClass(), QuerySigner.class);
   }


   public void testDeleteSSHKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SSHKeyPairAsyncClient.class.getMethod("deleteSSHKeyPair", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "jclouds-keypair");

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=deleteSSHKeyPair&name=jclouds-keypair HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }
}
