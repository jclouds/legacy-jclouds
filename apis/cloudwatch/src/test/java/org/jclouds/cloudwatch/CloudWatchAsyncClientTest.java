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

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import org.jclouds.Constants;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.cloudwatch.config.CloudWatchRestClientModule;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.EC2Constants;
import org.jclouds.cloudwatch.domain.Namespaces;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptions;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptionsV2;
import org.jclouds.cloudwatch.options.ListMetricsOptions;
import org.jclouds.cloudwatch.xml.GetMetricStatisticsResponseHandler;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.location.config.LocationModule;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Suppliers2;
import org.testng.annotations.Test;

import javax.inject.Named;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import static com.google.common.collect.Maps.transformValues;
import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of {@code CloudWatchAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "CloudWatchAsyncClientTest")
public class CloudWatchAsyncClientTest extends BaseAsyncClientTest<CloudWatchAsyncClient> {

   /**
    * Tests that {@link CloudWatchAsyncClient#getMetricStatistics(String, org.jclouds.cloudwatch.options.GetMetricStatisticsOptionsV2)}
    * works as expected.
    *
    * @throws Exception if anything goes wrong
    */
   public void testGetMetricStatisticsV2() throws Exception {
      Dimension dimension1 = new Dimension(EC2Constants.Dimension.INSTANCE_ID, "SOMEINSTANCEID");
      Dimension dimension2 = new Dimension(EC2Constants.Dimension.INSTANCE_TYPE, "t1.micro");
      Date endTime = new Date(10000000l);
      String metricName = EC2Constants.MetricName.CPU_UTILIZATION;
      String namespace = Namespaces.EC2;
      int period = 60;
      Date startTime = new Date(10000000l);
      Statistics statistic1 = Statistics.MAXIMUM;
      Statistics statistic2 = Statistics.MINIMUM;
      Unit unit = Unit.PERCENT;

      GetMetricStatisticsOptionsV2 goodOptions = GetMetricStatisticsOptionsV2.builder()
                                                                             .dimension(dimension1)
                                                                             .dimension(dimension2)
                                                                             .endTime(endTime)
                                                                             .metricName(metricName)
                                                                             .namespace(namespace)
                                                                             .period(period)
                                                                             .startTime(startTime)
                                                                             .statistic(statistic1)
                                                                             .statistic(statistic2)
                                                                             .unit(unit).build();
      Method method = CloudWatchAsyncClient.class.getMethod("getMetricStatistics", String.class,
                                                            GetMetricStatisticsOptionsV2.class);
      HttpRequest request = processor.createRequest(method, null, goodOptions);

      assertRequestLineEquals(request, "POST https://monitoring.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: monitoring.us-east-1.amazonaws.com\n");

      // Note: Order of request params is alphabetical
      assertPayloadEquals(request,
                          "Action=GetMetricStatistics" +
                                "&Dimensions.member.1.Name=" + dimension1.getName() +
                                "&Dimensions.member.1.Value=" + dimension1.getValue() +
                                "&Dimensions.member.2.Name=" + dimension2.getName() +
                                "&Dimensions.member.2.Value=" + dimension2.getValue() +
                                "&EndTime=1970-01-01T02%3A46%3A40Z" +
                                "&MetricName=" + metricName +
                                "&Namespace=" + URLEncoder.encode(namespace, "UTF-8") +
                                "&Period=" + period +
                                "&StartTime=1970-01-01T02%3A46%3A40Z" +
                                "&Statistics.member.1=" + statistic1 +
                                "&Statistics.member.2=" + statistic2 +
                                "&Unit=" + unit,
                          "application/x-www-form-urlencoded", false);
   }

