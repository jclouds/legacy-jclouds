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
package org.jclouds.cloudwatch.features;

import static org.testng.Assert.assertEquals;

import java.util.Date;
import java.util.TimeZone;

import org.jclouds.cloudwatch.CloudWatchApi;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.EC2Constants;
import org.jclouds.cloudwatch.domain.GetMetricStatistics;
import org.jclouds.cloudwatch.domain.Namespaces;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.cloudwatch.internal.BaseCloudWatchApiExpectTest;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptions;
import org.jclouds.cloudwatch.options.ListMetricsOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Jeremy Whitlock, Adrian Cole
 */
@Test(groups = "unit", testName = "MetricApiExpectTest")
public class MetricApiExpectTest extends BaseCloudWatchApiExpectTest {

   public MetricApiExpectTest() {
      TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
   }

   HttpRequest listMetrics = HttpRequest.builder()
                                       .method("POST")
                                       .endpoint("https://monitoring.us-east-1.amazonaws.com/")
                                       .addHeader("Host", "monitoring.us-east-1.amazonaws.com")
                                       .payload(
                                          payloadFromStringWithContentType(
                                                "Action=ListMetrics" +
                                                      "&Signature=KSh9oQydCR0HMAV6QPYwDzqwQIpxs8I/ig7brYgHVZU%3D" +
                                                      "&SignatureMethod=HmacSHA256" +
                                                      "&SignatureVersion=2" +
                                                      "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                      "&Version=2010-08-01" +
                                                      "&AWSAccessKeyId=identity",
                                                "application/x-www-form-urlencoded"))
                                       .build();
   
   public void testListMetricsWhenResponseIs2xx() throws Exception {

      HttpResponse listMetricsResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_metrics.xml", "text/xml")).build();

      CloudWatchApi apiWhenMetricsExist = requestSendsResponse(
            listMetrics, listMetricsResponse);

      assertEquals(apiWhenMetricsExist.getMetricApiForRegion(null).list().get(0).toString(),
            "[Metric{namespace=AWS/EC2, metricName=CPUUtilization, dimension=[Dimension{name=InstanceId, value=i-689fcf0f}]}]");
   }

   public void testListMetricsWhenResponseIs404() throws Exception {

      HttpResponse listMetricsResponse = HttpResponse.builder().statusCode(404).build();

      CloudWatchApi apiWhenMetricsDontExist = requestSendsResponse(
            listMetrics, listMetricsResponse);

      assertEquals(apiWhenMetricsDontExist.getMetricApiForRegion(null).list().get(0).toSet(), ImmutableSet.of());
   }
   
