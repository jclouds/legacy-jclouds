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

package org.jclouds.aws.cloudwatch;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Properties;

import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.aws.cloudwatch.config.CloudWatchRestClientModule;
import org.jclouds.aws.cloudwatch.xml.GetMetricStatisticsResponseHandler;
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
 * Tests behavior of {@code CloudWatchAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudwatch.MonitoringAsyncClientTest")
public class CloudWatchAsyncClientTest extends RestClientTest<CloudWatchAsyncClient> {

   public void testRegisterInstancesWithMeasure() throws SecurityException, NoSuchMethodException, IOException {
      Date date = new Date(10000000l);
      Method method = CloudWatchAsyncClient.class.getMethod("getMetricStatisticsInRegion", String.class, String.class,
            Date.class, Date.class, int.class, String.class);
      HttpRequest request = processor.createRequest(method, (String) null, "CPUUtilization", date, date, 60, "Average");

      assertRequestLineEquals(request, "POST https://monitoring.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: monitoring.us-east-1.amazonaws.com\n");
      try {
         assertPayloadEquals(
               request,
               "Version=2009-05-15&Action=GetMetricStatistics&Statistics.member.1=Average&StartTime=1970-01-01T02%3A46%3A40Z&MeasureName=CPUUtilization&EndTime=1970-01-01T02%3A46%3A40Z&Period=60",
               "application/x-www-form-urlencoded", false);
      } catch (AssertionError e) {
         // mvn 3.0 osx 10.6.5 somehow sorts differently
         assertPayloadEquals(
               request,
               "Version=2009-05-15&Action=GetMetricStatistics&Statistics.member.1=Average&StartTime=1970-01-01T02%3A46%3A40Z&MeasureName=CPUUtilization&Period=60&EndTime=1970-01-01T02%3A46%3A40Z",
               "application/x-www-form-urlencoded", false);
      }
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, GetMetricStatisticsResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<CloudWatchAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<CloudWatchAsyncClient>>() {
      };
   }

   @RequiresHttp
   @ConfiguresRestClient
   private static final class TestMonitoringRestClientModule extends CloudWatchRestClientModule {
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
      return new TestMonitoringRestClientModule();
   }

   @Override
   public RestContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("cloudwatch", "identity", "credential", new Properties());
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), FormSigner.class);
   }

}
