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
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/DeveloperGuide/CW_Support_For_AWS.html#ebs-metricscollected" />
 *
 * @author Jeremy Whitlock
 */
public interface EBSConstants {

   public static class Dimension {

      public static final String VOLUME_ID = "VolumeId";

   }

   public static class MetricName {

      public static final String VOLUME_IDLE_TIME = "VolumeIdleTime";
      public static final String VOLUME_QUEUE_LENGTH = "VolumeQueueLength";
      public static final String VOLUME_READ_BYTES = "VolumeReadBytes";
      public static final String VOLUME_READ_OPS = "VolumeReadOps";
      public static final String VOLUME_TOTAL_READ_TIME = "VolumeTotalReadTime";
      public static final String VOLUME_TOTAL_WRITE_TIME = "VolumeTotalWriteTime";
      public static final String VOLUME_WRITE_BYTES = "VolumeWriteBytes";
      public static final String VOLUME_WRITE_OPS = "VolumeWriteOps";

   }

}
