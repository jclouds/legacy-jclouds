/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.s3.binders;

import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.internal.BaseS3AsyncClientTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code BindAsHostPrefixIfConfigured}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindAsHostPrefixIfConfiguredNoPathTest")
public class BindAsHostPrefixIfConfiguredNoPathTest extends BaseS3AsyncClientTest<S3AsyncClient> {

   public void testBucketWithHostnameStyle() throws IOException, SecurityException, NoSuchMethodException {

      Method method = S3AsyncClient.class.getMethod("deleteObject", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, "testbucket.example.com", "test.jpg");
      assertRequestLineEquals(request, "DELETE https://s3.amazonaws.com/testbucket.example.com/test.jpg HTTP/1.1");
   }


   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      properties.setProperty(PROPERTY_S3_VIRTUAL_HOST_BUCKETS, "false");
      return properties;
   }

}
