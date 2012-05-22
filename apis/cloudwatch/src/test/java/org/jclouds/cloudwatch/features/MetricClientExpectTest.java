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
 * Unles required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either expres or implied.  See the License for the
 * specific language governing permisions and limitations
 * under the License.
 */
package org.jclouds.cloudwatch.features;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Date;
import java.util.TimeZone;

import org.jclouds.cloudwatch.CloudWatchClient;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.EC2Constants;
import org.jclouds.cloudwatch.domain.GetMetricStatistics;
import org.jclouds.cloudwatch.domain.Namespaces;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.cloudwatch.internal.BaseCloudWatchClientExpectTest;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptions;
import org.jclouds.cloudwatch.options.ListMetricsOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * @author Jeremy Whitlock, Adrian Cole
 */
@Test(groups = "unit", testName = "MetricClientExpectTest")
public class MetricClientExpectTest extends BaseCloudWatchClientExpectTest {

   public MetricClientExpectTest() {
      TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
   }

   HttpRequest listMetrics = HttpRequest.builder()
                                       .method("POST")
                                       .endpoint(URI.create("https://monitoring.us-east-1.amazonaws.com/"))
                                       .headers(ImmutableMultimap.<String, String> builder()
                                                .put("Host", "monitoring.us-east-1.amazonaws.com")
                                                .build())
                                       .payload(
                                          payloadFromStringWithContentType(
                                                   new StringBuilder()
                                                   .append("Action=ListMetrics").append('&')
                                                   .append("Signature=KSh9oQydCR0HMAV6QPYwDzqwQIpxs8I%2Fig7brYgHVZU%3D").append('&')
                                                   .append("SignatureMethod=HmacSHA256").append('&')
                                                   .append("SignatureVersion=2").append('&')
                                                   .append("Timestamp=2009-11-08T15%3A54%3A08.897Z").append('&')
                                                   .append("Version=2010-08-01").append('&')
                                                   .append("AWSAccessKeyId=identity").toString(), "application/x-www-form-urlencoded"))
                                       .build();
   
   public void testListMetricsWhenResponseIs2xx() throws Exception {

      HttpResponse listMetricsResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_metrics.xml", "text/xml")).build();

      CloudWatchClient clientWhenMetricsExist = requestSendsResponse(
            listMetrics, listMetricsResponse);

      assertEquals(clientWhenMetricsExist.getMetricClientForRegion(null).listMetrics().toString(),
            "ListMetricsResponse{metrics=[Metric{namespace=AWS/EC2, metricName=CPUUtilization, dimension=[Dimension{name=InstanceId, value=i-689fcf0f}]}], nextToken=null}");
   }

   // TODO: this should really be an empty set
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testListMetricsWhenResponseIs404() throws Exception {

      HttpResponse listMetricsResponse = HttpResponse.builder().statusCode(404).build();

      CloudWatchClient clientWhenMetricsDontExist = requestSendsResponse(
            listMetrics, listMetricsResponse);

      clientWhenMetricsDontExist.getMetricClientForRegion(null).listMetrics();
   }
   
