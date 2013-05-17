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
package org.jclouds.cloudwatch.domain;

/**
 * Constants interface for the AWS AutoScaling dimensions and metric names as of 2012-04-24.
 *
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/DeveloperGuide/CW_Support_For_AWS.html#elb-metricscollected" />
 *
 * @author Jeremy Whitlock
 */
public interface ELBConstants {

   public static class Dimension {

      public static final String AVAILABILITY_ZONE = "AvailabilityZone";
      public static final String LOAD_BALANCER_NAME = "LoadBalancerName";

   }

   public static class MetricName {

      public static final String HEALTHY_HOST_COUNT = "HealthyHostCount";
      public static final String HTTP_CODE_BACKEND_2XX = "HTTPCode_Backend_2XX";
      public static final String HTTP_CODE_BACKEND_3XX = "HTTPCode_Backend_3XX";
      public static final String HTTP_CODE_BACKEND_4XX = "HTTPCode_Backend_4XX";
      public static final String HTTP_CODE_BACKEND_5XX = "HTTPCode_Backend_5XX";
      public static final String HTTP_CODE_ELB_4XX = "HTTPCode_ELB_4XX";
      public static final String HTTP_CODE_ELB_5XX = "HTTPCode_ELB_5XX";
      public static final String LATENCY = "Latency";
      public static final String REQUEST_COUNT = "RequestCount";
      public static final String UNHEALTHY_HOST_COUNT = "UnHealthyHostCount";

   }

}