   /**
    * Tests that {@link CloudWatchAsyncClient#listMetrics(String, org.jclouds.cloudwatch.options.ListMetricsOptions)} works
    * as expected.
    *
    * @throws Exception if anything goes wrong
    */
   public void testListMetrics() throws Exception {
      Method method = CloudWatchAsyncClient.class.getMethod("listMetrics", String.class, ListMetricsOptions.class);
      HttpRequest request;

      // Test an empty request
      request = processor.createRequest(method, null, ListMetricsOptions.builder().build());

      assertRequestLineEquals(request, "POST https://monitoring.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: monitoring.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
                          "Action=ListMetrics",
                          "application/x-www-form-urlencoded", false);

      // Note: Order of request params is as follows => Namespace, MetricName, Dimensions, NextToken

      // Test a request with all (only one dimension)
      Dimension dimension1 = new Dimension(EC2Constants.Dimension.INSTANCE_ID, "SOMEINSTANCEID");
      String metricName = EC2Constants.MetricName.CPU_UTILIZATION;
      String nextToken = "SOMENEXTTOKEN";
      String namespace = Namespaces.EC2;
      request = processor.createRequest(method, null, ListMetricsOptions.builder()
                                                                        .dimension(dimension1)
                                                                        .metricName(metricName)
                                                                        .namespace(namespace)
                                                                        .nextToken(nextToken)
                                                                        .build());

      assertRequestLineEquals(request, "POST https://monitoring.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: monitoring.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
                          "Action=ListMetrics" +
                                  "&Namespace=" + URLEncoder.encode(namespace, "UTF-8") +
                                  "&MetricName=" + metricName +
                                  "&Dimensions.member.1.Name=" + dimension1.getName() +
                                  "&Dimensions.member.1.Value=" + dimension1.getValue() +
                                  "&NextToken=" + nextToken,
                          "application/x-www-form-urlencoded", false);

      // Test a request with multiple dimensions and no NextToken
      Dimension dimension2 = new Dimension(EC2Constants.Dimension.INSTANCE_TYPE, "t1.micro");
      request = processor.createRequest(method, null, ListMetricsOptions.builder()
                                                                        .dimension(dimension1)
                                                                        .dimension(dimension2)
                                                                        .metricName(metricName)
                                                                        .namespace(namespace)
                                                                        .build());

      assertRequestLineEquals(request, "POST https://monitoring.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: monitoring.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
                          "Action=ListMetrics" +
                                  "&Namespace=" + URLEncoder.encode(namespace, "UTF-8") +
                                  "&MetricName=" + metricName +
                                  "&Dimensions.member.1.Name=" + dimension1.getName() +
                                  "&Dimensions.member.1.Value=" + dimension1.getValue() +
                                  "&Dimensions.member.2.Name=" + dimension2.getName() +
                                  "&Dimensions.member.2.Value=" + dimension2.getValue(),
                          "application/x-www-form-urlencoded", false);
   }

   public void testRegisterInstancesWithMeasure() throws SecurityException, NoSuchMethodException, IOException {
      Date date = new Date(10000000l);
      Method method = CloudWatchAsyncClient.class.getMethod("getMetricStatisticsInRegion", String.class, String.class,
            String.class, Date.class, Date.class, int.class, Statistics.class, GetMetricStatisticsOptions[].class);
      HttpRequest request = processor.createRequest(method, (String) null, "CPUUtilization", "AWS/EC2", date, date, 60,
         Statistics.AVERAGE, GetMetricStatisticsOptions.Builder.instanceId("i-12312313"));

      assertRequestLineEquals(request, "POST https://monitoring.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: monitoring.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               request,
               "Action=GetMetricStatistics&Statistics.member.1=Average&Period=60&Namespace=AWS%2FEC2&MetricName=CPUUtilization&StartTime=1970-01-01T02%3A46%3A40Z&EndTime=1970-01-01T02%3A46%3A40Z&Dimensions.member.1.Name=InstanceId&Dimensions.member.1.Value=i-12312313",
               "application/x-www-form-urlencoded", false);

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
   public ApiMetadata createApiMetadata() {
      return new CloudWatchApiMetadata();
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), FormSigner.class);
   }

}
