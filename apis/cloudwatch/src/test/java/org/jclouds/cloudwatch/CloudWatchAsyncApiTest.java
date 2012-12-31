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
package org.jclouds.cloudwatch;

import static com.google.common.collect.Maps.transformValues;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.cloudwatch.config.CloudWatchRestClientModule;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptions;
import org.jclouds.cloudwatch.xml.GetMetricStatisticsResponseHandler;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.location.config.LocationModule;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseAsyncApiTest;
import org.jclouds.util.Suppliers2;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;

/**
 * Tests behavior of {@code CloudWatchAsyncApi}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "CloudWatchAsyncApiTest")
@Deprecated
public class CloudWatchAsyncApiTest extends BaseAsyncApiTest<CloudWatchAsyncApi> {

   public void testRegisterInstancesWithMeasure() throws SecurityException, NoSuchMethodException, IOException {
      Date date = new Date(10000000l);
      Method method = CloudWatchAsyncApi.class.getMethod("getMetricStatisticsInRegion", String.class, String.class,
            String.class, Date.class, Date.class, int.class, Statistics.class, GetMetricStatisticsOptions[].class);
      HttpRequest request = processor.createRequest(method, (String) null, "CPUUtilization", "AWS/EC2", date, date, 60,
         Statistics.AVERAGE, GetMetricStatisticsOptions.Builder.instanceId("i-12312313"));

      assertRequestLineEquals(request, "POST https://monitoring.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: monitoring.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               request,
               "Action=GetMetricStatistics&Statistics.member.1=Average&Period=60&Namespace=AWS/EC2&MetricName=CPUUtilization&StartTime=1970-01-01T02%3A46%3A40Z&EndTime=1970-01-01T02%3A46%3A40Z&Dimensions.member.1.Name=InstanceId&Dimensions.member.1.Value=i-12312313",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, GetMetricStatisticsResponseHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }


   @ConfiguresRestClient
   private static final class TestMonitoringRestClientModule extends CloudWatchRestClientModule {

      @Override
      protected void installLocations() {
         install(new LocationModule());
         bind(RegionIdToURISupplier.class).toInstance(new RegionIdToURISupplier() {

            @Override
            public Map<String, Supplier<URI>> get() {
               return transformValues(ImmutableMap.<String, URI> of(Region.EU_WEST_1, URI
                        .create("https://ec2.eu-west-1.amazonaws.com"), Region.US_EAST_1, URI
                        .create("https://ec2.us-east-1.amazonaws.com"), Region.US_WEST_1, URI
                        .create("https://ec2.us-west-1.amazonaws.com")), Suppliers2.<URI> ofInstanceFunction());
            }

         });
      }

      @Override
      protected String provideTimeStamp(final DateService dateService) {
         return "2009-11-08T15:54:08.897Z";
      }
   }

   @Override
   protected Module createModule() {
      return new TestMonitoringRestClientModule();
   }

   @Override
   public ApiMetadata createApiMetadata() {
      return new CloudWatchApiMetadata();
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), FormSigner.class);
   }


}
