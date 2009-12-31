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
package org.jclouds.aws.s3.util;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.s3.config.S3RestClientModule;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.aws.s3.reference.S3Headers;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.util.Jsr330;
import org.jclouds.util.Utils;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests parsing of S3 responses
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" }, testName = "s3.S3UtilsTest")
public class S3UtilsTest {
   S3Utils utils = null;
   private HttpResponse response;
   private HttpCommand command;

   @BeforeTest
   protected void setUpInjector() {
      Injector injector = Guice.createInjector(new S3RestClientModule(), new ParserModule(),
               new AbstractModule() {

                  @Override
                  protected void configure() {
                     bind(ExecutorService.class).toInstance(Executors.newCachedThreadPool());
                     // bind(new TypeLiteral<ClearListStrategy<S3Object>>(){}).to
                     bindConstant().annotatedWith(
                              Jsr330.named(S3Constants.PROPERTY_AWS_ACCESSKEYID)).to("user");
                     bindConstant().annotatedWith(
                              Jsr330.named(S3Constants.PROPERTY_AWS_SECRETACCESSKEY)).to("key");
                     bindConstant().annotatedWith(
                              Jsr330.named(S3Constants.PROPERTY_S3_SESSIONINTERVAL)).to("2");
                     bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_S3_ENDPOINT))
                              .to("https://s3.amazonaws.com");
                  }

               });
      utils = injector.getInstance(S3Utils.class);
      response = new HttpResponse();
      response.setStatusCode(400);
      response.getHeaders().put(S3Headers.REQUEST_ID, "requestid");
      response.getHeaders().put(S3Headers.REQUEST_TOKEN, "requesttoken");
      command = new HttpCommand() {

         public int getRedirectCount() {
            return 0;
         }

         public int incrementRedirectCount() {
            return 0;
         }

         public boolean isReplayable() {
            return false;
         }

         public void redirect(String host, int port) {
         }

         public void redirectAsGet() {
         }

         public Exception getException() {
            return null;
         }

         public int getFailureCount() {
            return 0;
         }

         public HttpRequest getRequest() {
            return null;
         }

         public int incrementFailureCount() {
            return 0;
         }

         public void setException(Exception exception) {

         }

         @Override
         public void redirectPath(String newPath) {
         }
      };

   }

   @AfterTest
   protected void tearDownInjector() {
      utils = null;
   }

   @Test
   public void testParseAWSErrorFromContentHttpCommandHttpResponseInputStream() {
      InputStream content = getClass().getResourceAsStream("/error.xml");
      AWSError error = utils.parseAWSErrorFromContent(command, response, content);
      validate(error);
   }

   private void validate(AWSError error) {
      assertEquals(error.getCode(), "NoSuchKey");
      assertEquals(error.getMessage(), "The resource you requested does not exist");
      assertEquals(error.getRequestToken(), "requesttoken");
      assertEquals(error.getRequestId(), "4442587FB7D0A2F9");
      assertEquals(error.getDetails().get("Resource"), "/mybucket/myfoto.jpg");
   }

   @Test
   public void testParseAWSErrorFromContentHttpCommandHttpResponseString() throws HttpException,
            IOException {
      InputStream content = getClass().getResourceAsStream("/error.xml");
      AWSError error = utils.parseAWSErrorFromContent(command, response, Utils
               .toStringAndClose(content));
      validate(error);
   }

   @Test
   public void testValidateBucketName() {
      // TODO
   }
}
