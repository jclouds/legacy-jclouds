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
package org.jclouds.aws.filters;

import static javax.ws.rs.HttpMethod.GET;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.testng.Assert.assertEquals;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.ContextBuilder;
import org.jclouds.aws.xml.SessionCredentialsHandlerTest;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.rest.RequestSigner;
import org.jclouds.rest.internal.BaseRestApiTest.MockModule;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;
/**
 * Tests behavior of {@code FormSigner}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", singleThreaded = true, testName = "FormSignerTest")
public class FormSignerTest {
   public static Injector injector(Credentials creds) {
      return ContextBuilder
            .newBuilder(
                  AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(IntegrationTestClient.class,
                        IntegrationTestAsyncClient.class, "http://localhost"))
            .credentialsSupplier(Suppliers.<Credentials> ofInstance(creds)).apiVersion("apiVersion")
            .modules(ImmutableList.<Module> of(new MockModule(), new NullLoggingModule(), new AbstractModule() {
               @Override
               protected void configure() {
                  bind(RequestSigner.class).to(FormSigner.class);
                  bind(String.class).annotatedWith(Names.named(PROPERTY_HEADER_TAG)).toInstance("amz");
                  bind(String.class).annotatedWith(TimeStamp.class).toInstance("2009-11-08T15:54:08.897Z");
               }

            })).buildInjector();
   }

   public static FormSigner filter(Credentials creds) {
      return injector(creds).getInstance(FormSigner.class);
   }

   public static FormSigner staticCredentialsFilter = filter(new Credentials("identity", "credential"));

   HttpRequest request = HttpRequest.builder().method(GET)
         .endpoint("http://localhost")
         .addHeader(HttpHeaders.HOST, "localhost")
         .addFormParam("Action", "DescribeImages")
         .addFormParam("ImageId.1", "ami-2bb65342").build();

   @Test
   void testAddsSecurityToken() {
      HttpRequest filtered = filter(new SessionCredentialsHandlerTest().expected()).filter(request);
      assertEquals(
            filtered.getPayload().getRawContent(),
            "Action=DescribeImages&ImageId.1=ami-2bb65342&SecurityToken=AQoEXAMPLEH4aoAH0gNCAPyJxz4BlCFFxWNE1OPTgk5TthT&Signature=/8ReFVH1tvyNORsJb%2BSBieT9zvdqREQQr/olwmxC7VY%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Version=apiVersion&AWSAccessKeyId=AKIAIOSFODNN7EXAMPLE");
   }

   @Test
   void testBuildCanonicalizedStringSetsVersion() {
      HttpRequest filtered = staticCredentialsFilter.filter(request);
      assertEquals(filtered.getPayload().getRawContent(),
            "Action=DescribeImages&ImageId.1=ami-2bb65342&Signature=ugnt4m2eHE7Ka/vXTr9EhKZq7bhxOfvW0y4pAEqF97w%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Version=apiVersion&AWSAccessKeyId=identity");
   }

   @Test
   void testBuildCanonicalizedString() {
      assertEquals(
            staticCredentialsFilter.buildCanonicalizedString(new ImmutableMultimap.Builder<String, String>()
                  .put("AWSAccessKeyId", "foo").put("Action", "DescribeImages").put("Expires", "2008-02-10T12:00:00Z")
                  .put("ImageId.1", "ami-2bb65342").put("SignatureMethod", "HmacSHA256").put("SignatureVersion", "2")
                  .put("Version", "2010-06-15").build()),
            "AWSAccessKeyId=foo&Action=DescribeImages&Expires=2008-02-10T12%3A00%3A00Z&ImageId.1=ami-2bb65342&SignatureMethod=HmacSHA256&SignatureVersion=2&Version=2010-06-15");
   }

}
