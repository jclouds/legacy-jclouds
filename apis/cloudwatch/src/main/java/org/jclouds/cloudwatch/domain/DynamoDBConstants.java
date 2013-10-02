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
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/DeveloperGuide/CW_Support_For_AWS.html#dynamo-metricscollected" />
 *
 * @author Jeremy Whitlock
 */
public interface DynamoDBConstants {

   public static class Dimension {

      public static final String OPERATION = "Operation";
      public static final String TABLE_NAME = "TableName";

   }

   public static class MetricName {

      public static final String CONSUMED_READ_CAPACITY_UNITS = "ConsumedReadCapacityUnits";
      public static final String CONSUMED_WRITE_CAPACITY_UNITS = "ConsumedWriteCapacityUnits";
      public static final String SUCCESSFUL_REQUEST_LATENCY = "SuccessfulRequestLatency";
      public static final String RETURNED_ITEM_COUNT = "ReturnedItemCount";
      public static final String SYSTEM_ERRORS = "SystemErrors";
      public static final String THROTTLED_REQUESTS = "ThrottledRequests";
      public static final String USER_ERRORS = "UserErrors";

   }

}
