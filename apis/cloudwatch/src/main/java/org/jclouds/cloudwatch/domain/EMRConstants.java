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
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/DeveloperGuide/CW_Support_For_AWS.html#emr-metricscollected" />
 *
 * @author Jeremy Whitlock
 */
public interface EMRConstants {

   public static class Dimension {

      public static final String JOB_FLOW_ID = "JobFlowId";
      public static final String JOB_ID = "JobId";

   }

   public static class MetricName {

      public static final String CORE_NODES_PENDING = "CoreNodesPending";
      public static final String CORE_NODES_RUNNING = "CoreNodesRunning";
      public static final String HDFS_BYTES_READ = "HDFSBytesRead";
      public static final String HDFS_BYTES_WRITTEN = "HDFSBytesWritten";
      public static final String HDFS_UTILIZATION = "HDFSUtilization";
      public static final String IS_IDLE = "IsIdle";
      public static final String JOBS_FAILED = "JobsFailed";
      public static final String JOBS_RUNNING = "JobsRunning";
      public static final String LIVE_DATA_NODES = "LiveDataNodes";
      public static final String LIVE_TASK_TRACKERS = "LiveTaskTrackers";
      public static final String MAP_SLOTS_OPEN = "MapSlotsOpen";
      public static final String MISSING_BLOCKS = "MissingBlocks";
      public static final String REDUCE_SLOTS_OPEN = "ReduceSlotsOpen";
      public static final String REMAINING_MAP_TASKS = "RemainingMapTasks";
      public static final String REMAINING_MAP_TASKS_PER_SLOT = "RemainingMapTasksPerSlot";
      public static final String REMAINING_REDUCE_TASKS = "RemainingReduceTasks";
      public static final String RUNNING_MAP_TASKS = "RunningMapTasks";
      public static final String RUNNING_REDUCE_TASKS = "RunningReduceTasks";
      public static final String S3_BYTES_READ = "S3BytesRead";
      public static final String S3_BYTES_WRITTEN = "S3BytesWritten";
      public static final String TASK_NODES_PENDING = "TaskNodesPending";
      public static final String TASK_NODES_RUNNING = "TaskNodesRunning";
      public static final String TOTAL_LOAD = "TotalLoad";

   }

}
