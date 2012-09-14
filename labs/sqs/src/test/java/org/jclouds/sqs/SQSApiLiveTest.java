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
package org.jclouds.sqs;

import static org.jclouds.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.providers.AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint;
import static org.jclouds.sqs.options.ListQueuesOptions.Builder.queuePrefix;
import static org.jclouds.sqs.options.ReceiveMessageOptions.Builder.attribute;
import static org.jclouds.sqs.reference.SQSParameters.ACTION;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.Timeout;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.sqs.domain.Action;
import org.jclouds.sqs.domain.QueueAttributes;
import org.jclouds.sqs.internal.BaseSQSApiLiveTest;
import org.jclouds.sqs.xml.ValueHandler;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Module;

/**
 * Tests behavior of {@code SQSApi}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "SQSApiLiveTest")
public class SQSApiLiveTest extends BaseSQSApiLiveTest {

   private Set<URI> queues = Sets.newHashSet();

   @Test
   protected void testListQueues() throws InterruptedException {
      listQueuesInRegion(null);
   }

   protected void listQueuesInRegion(String region) throws InterruptedException {
      SortedSet<URI> allResults = Sets.newTreeSet(api().listQueuesInRegion(region));
      assertNotNull(allResults);
      if (allResults.size() >= 1) {
         URI queue = allResults.last();
         assertQueueInList(region, queue);
      }
   }

   public static final String PREFIX = System.getProperty("user.name") + "-sqs";

   @Test
   protected void testCanRecreateQueueGracefully() throws InterruptedException {
      recreateQueueInRegion(PREFIX + "1", null);
      recreateQueueInRegion(PREFIX + "1", null);
   }

   public String recreateQueueInRegion(String queueName, String region) throws InterruptedException {
      Set<URI> result = api().listQueuesInRegion(region, queuePrefix(queueName));
      if (result.size() >= 1) {
         api().deleteQueue(Iterables.getLast(result));
      }
      URI queue = api().createQueueInRegion(region, queueName);
      assertQueueInList(region, queue);
      queues.add(queue);
      return queueName;
   }

   @Test(dependsOnMethods = "testCanRecreateQueueGracefully")
   protected void testGetQueueAttributes() {
      for (URI queue : queues) {
         Map<String, String> attributes = api().getQueueAttributes(queue, ImmutableSet.of("All"));
         assertEquals(api().getQueueAttributes(queue, attributes.keySet()), attributes);
      }
   }

   String message = "hardyharhar";
   HashCode md5 = Hashing.md5().hashString(message, Charsets.UTF_8);

   @Timeout(duration = 5, timeUnit = TimeUnit.SECONDS)
   static interface AnonymousAttributesApi {
      String getQueueArn();
   }

   static interface AnonymousAttributesAsyncApi {
      @POST
      @Path("/")
      @FormParams(keys = { ACTION, "AttributeName.1" }, values = { "GetQueueAttributes", "QueueArn" })
      @XMLResponseParser(ValueHandler.class)
      ListenableFuture<String> getQueueArn();
   }

   @Test(dependsOnMethods = "testGetQueueAttributes")
   protected void testAddAnonymousPermission() throws InterruptedException {
      for (URI queue : queues) {
         QueueAttributes attributes = api().getQueueAttributes(queue);
         assertNoPermissions(queue);

         String accountToAuthorize = getAccountToAuthorize(queue);
         api().addPermissionToAccount(queue, "fubar", Action.GET_QUEUE_ATTRIBUTES, accountToAuthorize);

         String policyForAuthorizationByAccount = assertPolicyPresent(queue);

         String policyForAnonymous = policyForAuthorizationByAccount.replace("\"" + accountToAuthorize + "\"", "\"*\"");
         api().setQueueAttribute(queue, "Policy", policyForAnonymous);

         assertEquals(getAnonymousAttributesApi(queue).getQueueArn(), attributes.getQueueArn());
      }
   }

   protected String getAccountToAuthorize(URI queue) {
      return Iterables.get(Splitter.on('/').split(queue.getPath()), 1);
   }

   @Test(dependsOnMethods = "testAddAnonymousPermission")
   protected void testRemovePermission() throws InterruptedException {
      for (URI queue : queues) {
         api().removePermission(queue, "fubar");
         assertNoPermissions(queue);
      }
   }

   @Test(dependsOnMethods = "testGetQueueAttributes")
   protected void testSetQueueAttribute() {
      for (URI queue : queues) {
         api().setQueueAttribute(queue, "MaximumMessageSize", "1024");
         assertEquals(api().getQueueAttributes(queue).getMaximumMessageSize(), 1024);
      }
   }

   @Test(dependsOnMethods = "testGetQueueAttributes")
   protected void testSendMessage() {
      for (URI queue : queues) {
         assertEquals(api().sendMessage(queue, message).getMD5(), md5);
      }
   }

   @Test(dependsOnMethods = "testSendMessage")
   protected void testReceiveMessageWithoutHidingMessage() {
      for (URI queue : queues) {
         assertEquals(api().receiveMessage(queue, attribute("All").visibilityTimeout(0)).getMD5(), md5);
      }
   }

   String receiptHandle;

   @Test(dependsOnMethods = "testReceiveMessageWithoutHidingMessage")
   protected void testChangeMessageVisibility() {
      for (URI queue : queues) {
         // start hiding it at 5 seconds
         receiptHandle = api().receiveMessage(queue, attribute("None").visibilityTimeout(5)).getReceiptHandle();
         // hidden message, so we can't see it
         assertNull(api().receiveMessage(queue));
         // this should unhide it
         api().changeMessageVisibility(queue, receiptHandle, 0);
         // so we can see it again
         assertEquals(api().receiveMessage(queue, attribute("All").visibilityTimeout(0)).getMD5(), md5);
      }
   }

   @Test(dependsOnMethods = "testChangeMessageVisibility")
   protected void testDeleteMessage() throws InterruptedException {
      for (URI queue : queues) {
         api().deleteMessage(queue, receiptHandle);
         assertNoMessages(queue);
      }
   }

   @Override
   @AfterClass(groups = "live")
   protected void tearDownContext() {
      for (URI queue : queues) {
         api().deleteQueue(queue);
      }
      super.tearDownContext();
   }

   protected SQSApi api() {
      return context.getApi();
   }

   private AnonymousAttributesApi getAnonymousAttributesApi(URI queue) {
      return ContextBuilder
            .newBuilder(
                  forClientMappedToAsyncClientOnEndpoint(AnonymousAttributesApi.class,
                        AnonymousAttributesAsyncApi.class, queue.toASCIIString()))
            .modules(ImmutableSet.<Module> of(new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor())))
            .buildInjector().getInstance(AnonymousAttributesApi.class);
   }

}
