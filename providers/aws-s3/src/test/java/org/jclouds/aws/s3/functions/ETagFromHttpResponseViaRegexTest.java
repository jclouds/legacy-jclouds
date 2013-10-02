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
 * Tests behavior of {@code ETagFromHttpResponseViaRegex}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ETagFromHttpResponseViaRegexTest")
public class ETagFromHttpResponseViaRegexTest {

   @Test
   public void test() {

      HttpResponse response = HttpResponse.builder().statusCode(200).payload(
               Payloads.newInputStreamPayload(getClass().getResourceAsStream("/complete-multipart-upload.xml")))
               .build();
      ETagFromHttpResponseViaRegex parser = new ETagFromHttpResponseViaRegex(new ReturnStringIf2xx());

      assertEquals(parser.apply(response), "\"3858f62230ac3c915f300c664312c11f-9\"");
   }

}
