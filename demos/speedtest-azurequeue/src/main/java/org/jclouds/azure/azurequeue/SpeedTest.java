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

import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azure.storage.queue.AzureQueueAsyncClient;
import org.jclouds.azure.storage.queue.AzureQueueClient;
import org.jclouds.azure.storage.queue.AzureQueueContextFactory;
import org.jclouds.azure.storage.queue.domain.QueueMetadata;
import org.jclouds.enterprise.config.EnterpriseConfigurationModule;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestContext;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

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

   public static void main(String[] args) throws InterruptedException {

      if (args.length < PARAMETERS)
         throw new IllegalArgumentException(INVALID_SYNTAX);

      boolean isEnterprise = System.getProperties().containsKey("jclouds.enterprise");
      // Args
      String account = args[0];
      String encodedKey = args[1];
      String queueName = args[2];
      int messageCount = Integer.parseInt(args[3]);

      RestContext<AzureQueueAsyncClient, AzureQueueClient> context = isEnterprise ? AzureQueueContextFactory
               .createContext(account, encodedKey, new NullLoggingModule(),
                        new EnterpriseConfigurationModule())
               : AzureQueueContextFactory.createContext(account, encodedKey,
                        new NullLoggingModule());

      try {
         if (purgeQueues(queueName, context)) {
            System.out.printf("pausing 60 seconds before recreating queues%n");
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

   private static void runTests(String queueName, int messageCount, String contextName,
            RestContext<AzureQueueAsyncClient, AzureQueueClient> context)
            throws InterruptedException {
      String message = "1";
      long timeOut = messageCount * 200; // minimum rate should be at least 5/second

      int complete = 0;
      int errors = 0;
      long start = System.currentTimeMillis();

      // fire off all the messages for the test
      Set<ListenableFuture<Void>> responses = Sets.newHashSet();
      for (int i = 0; i < messageCount; i++) {
         responses.add(context.getAsyncApi().putMessage(queueName, message));
      }

      do {
         Set<ListenableFuture<Void>> retries = Sets.newHashSet();
         for (ListenableFuture<Void> response : responses) {
            try {
               response.get(100, TimeUnit.MILLISECONDS);
               complete++;
            } catch (ExecutionException e) {
               System.err.println(e.getMessage());
               errors++;
            } catch (TimeoutException e) {
               retries.add(response);
            }
         }
         responses = Sets.newHashSet(retries);
      } while (responses.size() > 0 && System.currentTimeMillis() < start + timeOut);
      long duration = System.currentTimeMillis() - start;
      if (duration > timeOut)
         System.out.printf("TIMEOUT: context: %s, rate: %f messages/second%n", contextName,
                  ((double) complete) / (duration / 1000.0));
      else
         System.out.printf("COMPLETE: context: %s, rate: %f messages/second%n", contextName,
                  ((double) complete) / (duration / 1000.0));
      System.gc();
      System.out.println("pausing 5 seconds before the next run");
      Thread.sleep(5000);// let the network quiet down
   }

   private static void createQueue(String queueName,
            RestContext<AzureQueueAsyncClient, AzureQueueClient> nullLoggingDefaultContext) {
      System.out.printf("creating queue: %s%n", queueName);
      nullLoggingDefaultContext.getApi().createQueue(queueName);
   }

   private static boolean purgeQueues(String queueName,
            RestContext<AzureQueueAsyncClient, AzureQueueClient> nullLoggingDefaultContext) {
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
