/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.atmosonline.saas.filters;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.atmosonline.saas.config.AtmosStorageRestClientModule;
import org.jclouds.atmosonline.saas.reference.AtmosStorageHeaders;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.BaseRestClientTest.MockModule;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestContextFactory;
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
      assertEquals(request.getFirstHeaderOrNull(AtmosStorageHeaders.SIGNATURE), EXPECTED_SIGNATURE);
   }

   @Test
   void testFilterReplacesOldValues() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
      HttpRequest request = newRequest(inputHeaders().put(AtmosStorageHeaders.SIGNATURE, "foo")
            .put(HttpHeaders.DATE, "foo").put(AtmosStorageHeaders.DATE, "foo").put(AtmosStorageHeaders.UID, "foo")
            .build());
      request = filter.filter(request);
      assertEquals(request.getFirstHeaderOrNull(AtmosStorageHeaders.SIGNATURE), EXPECTED_SIGNATURE);
   }

   @BeforeClass
   protected void createFilter() {
      Injector injector = new RestContextFactory()
            .createContextBuilder(
                  "atmosonline",
                  UID,
                  KEY,
                  ImmutableSet.<Module> of(new MockModule(), new TestAtmosStorageRestClientModule(),
                        new NullLoggingModule()), new Properties()).buildInjector();

      filter = injector.getInstance(SignRequest.class);

   }

   @RequiresHttp
   @ConfiguresRestClient
   private static final class TestAtmosStorageRestClientModule extends AtmosStorageRestClientModule {

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
      HttpRequest request = new HttpRequest("POST", URI.create("http://localhost/rest/objects"), headers);
      request.setPayload("");
      request.getPayload().getContentMetadata().setContentLength(4286l);
      request.getPayload().getContentMetadata().setContentType(MediaType.APPLICATION_OCTET_STREAM);
      return request;
   }

   protected Builder<String, String> preconstructedHeaders() {
      Builder<String, String> builder = inputHeaders();
      builder.put(HttpHeaders.DATE, DEFAULT_DATE);
      builder.put(AtmosStorageHeaders.UID, UID);
      return builder;
   }

   protected Builder<String, String> inputHeaders() {
      Builder<String, String> builder = ImmutableMultimap.builder();
      builder.put(AtmosStorageHeaders.LISTABLE_META, "part4/part7/part8=quick");
      builder.put(AtmosStorageHeaders.META, "part1=buy");
      builder.put(HttpHeaders.ACCEPT, "*/*");
      builder.put(AtmosStorageHeaders.USER_ACL, "john=FULL_CONTROL,mary=WRITE");
      builder.put(AtmosStorageHeaders.GROUP_ACL, "other=NONE");
      builder.put(AtmosStorageHeaders.DATE, DEFAULT_DATE);
      builder.put(HttpHeaders.HOST, "10.5.115.118");
      return builder;
   }
}