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

package org.jclouds.aws.sqs.config;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.handlers.AWSClientErrorRetryHandler;
import org.jclouds.aws.handlers.AWSRedirectionRetryHandler;
import org.jclouds.aws.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.BaseRestClientTest.MockModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class SQSRestClientModuleTest {

   Injector createInjector() {
      return new RestContextFactory().createContextBuilder("sqs", "uid", "key",
               ImmutableSet.<Module> of(new MockModule(), new NullLoggingModule())).buildInjector();
   }

   @Test
   void testServerErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getServerErrorHandler().getClass(), ParseAWSErrorFromXmlContent.class);
   }

   @Test
   void testRegions() {
      Map<String, URI> regionMap = createInjector().getInstance(
               new Key<Map<String, URI>>(org.jclouds.location.Region.class) {
               });
      assertEquals(regionMap, ImmutableMap.<String, URI> of(Region.US_EAST_1, URI
               .create("https://sqs.us-east-1.amazonaws.com"), Region.US_WEST_1, URI
               .create("https://sqs.us-west-1.amazonaws.com"), Region.EU_WEST_1, URI
               .create("https://sqs.eu-west-1.amazonaws.com"), Region.AP_SOUTHEAST_1, URI
               .create("https://sqs.ap-southeast-1.amazonaws.com")));
   }

   @Test
   void testClientErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getClientErrorHandler().getClass(), ParseAWSErrorFromXmlContent.class);
   }

   @Test
   void testClientRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getClientErrorRetryHandler().getClass(),
               AWSClientErrorRetryHandler.class);
   }

   @Test
   void testRedirectionRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getRedirectionRetryHandler().getClass(),
               AWSRedirectionRetryHandler.class);
   }

}