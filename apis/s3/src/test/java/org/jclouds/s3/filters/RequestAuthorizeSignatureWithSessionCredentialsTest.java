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
package org.jclouds.s3.filters;

import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import org.jclouds.ContextBuilder;
import org.jclouds.aws.domain.SessionCredentials;
import org.jclouds.date.TimeStamp;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseRestApiTest.MockModule;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.config.S3RestClientModule;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests behavior of {@code RequestAuthorizeSignature}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "RequestAuthorizeSignatureWithSessionCredentialsTest")
public class RequestAuthorizeSignatureWithSessionCredentialsTest {
   public static Injector injector(Credentials creds) {
      return ContextBuilder.newBuilder("s3")
            .credentialsSupplier(Suppliers.<Credentials> ofInstance(creds))
            .modules(ImmutableList.<Module> of(new MockModule(), new NullLoggingModule(), new TestS3RestClientModule())).buildInjector();
   }

   @ConfiguresRestClient
   private static final class TestS3RestClientModule extends S3RestClientModule<S3Client, S3AsyncClient> {

      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return "2009-11-08T15:54:08.897Z";
      }
   }
   
   public static RequestAuthorizeSignature filter(Credentials creds) {
      return injector(creds).getInstance(RequestAuthorizeSignature.class);
   }

   SessionCredentials temporaryCredentials =  SessionCredentials.builder()
      .accessKeyId("AKIAIOSFODNN7EXAMPLE")
      .secretAccessKey("wJalrXUtnFEMI/K7MDENG/bPxRfiCYzEXAMPLEKEY")
      .sessionToken("AQoEXAMPLEH4aoAH0gNCAPyJxz4BlCFFxWNE1OPTgk5TthT")
      .expiration(new SimpleDateFormatDateService().iso8601DateParse("2011-07-11T19:55:29.611Z")).build();

   Invocation invocation = Invocation.create(method(S3AsyncClient.class, "bucketExists", String.class),
                                             ImmutableList.<Object> of("foo"));

   HttpRequest bucketFooExists = GeneratedHttpRequest.builder().method("GET")
                                            .invocation(invocation)
                                            .endpoint("https://foo.s3.amazonaws.com/?max-keys=0")
                                            .addHeader("Host", "foo.s3.amazonaws.com").build();

   @Test
   void testAddsSecurityToken() {
      HttpRequest filtered = filter(temporaryCredentials).filter(bucketFooExists);
      assertEquals(filtered.getFirstHeaderOrNull("Authorization"),
            "AWS AKIAIOSFODNN7EXAMPLE:0fUhWTaRBcIvIAndg2C+5eLfE24=");
      assertEquals(filtered.getFirstHeaderOrNull("x-amz-security-token"), temporaryCredentials.getSessionToken());
   }
}
