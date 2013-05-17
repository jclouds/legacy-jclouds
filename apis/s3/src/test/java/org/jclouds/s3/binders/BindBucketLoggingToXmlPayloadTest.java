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
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.s3.domain.AccessControlList.Grant;
import org.jclouds.s3.domain.AccessControlList.Permission;
import org.jclouds.s3.domain.BucketLogging;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code BindBucketLoggingToXmlPayload}
 * 
 * @author Adrian Cole
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindBucketLoggingToXmlPayloadTest")
public class BindBucketLoggingToXmlPayloadTest  extends BaseHandlerTest {

   public void testApplyInputStream() throws IOException {
      
      BucketLogging bucketLogging = new BucketLogging("mylogs", "access_log-", ImmutableSet
               .<Grant> of(new Grant(new EmailAddressGrantee("adrian@jclouds.org"),
                        Permission.FULL_CONTROL)));
     
      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream(
               "/bucket_logging.xml"));
      
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://test").build();
      BindBucketLoggingToXmlPayload binder = injector
               .getInstance(BindBucketLoggingToXmlPayload.class);

      binder.bindToRequest(request, bucketLogging);
      assertEquals(request.getPayload().getContentMetadata().getContentType(), "text/xml");
      assertEquals(request.getPayload().getRawContent(), expected);

   }
}
