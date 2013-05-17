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
package org.jclouds.aws.s3.functions;

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code UploadIdFromHttpResponseViaRegex}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "UploadIdFromHttpResponseViaRegexTest")
public class UploadIdFromHttpResponseViaRegexTest {

   @Test
   public void test() {

      HttpResponse response = HttpResponse.builder().statusCode(200).payload(
               Payloads.newInputStreamPayload(getClass().getResourceAsStream("/initiate-multipart-upload.xml")))
               .build();
      UploadIdFromHttpResponseViaRegex parser = new UploadIdFromHttpResponseViaRegex(new ReturnStringIf2xx());

      assertEquals(parser.apply(response), "VXBsb2FkIElEIGZvciA2aWWpbmcncyBteS1tb3ZpZS5tMnRzIHVwbG9hZA");
   }

}
