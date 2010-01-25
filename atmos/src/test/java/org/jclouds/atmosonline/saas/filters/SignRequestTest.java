/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.util.concurrent.Executors.sameThreadExecutor;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.Constants;
import org.jclouds.atmosonline.saas.reference.AtmosStorageConstants;
import org.jclouds.atmosonline.saas.reference.AtmosStorageHeaders;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.util.Jsr330;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

@Test(groups = "unit", testName = "emcsaas.SignRequestTest")
public class SignRequestTest {

   private static final String KEY = "LJLuryj6zs8ste6Y3jTGQp71xq0=";

   private Injector injector;
   private SignRequest filter;

   @Test
   void testCreateStringToSign() throws IOException {
      String expects = Utils.toStringAndClose(getClass().getResourceAsStream("/hashstring.txt"));
      HttpRequest request = newRequest();
      String toSign = filter.replaceDateHeader(request).createStringToSign(request);
      assertEquals(toSign, expects);
   }

   @Test
   void testUid() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
      HttpRequest request = newRequest();
      filter.replaceUIDHeader(request);
      assertEquals(request.getFirstHeaderOrNull(AtmosStorageHeaders.UID), "user");
   }

   @Test
   void testSignString() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
      String expects = "WHJo1MFevMnK4jCthJ974L3YHoo=";

      HttpRequest request = newRequest();
      String toSign = filter.replaceDateHeader(request).createStringToSign(request);
      String signature = filter.signString(toSign);
      assertEquals(signature, expects);
   }

   @BeforeClass
   protected void createFilter() {
      injector = Guice.createInjector(new ExecutorServiceModule(sameThreadExecutor(),
               sameThreadExecutor()), new AbstractModule() {

         protected void configure() {
            bindConstant().annotatedWith(Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_UID))
                     .to("user");
            bindConstant().annotatedWith(Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_KEY))
                     .to(KEY);
            bindConstant().annotatedWith(Jsr330.named(Constants.PROPERTY_IO_WORKER_THREADS))
                     .to("1");
            bindConstant().annotatedWith(Jsr330.named(Constants.PROPERTY_USER_THREADS)).to("1");
         }

         @SuppressWarnings("unused")
         @Provides
         @TimeStamp
         String getDate() {
            return "Thu, 05 Jun 2008 16:38:19 GMT";
         }
      });
      filter = injector.getInstance(SignRequest.class);

   }

   public HttpRequest newRequest() {
      HttpRequest request = new HttpRequest("POST", URI.create("http://localhost/rest/objects"));
      request.getHeaders().put(AtmosStorageHeaders.LISTABLE_META, "part4/part7/part8=quick");
      request.getHeaders().put(AtmosStorageHeaders.META, "part1=buy");
      request.getHeaders().put(HttpHeaders.ACCEPT, "*/*");
      request.getHeaders().put(AtmosStorageHeaders.USER_ACL, "john=FULL_CONTROL,mary=WRITE");
      request.getHeaders().put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM);
      request.getHeaders().put(AtmosStorageHeaders.DATE, "Thu, 05 Jun 2008 16:38:19 GMT");
      request.getHeaders().put(AtmosStorageHeaders.GROUP_ACL, "other=NONE");
      request.getHeaders().put(HttpHeaders.HOST, "10.5.115.118");
      request.getHeaders().put(HttpHeaders.CONTENT_LENGTH, "4286");
      request.getHeaders().put(AtmosStorageHeaders.UID, "6039ac182f194e15b9261d73ce044939/user1");
      return request;
   }
}