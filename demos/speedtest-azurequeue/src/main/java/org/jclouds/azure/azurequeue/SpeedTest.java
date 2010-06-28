/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.azure.azurequeue;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.concurrent.ConcurrentUtils.awaitCompletion;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azure.storage.queue.AzureQueueAsyncClient;
import org.jclouds.azure.storage.queue.AzureQueueClient;
import org.jclouds.azure.storage.queue.domain.QueueMetadata;
import org.jclouds.enterprise.config.EnterpriseConfigurationModule;
import org.jclouds.logging.ConsoleLogger;
import org.jclouds.logging.Logger;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Module;

/**
 * This the Main class of an Application that tests your response time to amazon AzureQueue.
 * 
 * Usage is: java org.jclouds.aws.sqs.SpeedTest \"account\" \"encodedKey\" \"queueName\"
 * \"messageCount\"
 * 
 * @author Adrian Cole
 */
public class SpeedTest {

   public static int PARAMETERS = 4;
   public static String INVALID_SYNTAX = "Invalid number of parameters. Syntax is: \"account\" \"encodedKey\"  \"queueName\" \"messageCount\" ";
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
      String account = args[0];
      String encodedKey = args[1];
      String queueName = args[2];
      int messageCount = Integer.parseInt(args[3]);

      Set<Module> modules = isEnterprise ? ImmutableSet.<Module> of(new NullLoggingModule(),
               new EnterpriseConfigurationModule()) : ImmutableSet
               .<Module> of(new NullLoggingModule());

      RestContext<AzureQueueClient, AzureQueueAsyncClient> context = new RestContextFactory()
               .createContext("azurequeue", account, encodedKey, modules);

      try {
         if (purgeQueues(queueName, context)) {
            logger.info("pausing 60 seconds before recreating queues");
            Thread.sleep(60 * 1000);
         }
         createQueue(queueName, context);
         runTests(queueName, messageCount, isEnterprise ? "enterprise" : "default", context);
      } finally {
         purgeQueues(queueName, context);
         // Close connectons
         context.close();
         System.exit(0);
      }

   }

   private static class QueueMessage {
      final String queue;
      final String message;

      QueueMessage(String queue, String message) {
         this.queue = queue;
         this.message = message;
      }

      @Override
      public String toString() {
         return "[queue=" + queue + ", message=" + message + "]";
      }
   }

   private static void runTests(String queueName, int messageCount, String contextName,
            RestContext<AzureQueueClient, AzureQueueAsyncClient> context)
            throws InterruptedException {
      String message = "1";
      long timeOut = messageCount * 200; // minimum rate should be at least 5/second

      logger.info("context: %s, queueName: %s", contextName, queueName);

      // fire off all the messages for the test
      Map<QueueMessage, ListenableFuture<Void>> responses = Maps.newHashMap();
      for (int i = 0; i < messageCount; i++) {
         responses.put(new QueueMessage(queueName, message), context.getAsyncApi().putMessage(
                  queueName, message));
      }

      Map<QueueMessage, Exception> exceptions = awaitCompletion(responses, sameThreadExecutor(),
               timeOut, traceLogger, String.format("context: %s", contextName));

      if (exceptions.size() > 0)
         logger.error("problems in context: %s: %s", contextName, exceptions);

      System.gc();
      logger.info("pausing 5 seconds before the next run");
      Thread.sleep(5000);// let the network quiet down
   }

   private static void createQueue(String queueName,
            RestContext<AzureQueueClient, AzureQueueAsyncClient> nullLoggingDefaultContext) {
      logger.info("creating queue: %s", queueName);
      nullLoggingDefaultContext.getApi().createQueue(queueName);
   }

   private static boolean purgeQueues(String queueName,
            RestContext<AzureQueueClient, AzureQueueAsyncClient> nullLoggingDefaultContext) {
      boolean deleted = false;
      try {
         SortedSet<QueueMetadata> result = Sets.newTreeSet(nullLoggingDefaultContext.getApi()
                  .listQueues(ListOptions.Builder.prefix(queueName)));
         if (result.size() >= 1) {
            nullLoggingDefaultContext.getApi().deleteQueue(result.last().getName());
            System.out.printf("deleted queue: %s%n", queueName);
            deleted = true;
         }
      } catch (Exception e) {

      }
      return deleted;
   }
}
