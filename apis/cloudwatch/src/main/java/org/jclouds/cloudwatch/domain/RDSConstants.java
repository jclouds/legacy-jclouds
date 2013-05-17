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
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/DeveloperGuide/CW_Support_For_AWS.html#rds-metricscollected" />
 *
 * @author Jeremy Whitlock
 */
public interface RDSConstants {

   public static class Dimension {

      public static final String DB_INSTANCE_IDENTIFIER = "DBInstanceIdentifier";
      public static final String DATABASE_CLASS = "DatabaseClass";
      public static final String ENGINE_NAME = "EngineName";

   }

   public static class MetricName {

      public static final String BIN_LOG_DISK_USAGE = "BinLogDiskUsage";
      public static final String CPU_UTILIZATION = "CPUUtilization";
      public static final String DATABASE_CONNECTIONS = "DatabaseConnections";
      public static final String FREEABLE_MEMORY = "FreeableMemory";
      public static final String FREE_STORAGE_SPACE = "FreeStorageSpace";
      public static final String REPLICA_LAG = "ReplicaLag";
      public static final String READ_IO_OPS = "ReadIOPS";
      public static final String READ_LATENCY = "ReadLatency";
      public static final String READ_THROUGHPUT = "ReadThroughput";
      public static final String SWAP_USAGE = "SwapUsage";
      public static final String WRITE_IO_OPS = "WriteIOPS";
      public static final String WRITE_LATENCY = "WriteLtency";
      public static final String WRITE_THROUGHPUT = "WriteThroughput";

   }

}
