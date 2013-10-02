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
package org.jclouds.sqs.config;


/**
 * Configuration properties and constants used in SQS connections.
 * 
 * @author Adrian Cole
 */
public interface SQSProperties {

   /**
    * Integer property.
    * <p/>
    * When creating a queue, you can encounter
    * {@code AWS.SimpleQueueService.QueueDeletedRecently}, which is typically a
    * resolvable error. default tries are 60,
    */
   public static final String CREATE_QUEUE_MAX_RETRIES = "jclouds.sqs.create-queue.max-retries";

   /**
    * Long property.
    * <p/>
    * When creating a queue, you can encounter
    * {@code AWS.SimpleQueueService.QueueDeletedRecently}, which is typically a
    * resolvable error. default interval between tries is 1000 milliseconds (1
    * second).
    */
   public static final String CREATE_QUEUE_RETRY_INTERVAL = "jclouds.sqs.create-queue.retry-interval";

}
