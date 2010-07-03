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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.s3.reference.S3Headers;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestClientTest.MockModule;
import org.jclouds.util.Utils;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * Tests parsing of S3 responses
 * 
 * @author Adrian Cole
 */
@Test(sequential = true, groups = { "unit" }, testName = "s3.S3UtilsTest")
public class S3UtilsTest {
   S3Utils utils = null;
   private HttpResponse response;
   private HttpCommand command;

   @BeforeTest
   protected void setUpInjector() throws IOException {

      Injector injector = new RestContextFactory().createContextBuilder("s3", "foo", "bar",
               ImmutableSet.of(new MockModule(), new NullLoggingModule()), new Properties())
               .buildInjector();

      utils = injector.getInstance(S3Utils.class);
      response = new HttpResponse();
      response.setStatusCode(400);
      response.getHeaders().put(S3Headers.REQUEST_ID, "requestid");
      response.getHeaders().put(S3Headers.REQUEST_TOKEN, "requesttoken");
      command = createMock(HttpCommand.class);
      expect(command.getRequest()).andReturn(createMock(HttpRequest.class)).atLeastOnce();
      replay(command);
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