   public void testListMetricsWithOptionsWhenResponseIs2xx() throws Exception {
      HttpRequest listMetricsWithOptions = HttpRequest.builder()
                                                      .method("POST")
                                                      .endpoint(URI.create("https://monitoring.us-east-1.amazonaws.com/"))
                                                      .headers(ImmutableMultimap.<String, String> builder()
                                                               .put("Host", "monitoring.us-east-1.amazonaws.com")
                                                               .build())
                                                      .payload(
                                                         payloadFromStringWithContentType(
                                                                  new StringBuilder()
                                                                  .append("Action=ListMetrics").append('&')
                                                                  .append("Dimensions.member.1.Name=InstanceId").append('&')
                                                                  .append("Dimensions.member.1.Value=SOMEINSTANCEID").append('&')
                                                                  .append("MetricName=CPUUtilization").append('&')
                                                                  .append("Namespace=SOMENEXTTOKEN").append('&')
                                                                  .append("NextToken=AWS%2FEC2").append('&')
                                                                  .append("Signature=G05HKEx9FJpGZBk02OVYwt3u4g%2FilAY9nU5hJI9LDXA%3D").append('&')
                                                                  .append("SignatureMethod=HmacSHA256").append('&')
                                                                  .append("SignatureVersion=2").append('&')
                                                                  .append("Timestamp=2009-11-08T15%3A54%3A08.897Z").append('&')
                                                                  .append("Version=2010-08-01").append('&')
                                                                  .append("AWSAccessKeyId=identity").toString(), "application/x-www-form-urlencoded"))
                                                      .build();
      
      HttpResponse listMetricsWithOptionsResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/list_metrics.xml", "text/xml")).build();

      CloudWatchClient clientWhenMetricsWithOptionsExist = requestSendsResponse(listMetricsWithOptions,
               listMetricsWithOptionsResponse);

      assertEquals(
               clientWhenMetricsWithOptionsExist.getMetricClientForRegion(null).listMetrics(
                        ListMetricsOptions.builder()
                                          .dimension(new Dimension(EC2Constants.Dimension.INSTANCE_ID, "SOMEINSTANCEID"))
                                          .metricName(EC2Constants.MetricName.CPU_UTILIZATION)
                                          .namespace("SOMENEXTTOKEN")
                                          .nextToken( Namespaces.EC2)
                                          .build()).toString(),
         "ListMetricsResponse{metrics=[Metric{namespace=AWS/EC2, metricName=CPUUtilization, dimension=[Dimension{name=InstanceId, value=i-689fcf0f}]}], nextToken=null}");
   }

   GetMetricStatistics stats = GetMetricStatistics.builder()
                                                  .endTime(new Date(10000000l))
                                                  .metricName(EC2Constants.MetricName.CPU_UTILIZATION)
                                                  .namespace(Namespaces.EC2)
                                                  .period(60)
                                                  .startTime(new Date(10000000l))
                                                  .statistic(Statistics.MAXIMUM)
                                                  .statistic(Statistics.MINIMUM)
                                                  .unit(Unit.PERCENT).build();
   
   HttpRequest getMetricStatistics = HttpRequest.builder()
                                       .method("POST")
                                       .endpoint(URI.create("https://monitoring.us-east-1.amazonaws.com/"))
                                       .headers(ImmutableMultimap.<String, String> builder()
                                                .put("Host", "monitoring.us-east-1.amazonaws.com")
                                                .build())
                                       .payload(
                                          payloadFromStringWithContentType(
                                                   new StringBuilder()
                                                   .append("Action=GetMetricStatistics").append('&')
                                                   .append("EndTime=1970-01-01T02%3A46%3A40Z").append('&')
                                                   .append("MetricName=CPUUtilization").append('&')
                                                   .append("Namespace=AWS%2FEC2").append('&')
                                                   .append("Period=60").append('&')
                                                   .append("Signature=rmg8%2Ba7w4ycy%2FKfO8rnuj6rDL0jNE96m8GKfjh3SWcw%3D").append('&')
                                                   .append("SignatureMethod=HmacSHA256").append('&')
                                                   .append("SignatureVersion=2").append('&')
                                                   .append("StartTime=1970-01-01T02%3A46%3A40Z").append('&')
                                                   .append("Statistics.member.1=Maximum").append('&')
                                                   .append("Statistics.member.2=Minimum").append('&')
                                                   .append("Timestamp=2009-11-08T15%3A54%3A08.897Z").append('&')
                                                   .append("Unit=Percent").append('&').append("Version=2010-08-01").append('&')
                                                   .append("AWSAccessKeyId=identity").toString(), "application/x-www-form-urlencoded"))
                                       .build();
   
   public void testGetMetricStatisticsWhenResponseIs2xx() throws Exception {

      HttpResponse getMetricStatisticsResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResourceWithContentType("/get_metric_statistics.xml", "text/xml")).build();

      CloudWatchClient clientWhenMetricsExist = requestSendsResponse(getMetricStatistics, getMetricStatisticsResponse);

