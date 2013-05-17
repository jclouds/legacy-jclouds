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
package org.jclouds.sqs.features;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.providers.AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint;
import static org.jclouds.sqs.reference.SQSParameters.ACTION;
import static org.testng.Assert.assertEquals;

import java.io.Closeable;
import java.net.URI;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.sqs.domain.Action;
import org.jclouds.sqs.domain.QueueAttributes;
import org.jclouds.sqs.internal.BaseSQSApiLiveTest;
import org.jclouds.sqs.xml.ValueHandler;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Module;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "PermissionApiLiveTest")
public class PermissionApiLiveTest extends BaseSQSApiLiveTest {

   public PermissionApiLiveTest() {
      prefix = prefix + "-permission";
   }

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setup() {
      super.setup();
      recreateQueueInRegion(prefix, null);
   }

   static interface AnonymousAttributesApi extends Closeable {
      String getQueueArn();
   }

   static interface AnonymousAttributesAsyncApi extends Closeable {
      @POST
      @Path("/")
      @FormParams(keys = { ACTION, "AttributeName.1" }, values = { "GetQueueAttributes", "QueueArn" })
      @XMLResponseParser(ValueHandler.class)
      ListenableFuture<String> getQueueArn();
   }

   public void testAddAnonymousPermission() throws InterruptedException {
      for (URI queue : queues) {
         QueueAttributes attributes = api.getQueueApi().getAttributes(queue);
         assertNoPermissions(queue);

         String accountToAuthorize = getOwner(queue);
         api.getPermissionApiForQueue(queue).addPermissionToAccount("fubar", Action.GET_QUEUE_ATTRIBUTES,
               accountToAuthorize);

         String policyForAuthorizationByAccount = assertPolicyPresent(queue);

         String policyForAnonymous = policyForAuthorizationByAccount.replace("\"" + accountToAuthorize + "\"", "\"*\"");
         api.getQueueApi().setAttribute(queue, "Policy", policyForAnonymous);

         assertEquals(getAnonymousAttributesApi(queue).getQueueArn(), attributes.getQueueArn());
      }
   }

   @Test(dependsOnMethods = "testAddAnonymousPermission")
   public void testRemovePermission() throws InterruptedException {
      for (URI queue : queues) {
         api.getPermissionApiForQueue(queue).remove("fubar");
         assertNoPermissions(queue);
      }
   }

   private AnonymousAttributesApi getAnonymousAttributesApi(URI queue) {
      return ContextBuilder.newBuilder(
                  forClientMappedToAsyncClientOnEndpoint(AnonymousAttributesApi.class,
                        AnonymousAttributesAsyncApi.class, queue.toASCIIString()))
            .modules(ImmutableSet.<Module> of(new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor())))
            .buildApi(AnonymousAttributesApi.class);
   }

}
