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
package org.jclouds.atmos.filters;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.ContextBuilder;
import org.jclouds.atmos.config.AtmosRestClientModule;
import org.jclouds.atmos.reference.AtmosHeaders;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseRestApiTest.MockModule;
import org.jclouds.util.Strings2;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class SignRequestTest {

   private static final String EXPECTED_SIGNATURE = "WHJo1MFevMnK4jCthJ974L3YHoo=";
   private static final String UID = "6039ac182f194e15b9261d73ce044939/user1";
   private static final String DEFAULT_DATE = "Thu, 05 Jun 2008 16:38:19 GMT";
   private static final String KEY = "LJLuryj6zs8ste6Y3jTGQp71xq0=";

   private SignRequest filter;

   @Test
   void testCreateStringToSign() throws IOException {
      String expects = Strings2.toStringAndClose(getClass().getResourceAsStream("/hashstring.txt"));
      HttpRequest request = newRequest(preconstructedHeaders().build());
      String toSign = filter.createStringToSign(request);
      assertEquals(toSign, expects);
   }

   @Test
   void testSignString() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
      HttpRequest request = newRequest(preconstructedHeaders().build());
      String toSign = filter.createStringToSign(request);
      String signature = filter.signString(toSign);
      assertEquals(signature, EXPECTED_SIGNATURE);
   }

   @Test
   void testFilter() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
      HttpRequest request = newRequest(inputHeaders().build());
      request = filter.filter(request);
      assertEquals(request.getFirstHeaderOrNull(AtmosHeaders.SIGNATURE), EXPECTED_SIGNATURE);
   }

   @Test
   void testFilterReplacesOldValues() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
      HttpRequest request = newRequest(inputHeaders().put(AtmosHeaders.SIGNATURE, "foo")
            .put(HttpHeaders.DATE, "foo").put(AtmosHeaders.DATE, "foo").put(AtmosHeaders.UID, "foo")
            .build());
      request = filter.filter(request);
      assertEquals(request.getFirstHeaderOrNull(AtmosHeaders.SIGNATURE), EXPECTED_SIGNATURE);
   }

   @BeforeClass
   protected void createFilter() {
      Injector injector = ContextBuilder
            .newBuilder("atmos")
            .credentials(UID, KEY)
            .modules(
                  ImmutableSet.<Module> of(new MockModule(), new TestAtmosRestClientModule(), new NullLoggingModule()))
            .buildInjector();

      filter = injector.getInstance(SignRequest.class);

   }

      @ConfiguresRestClient
   private static final class TestAtmosRestClientModule extends AtmosRestClientModule {

      @Override
      protected void configure() {
         super.configure();
      }

      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return DEFAULT_DATE;
      }
   }

   public HttpRequest newRequest(Multimap<String, String> headers) {
      HttpRequest request = HttpRequest.builder()
                                       .method("POST")
                                       .endpoint("http://localhost/rest/objects")
                                       .headers(headers).build();
      request.setPayload("");
      request.getPayload().getContentMetadata().setContentLength(4286l);
      request.getPayload().getContentMetadata().setContentType(MediaType.APPLICATION_OCTET_STREAM);
      return request;
   }

   protected Builder<String, String> preconstructedHeaders() {
      Builder<String, String> builder = inputHeaders();
      builder.put(HttpHeaders.DATE, DEFAULT_DATE);
      builder.put(AtmosHeaders.UID, UID);
      return builder;
   }

   protected Builder<String, String> inputHeaders() {
      Builder<String, String> builder = ImmutableMultimap.builder();
      builder.put(AtmosHeaders.LISTABLE_META, "part4/part7/part8=quick");
      builder.put(AtmosHeaders.META, "part1=buy");
      builder.put(HttpHeaders.ACCEPT, "*/*");
      builder.put(AtmosHeaders.USER_ACL, "john=FULL_CONTROL,mary=WRITE");
      builder.put(AtmosHeaders.GROUP_ACL, "other=NONE");
      builder.put(AtmosHeaders.DATE, DEFAULT_DATE);
      builder.put(HttpHeaders.HOST, "10.5.115.118");
      return builder;
   }
}