   public void testListMetrics2PagesWhenResponseIs2xx() throws Exception {

      HttpResponse listMetricsResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_metrics_marker.xml", "text/xml")).build();
      
      HttpRequest listMetrics2 = HttpRequest.builder()
               .method("POST")
               .endpoint("https://monitoring.us-east-1.amazonaws.com/")
               .addHeader("Host", "monitoring.us-east-1.amazonaws.com")
               .payload(
                  payloadFromStringWithContentType(
                        "Action=ListMetrics" +
                              "&NextToken=MARKER" +
                              "&Signature=RpBdQydXD1jQhEUnXoqT60NEuCP/ZgdvO6Hf3uf/wy0%3D" +
                              "&SignatureMethod=HmacSHA256" +
                              "&SignatureVersion=2" +
                              "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                              "&Version=2010-08-01" +
                              "&AWSAccessKeyId=identity",
                        "application/x-www-form-urlencoded"))
               .build();

      HttpResponse listMetrics2Response = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/list_metrics.xml", "text/xml")).build();

      CloudWatchApi apiWhenMetricsExist = requestsSendResponses(
            listMetrics, listMetricsResponse, listMetrics2, listMetrics2Response);

      assertEquals(apiWhenMetricsExist.getMetricApiForRegion(null).list().concat().toString(),
            "[Metric{namespace=AWS/EC2, metricName=CPUUtilization, dimension=[Dimension{name=InstanceId, value=i-689fcf0f}]}, Metric{namespace=AWS/EC2, metricName=CPUUtilization, dimension=[Dimension{name=InstanceId, value=i-689fcf0f}]}]");
   }

   
   public void testListMetricsWithOptionsWhenResponseIs2xx() throws Exception {
      HttpRequest listMetricsWithOptions =
            HttpRequest.builder()
                       .method("POST")
                       .endpoint("https://monitoring.us-east-1.amazonaws.com/")
                       .addHeader("Host", "monitoring.us-east-1.amazonaws.com")
                       .payload(payloadFromStringWithContentType(
                             "Action=ListMetrics" +
                                   "&Dimensions.member.1.Name=InstanceId" +
                                   "&Dimensions.member.1.Value=SOMEINSTANCEID" +
                                   "&MetricName=CPUUtilization" +
                                   "&Namespace=SOMENEXTTOKEN" +
                                   "&NextToken=AWS/EC2" +
                                   "&Signature=G05HKEx9FJpGZBk02OVYwt3u4g/ilAY9nU5hJI9LDXA%3D" +
                                   "&SignatureMethod=HmacSHA256" +
                                   "&SignatureVersion=2" +
                                   "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                   "&Version=2010-08-01" +
                                   "&AWSAccessKeyId=identity",
                             "application/x-www-form-urlencoded"))
                       .build();
      
      HttpResponse listMetricsWithOptionsResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/list_metrics.xml", "text/xml")).build();

      CloudWatchApi apiWhenMetricsWithOptionsExist = requestSendsResponse(listMetricsWithOptions,
               listMetricsWithOptionsResponse);

      assertEquals(
               apiWhenMetricsWithOptionsExist.getMetricApiForRegion(null).list(
                        ListMetricsOptions.Builder
                                          .dimension(new Dimension(EC2Constants.Dimension.INSTANCE_ID,
                                                                   "SOMEINSTANCEID"))
                                          .metricName(EC2Constants.MetricName.CPU_UTILIZATION)
                                          .namespace("SOMENEXTTOKEN")
                                          .afterMarker(Namespaces.EC2)).toString(),
         "[Metric{namespace=AWS/EC2, metricName=CPUUtilization, dimension=[Dimension{name=InstanceId, value=i-689fcf0f}]}]");
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
                                       .endpoint("https://monitoring.us-east-1.amazonaws.com/")
                                       .addHeader("Host", "monitoring.us-east-1.amazonaws.com")
                                       .payload(
                                          payloadFromStringWithContentType(
                                                "Action=GetMetricStatistics" +
                                                      "&EndTime=1970-01-01T02%3A46%3A40Z" +
                                                      "&MetricName=CPUUtilization" +
                                                      "&Namespace=AWS/EC2" +
                                                      "&Period=60" +
                                                      "&Signature=rmg8%2Ba7w4ycy/KfO8rnuj6rDL0jNE96m8GKfjh3SWcw%3D" +
                                                      "&SignatureMethod=HmacSHA256" +
                                                      "&SignatureVersion=2" +
                                                      "&StartTime=1970-01-01T02%3A46%3A40Z" +
                                                      "&Statistics.member.1=Maximum" +
                                                      "&Statistics.member.2=Minimum" +
                                                      "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                      "&Unit=Percent" +
                                                      "&Version=2010-08-01" +
                                                      "&AWSAccessKeyId=identity",
                                                "application/x-www-form-urlencoded"))
                                       .build();
   
   public void testGetMetricStatisticsWhenResponseIs2xx() throws Exception {

      HttpResponse getMetricStatisticsResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResourceWithContentType("/get_metric_statistics.xml", "text/xml")).build();

      CloudWatchApi apiWhenMetricsExist = requestSendsResponse(getMetricStatistics, getMetricStatisticsResponse);

      assertEquals(
               apiWhenMetricsExist.getMetricApiForRegion(null).getMetricStatistics(stats).toString(),
               // TODO: make an object for this
               "GetMetricStatisticsResponse{label=CPUUtilization, " +
                     "datapoints=[Datapoint{timestamp=Thu Jan 15 16:00:00 PST 2009, customUnit=null, maximum=null, " +
                     "minimum=null, average=0.17777777777777778, sum=null, samples=9.0, unit=Percent}, " +
                     "Datapoint{timestamp=Thu Jan 15 16:01:00 PST 2009, customUnit=null, maximum=null, minimum=null, " +
                     "average=0.1, sum=null, samples=8.0, unit=Percent}]}");
   }

