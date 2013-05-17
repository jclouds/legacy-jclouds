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
package org.jclouds.aws.util;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.IOException;
import java.io.InputStream;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.filters.FormSignerTest;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Tests parsing of S3 responses
 * 
 * @author Adrian Cole
 */
@Test(singleThreaded = true, groups = "unit", testName = "AWSUtilsTest")
public class AWSUtilsTest {
   AWSUtils utils = null;
   private HttpCommand command;

   @BeforeTest
   protected void setUpInjector() throws IOException {

      utils = FormSignerTest.injector(new Credentials("identity", "credential")).getInstance(AWSUtils.class);

      command = createMock(HttpCommand.class);
      expect(command.getCurrentRequest()).andReturn(createMock(HttpRequest.class)).atLeastOnce();
      replay(command);
   }

   @AfterTest
   protected void tearDownInjector() {
      utils = null;
   }

   HttpResponse response(InputStream content) {
      HttpResponse response = HttpResponse.builder().statusCode(BAD_REQUEST.getStatusCode())
                                          .message("boa")
                                          .payload(content)
                                          .addHeader("x-amz-request-id", "requestid")
                                          .addHeader("x-amz-id-2", "requesttoken").build();
      response.getPayload().getContentMetadata().setContentType("text/xml");
      return response;
   }
   
   /**
    * HEAD requests don't have a payload
    */
   @Test
   public void testNoExceptionWhenNoPayload() {
      HttpResponse response = HttpResponse.builder().statusCode(BAD_REQUEST.getStatusCode()).build();
      assertNull(utils.parseAWSErrorFromContent(command.getCurrentRequest(), response));
   }
   
   /**
    * clones or proxies can mess up the error message.
    */
   @Test
   public void testNoExceptionParsingTextPlain() {
      HttpResponse response = HttpResponse.builder().statusCode(BAD_REQUEST.getStatusCode()).payload("foo bar").build();
      response.getPayload().getContentMetadata().setContentType(TEXT_PLAIN);
      assertNull(utils.parseAWSErrorFromContent(command.getCurrentRequest(), response));
   }

   @Test
   public void testParseAWSErrorFromContentHttpCommandHttpResponseInputStream() {
      AWSError error = utils.parseAWSErrorFromContent(command.getCurrentRequest(), response(getClass()
            .getResourceAsStream("/error.xml")));
      assertEquals(error.getCode(), "NoSuchKey");
      assertEquals(error.getMessage(), "The resource you requested does not exist");
      assertEquals(error.getRequestToken(), "requesttoken");
      assertEquals(error.getRequestId(), "4442587FB7D0A2F9");
      assertEquals(error.getDetails().get("Resource"), "/mybucket/myfoto.jpg");
   }

   @Test
   public void testValidateBucketName() {
      // TODO
   }
}
