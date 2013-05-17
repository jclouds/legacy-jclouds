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
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/DeveloperGuide/CW_Support_For_AWS.html#awssg-metricscollected" />
 *
 * @author Jeremy Whitlock
 */
public interface StorageGatewayConstants {

   public static class Dimension {

      public static final String GATEWAY_ID = "GatewayId";
      public static final String GATEWAY_NAME = "GatewayName";
      public static final String VOLUME_ID = "VolumeId";

   }

   public static class MetricName {

      // Applicable for all Dimensions
      public static final String QUEUED_WRITES = "QueuedWrites";
      public static final String READ_BYTES = "ReadBytes";
      public static final String READ_TIME = "ReadTime";
      public static final String WRITE_BYTES = "WriteBytes";
      public static final String WRITE_TIME = "WriteTime";

      // Applicable for only GatewayId and GatewayName Dimensions
      public static final String CLOUD_BYTES_DOWNLOADED = "CloudBytesDownloaded";
      public static final String CLOUD_BYTES_UPLOADED = "CloudBytesUploaded";
      public static final String CLOUD_DOWNLOAD_LATENCY = "CloudDownloadLatency";
      public static final String WORKING_STORAGE_FREE = "WorkingStorageFree";
      public static final String WORKING_STORAGE_PERCENT_USED = "WorkingStoragePercentUsed";
      public static final String WORKING_STORAGE_USED = "WorkingStorageUsed";

   }

}
