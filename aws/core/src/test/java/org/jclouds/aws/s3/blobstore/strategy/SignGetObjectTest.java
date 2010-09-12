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

package org.jclouds.aws.s3.blobstore.strategy;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.aws.s3.BaseS3AsyncClientTest;
import org.jclouds.aws.s3.config.S3RestClientModule;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Module;

/**
 * Tests behavior of {@code SignGetObject}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.SignGetObjectTest")
public class SignGetObjectTest extends BaseS3AsyncClientTest {

   public void testSignGetObject() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = new SignGetObject(processor).apply( "bucket", "object");

      assertRequestLineEquals(request, "GET https://bucket.s3.amazonaws.com/object HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Authorization: AWS identity:1UqxCBECNncBHUhxJ3Y/Q1O3IiA=\nDate: 2009-11-08T15:54:08.897Z\nHost: bucket.s3.amazonaws.com\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }


   @RequiresHttp
   @ConfiguresRestClient
   protected static final class TestS3RestClientModule extends S3RestClientModule {
      @Override
      protected void configure() {
         super.configure();
      }

      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return "2009-11-08T15:54:08.897Z";
      }
   }

   @Override
   protected Module createModule() {
      return new TestS3RestClientModule();
   }

}