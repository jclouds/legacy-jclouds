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
package org.jclouds.ec2.binders;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import org.jclouds.ec2.services.BaseEC2AsyncClientTest;
import org.jclouds.ec2.services.InstanceAsyncClient;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code BindS3UploadPolicyAndSignature}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindS3UploadPolicyAndSignatureTest")
public class BindS3UploadPolicyAndSignatureTest extends BaseEC2AsyncClientTest<InstanceAsyncClient> {
   private BindS3UploadPolicyAndSignature binder;

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      binder = injector.getInstance(BindS3UploadPolicyAndSignature.class);
   }

   public void testMapping() {
      String json = "{\"foo\":true}";

      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      request = binder.bindToRequest(request, json);
      assertEquals(
            request.getPayload().getRawContent(),
            "Storage.S3.UploadPolicy=eyJmb28iOnRydWV9&Storage.S3.UploadPolicySignature=UbDQLDM5P3aZ840aqJoH%2B6rwDcRo5KrIfsG7vJWHIVY%3D");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIsBad() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://momma")).build();
      binder.bindToRequest(request, null);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<InstanceAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<InstanceAsyncClient>>() {
      };
   }
}
