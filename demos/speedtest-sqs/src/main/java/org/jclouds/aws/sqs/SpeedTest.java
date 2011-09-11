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
package org.jclouds.aws.sqs;

import static org.jclouds.aws.sqs.options.ListQueuesOptions.Builder.queuePrefix;
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.Future;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.sqs.domain.Queue;
import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.enterprise.config.EnterpriseConfigurationModule;
import org.jclouds.logging.ConsoleLogger;
import org.jclouds.logging.Logger;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Module;

/**
 * This the Main class of an Application that tests your response time to amazon SQS.
 * 
 * Usage is: java org.jclouds.aws.sqs.SpeedTest \"accesskeyid\" \"secretkey\" \"queueName\"
 * \"messageCount\"
 * 
 * @author Adrian Cole
 */
public class SpeedTest {
   private static final ImmutableSet<String> REGIONS = ImmutableSet.of(Region.EU_WEST_1, Region.US_EAST_1,
            Region.US_WEST_1, Region.AP_SOUTHEAST_1);
   public static final int PARAMETERS = 4;
   public static final String INVALID_SYNTAX = "Invalid number of parameters. Syntax is: \"accesskeyid\" \"secretkey\"  \"queueName\" \"messageCount\" ";

   private static final Logger logger = Logger.CONSOLE;

   private static final Logger traceLogger = new ConsoleLogger() {

      @Override
      public boolean isTraceEnabled() {
         return true;
      }

      @Override
      public void trace(String message, Object... args) {
         super.info(message, args);
      }

   };

   public static void main(String[] args) throws InterruptedException {

      if (args.length < PARAMETERS)
         throw new IllegalArgumentException(INVALID_SYNTAX);

      boolean isEnterprise = System.getProperties().containsKey("jclouds.enterprise");
      // Args
      String accesskeyid = args[0];
      String secretkey = args[1];
      String queueName = args[2];
      int messageCount = Integer.parseInt(args[3]);

      Set<Module> modules = isEnterprise ? ImmutableSet.<Module> of(new NullLoggingModule(),
               new EnterpriseConfigurationModule()) : ImmutableSet.<Module> of(new NullLoggingModule());

      RestContext<SQSClient, SQSAsyncClient> context = new RestContextFactory().createContext("sqs", accesskeyid,
               secretkey, modules);

      try {
         Set<Queue> queues = Sets.newHashSet();
         if (purgeQueues(queueName, context)) {
            logger.info("pausing 60 seconds before recreating queues");
            Thread.sleep(60 * 1000);
         }
         createQueues(queueName, context, queues);
         runTests(messageCount, isEnterprise ? "enterprise" : "default", context, queues);
      } finally {
         purgeQueues(queueName, context);
         // Close connectons
         context.close();
         System.exit(0);
      }

   }

   private static class QueueMessage {
      final Queue queue;
      final String message;

      QueueMessage(Queue queue, String message) {
         this.queue = queue;
         this.message = message;
      }

      @Override
      public String toString() {
         return "[queue=" + queue + ", message=" + message + "]";
      }
   }

   private static void runTests(int messageCount, String contextName, RestContext<SQSClient, SQSAsyncClient> context,
            Set<Queue> queues) throws InterruptedException {
      String message = "1";
      long timeOut = messageCount * 200; // minimum rate should be at least 5/second

      for (Queue queue : queues) {
         logger.info("context: %s, region: %s, queueName: %s", contextName, queue.getRegion(), queue.getName());

         // fire off all the messages for the test
         Map<QueueMessage, Future<byte[]>> responses = Maps.newHashMap();
         for (int i = 0; i < messageCount; i++) {
            responses.put(new QueueMessage(queue, message), context.getAsyncApi().sendMessage(queue, message));
         }

         Map<QueueMessage, Exception> exceptions = awaitCompletion(responses, MoreExecutors.sameThreadExecutor(),
                  timeOut, traceLogger, String.format("context: %s, region: %s", contextName, queue.getRegion()));

         if (exceptions.size() > 0)
            logger.error("problems in context: %s, region: %s: %s", contextName, queue.getRegion(), exceptions);

         System.gc();
         logger.info("pausing 5 seconds before the next run");
         Thread.sleep(5000);// let the network quiet down
      }
   }

   private static void createQueues(String queueName, RestContext<SQSClient, SQSAsyncClient> nullLoggingDefaultContext,
            Set<Queue> queues) {
      for (String region : REGIONS) {
         logger.info("creating queue: %s in region %s", queueName, region);
         queues.add(nullLoggingDefaultContext.getApi().createQueueInRegion(region, queueName));
      }
   }

   private static boolean purgeQueues(String queueName, RestContext<SQSClient, SQSAsyncClient> nullLoggingDefaultContext) {
      boolean deleted = false;
      for (String region : REGIONS) {
         try {
            SortedSet<Queue> result = Sets.newTreeSet(nullLoggingDefaultContext.getApi().listQueuesInRegion(region,
                     queuePrefix(queueName)));
            if (result.size() >= 1) {
               nullLoggingDefaultContext.getApi().deleteQueue(result.last());
               logger.info("deleted queue: %s in region %s", queueName, region);
               deleted = true;
            }
         } catch (Exception e) {

         }
      }
      return deleted;
   }
}
