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
package org.jclouds.aws.sqs;

import static org.jclouds.aws.sqs.options.ListQueuesOptions.Builder.queuePrefix;

import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.sqs.domain.Queue;
import org.jclouds.enterprise.config.EnterpriseConfigurationModule;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestContext;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * This the Main class of an Application that tests your response time to amazon SQS.
 * 
 * Usage is: java org.jclouds.aws.sqs.SpeedTest \"accesskeyid\" \"secretkey\" \"queueName\"
 * \"messageCount\"
 * 
 * @author Adrian Cole
 */
public class SpeedTest {

   public static int PARAMETERS = 4;
   public static String INVALID_SYNTAX = "Invalid number of parameters. Syntax is: \"accesskeyid\" \"secretkey\"  \"queueName\" \"messageCount\" ";

   public static void main(String[] args) throws InterruptedException {

      if (args.length < PARAMETERS)
         throw new IllegalArgumentException(INVALID_SYNTAX);

      boolean isEnterprise = System.getProperties().containsKey("jclouds.enterprise");
      // Args
      String accesskeyid = args[0];
      String secretkey = args[1];
      String queueName = args[2];
      int messageCount = Integer.parseInt(args[3]);

      RestContext<SQSAsyncClient, SQSClient> context = isEnterprise ? SQSContextFactory
               .createContext(accesskeyid, secretkey, new NullLoggingModule(),
                        new EnterpriseConfigurationModule()) : SQSContextFactory.createContext(
               accesskeyid, secretkey, new NullLoggingModule());

      try {
         Set<Queue> queues = Sets.newHashSet();
         if (purgeQueues(queueName, context)) {
            System.out.printf("pausing 60 seconds before recreating queues%n");
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

   private static void runTests(int messageCount, String contextName,
            RestContext<SQSAsyncClient, SQSClient> context, Set<Queue> queues)
            throws InterruptedException {
      String message = "1";
      long timeOut = messageCount * 200; // minimum rate should be at least 5/second

      for (Queue queue : queues) {
         int complete = 0;
         int errors = 0;
         long start = System.currentTimeMillis();

         // fire off all the messages for the test
         Set<ListenableFuture<byte[]>> responses = Sets.newHashSet();
         for (int i = 0; i < messageCount; i++) {
            responses.add(context.getAsyncApi().sendMessage(queue, message));
         }

         do {
            Set<ListenableFuture<byte[]>> retries = Sets.newHashSet();
            for (ListenableFuture<byte[]> response : responses) {
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
            System.out.printf("TIMEOUT: context: %s, region: %s, rate: %f messages/second%n",
                     contextName, queue.getRegion(), ((double) complete) / (duration / 1000.0));
         else
            System.out.printf("COMPLETE:  context: %s, region: %s, rate: %f messages/second%n",
                     contextName, queue.getRegion(), ((double) complete) / (duration / 1000.0));
         System.gc();
         System.out.println("pausing 5 seconds before the next run");
         Thread.sleep(5000);// let the network quiet down
      }
   }

   private static void createQueues(String queueName,
            RestContext<SQSAsyncClient, SQSClient> nullLoggingDefaultContext, Set<Queue> queues) {
      for (Region region : ImmutableSet.of(Region.EU_WEST_1, Region.US_EAST_1, Region.US_WEST_1)) {
         System.out.printf("creating queue: %s in region %s%n", queueName, region);
         queues.add(nullLoggingDefaultContext.getApi().createQueueInRegion(region, queueName));
      }
   }

   private static boolean purgeQueues(String queueName,
            RestContext<SQSAsyncClient, SQSClient> nullLoggingDefaultContext) {
      boolean deleted = false;
      for (Region region : ImmutableSet.of(Region.EU_WEST_1, Region.US_EAST_1, Region.US_WEST_1)) {
         try {
            SortedSet<Queue> result = Sets.newTreeSet(nullLoggingDefaultContext.getApi()
                     .listQueuesInRegion(region, queuePrefix(queueName)));
            if (result.size() >= 1) {
               nullLoggingDefaultContext.getApi().deleteQueue(result.last());
               System.out.printf("deleted queue: %s in region %s%n", queueName, region);
               deleted = true;
            }
         } catch (Exception e) {

         }
      }
      return deleted;
   }
}
