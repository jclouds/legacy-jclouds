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

package org.jclouds.aws.s3;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.functions.ReturnFalseIfBucketAlreadyOwnedByYouOrIllegalState;
import org.jclouds.s3.options.PutBucketOptions;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AWSS3AsyncClientTest")
public class AWSS3AsyncClientTest extends org.jclouds.s3.S3AsyncClientTest {

   public AWSS3AsyncClientTest() {
      this.provider = "aws-s3";
   }

   public void testPutBucketEu() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("putBucketInRegion", String.class, String.class, Array.newInstance(
               PutBucketOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, "EU", "bucket");

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request,
               "<CreateBucketConfiguration><LocationConstraint>EU</LocationConstraint></CreateBucketConfiguration>",
               "text/xml", false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseIfBucketAlreadyOwnedByYouOrIllegalState.class);

      checkFilters(request);
   }
}