   // TODO: this should really be an empty set
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testGetMetricStatisticsWhenResponseIs404() throws Exception {

      HttpResponse getMetricStatisticsResponse = HttpResponse.builder().statusCode(404).build();

      CloudWatchApi apiWhenMetricsDontExist = requestSendsResponse(getMetricStatistics, getMetricStatisticsResponse);

      apiWhenMetricsDontExist.getMetricApiForRegion(null).getMetricStatistics(stats);
   }
   
   public void testGetMetricStatisticsWithOptionsWhenResponseIs2xx() throws Exception {
      HttpRequest getMetricStatistics =
            HttpRequest.builder()
                       .method("POST")
                       .endpoint("https://monitoring.us-east-1.amazonaws.com/")
                       .addHeader("Host", "monitoring.us-east-1.amazonaws.com")
                       .payload(payloadFromStringWithContentType(
                             "Action=GetMetricStatistics" +
                                   "&Dimensions.member.1.Name=InstanceId" +
                                   "&Dimensions.member.1.Value=SOMEINSTANCEID" +
                                   "&Dimensions.member.2.Name=InstanceType" +
                                   "&Dimensions.member.2.Value=t1.micro" +
                                   "&EndTime=1970-01-01T02%3A46%3A40Z" +
                                   "&MetricName=CPUUtilization" +
                                   "&Namespace=AWS/EC2" +
                                   "&Period=60" +
                                   "&Signature=e0WyI/Nm4hN2%2BMEm1mjRUzsvgvMCdFXbVJWi4ORpwic%3D" +
                                   "&SignatureMethod=HmacSHA256" +
                                   "&SignatureVersion=2" +
                                   "&StartTime=1970-01-01T02%3A46%3A40Z" +
                                   "&Statistics.member.1=Maximum" +
                                   "&Statistics.member.2=Minimum" +
                                   "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                   "&Unit=Percent" +
                                   "&Version=2010-08-01" +
                                   "&AWSAccessKeyId=identity",
                             "application/x-www-form-urlencoded"))
                       .build();

      HttpResponse getMetricStatisticsResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResourceWithContentType("/get_metric_statistics.xml", "text/xml")).build();

      CloudWatchApi apiWhenMetricsExist = requestSendsResponse(getMetricStatistics, getMetricStatisticsResponse);

      Dimension dimension1 = new Dimension(EC2Constants.Dimension.INSTANCE_ID, "SOMEINSTANCEID");
      Dimension dimension2 = new Dimension(EC2Constants.Dimension.INSTANCE_TYPE, "t1.micro");

      assertEquals(
               apiWhenMetricsExist.getMetricApiForRegion(null).getMetricStatistics(stats,
                        GetMetricStatisticsOptions.Builder.dimension(dimension1).dimension(dimension2)).toString(),
               // TODO: make an object for this
               "GetMetricStatisticsResponse{label=CPUUtilization, " +
                     "datapoints=[Datapoint{timestamp=Thu Jan 15 16:00:00 PST 2009, customUnit=null, maximum=null, " +
                     "minimum=null, average=0.17777777777777778, sum=null, samples=9.0, unit=Percent}, " +
                     "Datapoint{timestamp=Thu Jan 15 16:01:00 PST 2009, customUnit=null, maximum=null, minimum=null, " +
                     "average=0.1, sum=null, samples=8.0, unit=Percent}]}");
   }

}
