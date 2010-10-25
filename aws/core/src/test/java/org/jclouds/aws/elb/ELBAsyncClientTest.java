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

package org.jclouds.aws.elb;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.aws.elb.config.ELBRestClientModule;
import org.jclouds.aws.elb.xml.RegisterInstancesWithLoadBalancerResponseHandler;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ELBAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "elb.ELBAsyncClientTest")
public class ELBAsyncClientTest extends RestClientTest<ELBAsyncClient> {

   public void testRegisterInstancesWithLoadBalancer() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = ELBAsyncClient.class.getMethod("registerInstancesWithLoadBalancerInRegion",
               String.class, String.class, String[].class);

      HttpRequest request = processor.createRequest(method, null, "ReferenceAP1", "i-6055fa09");

      assertRequestLineEquals(request,
               "POST https://elasticloadbalancing.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: elasticloadbalancing.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               request,
               "Version=2009-11-25&Action=RegisterInstancesWithLoadBalancer&LoadBalancerName=ReferenceAP1&Instances.member.1.InstanceId=i-6055fa09",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method,
               RegisterInstancesWithLoadBalancerResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ELBAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ELBAsyncClient>>() {
      };
   }

   @RequiresHttp
   @ConfiguresRestClient
   private static final class TestELBRestClientModule extends ELBRestClientModule {
      @Override
      protected void configure() {
         super.configure();
      }

      @Override
      protected String provideTimeStamp(final DateService dateService,
               @Named(Constants.PROPERTY_SESSION_INTERVAL) int expiration) {
         return "2009-11-08T15:54:08.897Z";
      }
   }

   @Override
   protected Module createModule() {
      return new TestELBRestClientModule();
   }

   @Override
   public RestContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("elb", "identity", "credential",
               new Properties());
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), FormSigner.class);
   }

}
