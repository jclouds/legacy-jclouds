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
package org.jclouds.aws.filters;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.testng.Assert.assertEquals;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.ContextBuilder;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.rest.RequestSigner;
import org.jclouds.rest.internal.BaseRestApiTest.MockModule;
import org.testng.annotations.Test;

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
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "FormSignerTest")
public class FormSignerTest {
   public static final Injector INJECTOR = ContextBuilder
         .newBuilder(
               AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(IntegrationTestClient.class, IntegrationTestAsyncClient.class,
                     "http://localhost"))
         .credentials("identity", "credential")
         .apiVersion("apiVersion")
         .modules(ImmutableList.<Module> of(new MockModule(), new NullLoggingModule(),
                     new AbstractModule() {
                        @Override
                        protected void configure() {
                           bind(RequestSigner.class).to(FormSigner.class);
                           bind(String.class).annotatedWith(Names.named(PROPERTY_HEADER_TAG)).toInstance("amz");
                           bind(String.class).annotatedWith(TimeStamp.class).toInstance("2009-11-08T15:54:08.897Z");
                        }

                     })).buildInjector();
   FormSigner filter = INJECTOR.getInstance(FormSigner.class);

   @Test
   void testBuildCanonicalizedStringSetsVersion() {

      assertEquals(
               filter.filter(
                        HttpRequest.builder()
                                   .method("GET")
                                   .endpoint("http://localhost")
                                   .addHeader(HttpHeaders.HOST, "localhost")
                                   .payload("Action=DescribeImages&ImageId.1=ami-2bb65342").build())
                        .getPayload().getRawContent(),
               "Action=DescribeImages&ImageId.1=ami-2bb65342&Signature=ugnt4m2eHE7Ka%2FvXTr9EhKZq7bhxOfvW0y4pAEqF97w%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Version=apiVersion&AWSAccessKeyId=identity");
   }

   @Test
   void testBuildCanonicalizedString() {
      assertEquals(
               filter.buildCanonicalizedString(new ImmutableMultimap.Builder<String, String>().put("AWSAccessKeyId",
                        "foo").put("Action", "DescribeImages").put("Expires", "2008-02-10T12:00:00Z").put("ImageId.1",
                        "ami-2bb65342").put("SignatureMethod", "HmacSHA256").put("SignatureVersion", "2").put(
                        "Version", "2010-06-15").build()),
               "AWSAccessKeyId=foo&Action=DescribeImages&Expires=2008-02-10T12%3A00%3A00Z&ImageId.1=ami-2bb65342&SignatureMethod=HmacSHA256&SignatureVersion=2&Version=2010-06-15");
   }

}