      assertEquals(
               clientWhenMetricsExist.getMetricClientForRegion(null).getMetricStatistics(stats).toString(),
               // TODO: make an object for this
               "GetMetricStatisticsResponse{label=CPUUtilization, datapoints=[Datapoint{timestamp=Thu Jan 15 16:00:00 PST 2009, customUnit=null, maximum=null, minimum=null, average=0.17777777777777778, sum=null, samples=9.0, unit=Percent}, Datapoint{timestamp=Thu Jan 15 16:01:00 PST 2009, customUnit=null, maximum=null, minimum=null, average=0.1, sum=null, samples=8.0, unit=Percent}]}");
   }

   // TODO: this should really be an empty set
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testGetMetricStatisticsWhenResponseIs404() throws Exception {

      HttpResponse getMetricStatisticsResponse = HttpResponse.builder().statusCode(404).build();

      CloudWatchClient clientWhenMetricsDontExist = requestSendsResponse(getMetricStatistics, getMetricStatisticsResponse);

      clientWhenMetricsDontExist.getMetricClientForRegion(null).getMetricStatistics(stats);
   }
   
   public void testGetMetricStatisticsWithOptionsWhenResponseIs2xx() throws Exception {
      HttpRequest getMetricStatistics = HttpRequest.builder()
                                                   .method("POST")
                                                   .endpoint(URI.create("https://monitoring.us-east-1.amazonaws.com/"))
                                                   .headers(ImmutableMultimap.<String, String> builder()
                                                            .put("Host", "monitoring.us-east-1.amazonaws.com")
                                                            .build())
                                                   .payload(
                                                      payloadFromStringWithContentType(
                                                               new StringBuilder()
                                                               .append("Action=GetMetricStatistics").append('&')
                                                               .append("Dimensions.member.1.Name=InstanceId").append('&')
                                                               .append("Dimensions.member.1.Value=SOMEINSTANCEID").append('&')
                                                               .append("Dimensions.member.2.Name=InstanceType").append('&')
                                                               .append("Dimensions.member.2.Value=t1.micro").append('&')
                                                               .append("EndTime=1970-01-01T02%3A46%3A40Z").append('&')
                                                               .append("MetricName=CPUUtilization").append('&')
                                                               .append("Namespace=AWS%2FEC2").append('&')
                                                               .append("Period=60").append('&')
                                                               .append("Signature=e0WyI%2FNm4hN2%2BMEm1mjRUzsvgvMCdFXbVJWi4ORpwic%3D").append('&')
                                                               .append("SignatureMethod=HmacSHA256").append('&')
                                                               .append("SignatureVersion=2").append('&')
                                                               .append("StartTime=1970-01-01T02%3A46%3A40Z").append('&')
                                                               .append("Statistics.member.1=Maximum").append('&')
                                                               .append("Statistics.member.2=Minimum").append('&')
                                                               .append("Timestamp=2009-11-08T15%3A54%3A08.897Z").append('&')
                                                               .append("Unit=Percent").append('&')
                                                               .append("Version=2010-08-01").append('&')
                                                               .append("AWSAccessKeyId=identity").toString(), "application/x-www-form-urlencoded"))
                                                   .build();

      HttpResponse getMetricStatisticsResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResourceWithContentType("/get_metric_statistics.xml", "text/xml")).build();

      CloudWatchClient clientWhenMetricsExist = requestSendsResponse(getMetricStatistics, getMetricStatisticsResponse);

      Dimension dimension1 = new Dimension(EC2Constants.Dimension.INSTANCE_ID, "SOMEINSTANCEID");
      Dimension dimension2 = new Dimension(EC2Constants.Dimension.INSTANCE_TYPE, "t1.micro");

      assertEquals(
               clientWhenMetricsExist.getMetricClientForRegion(null).getMetricStatistics(stats,
                        GetMetricStatisticsOptions.Builder.dimension(dimension1).dimension(dimension2)).toString(),
               // TODO: make an object for this
               "GetMetricStatisticsResponse{label=CPUUtilization, datapoints=[Datapoint{timestamp=Thu Jan 15 16:00:00 PST 2009, customUnit=null, maximum=null, minimum=null, average=0.17777777777777778, sum=null, samples=9.0, unit=Percent}, Datapoint{timestamp=Thu Jan 15 16:01:00 PST 2009, customUnit=null, maximum=null, minimum=null, average=0.1, sum=null, samples=8.0, unit=Percent}]}");
   }

}
