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

package org.jclouds.aws.util;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.BaseRestClientTest.MockModule;
import org.jclouds.rest.RestContextFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * Tests parsing of S3 responses
 * 
 * @author Adrian Cole
 */
@Test(sequential = true, groups = { "unit" })
public class AWSUtilsTest {
   AWSUtils utils = null;
   private HttpCommand command;

   @BeforeTest
   protected void setUpInjector() throws IOException {

      Injector injector = new RestContextFactory().createContextBuilder("ec2", "foo", "bar",
            ImmutableSet.of(new MockModule(), new NullLoggingModule()), new Properties()).buildInjector();

      utils = injector.getInstance(AWSUtils.class);

      command = createMock(HttpCommand.class);
      expect(command.getCurrentRequest()).andReturn(createMock(HttpRequest.class)).atLeastOnce();
      replay(command);
   }

   @AfterTest
   protected void tearDownInjector() {
      utils = null;
   }

   HttpResponse response(InputStream content) {
      HttpResponse response = new HttpResponse(400, "boa", Payloads.newInputStreamPayload(content),
            ImmutableMultimap.<String, String> of("x-amz-request-id", "requestid", "x-amz-id-2", "requesttoken"));
      response.getPayload().getContentMetadata().setContentType("text/xml");
      return response;
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
