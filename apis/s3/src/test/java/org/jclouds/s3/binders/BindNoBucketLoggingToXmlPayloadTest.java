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
package org.jclouds.s3.binders;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.http.HttpRequest;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.internal.BaseS3AsyncClientTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code BindNoBucketLoggingToXmlPayload}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindNoBucketLoggingToXmlPayloadTest")
public class BindNoBucketLoggingToXmlPayloadTest extends BaseS3AsyncClientTest<S3AsyncClient> {

   public void testApplyInputStream() throws IOException {

      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://test").build();
      BindNoBucketLoggingToXmlPayload binder = injector.getInstance(BindNoBucketLoggingToXmlPayload.class);

      request = binder.bindToRequest(request, "bucket");
      assertEquals(request.getPayload().getRawContent(),
               "<BucketLoggingStatus xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"/>");

   }
}
