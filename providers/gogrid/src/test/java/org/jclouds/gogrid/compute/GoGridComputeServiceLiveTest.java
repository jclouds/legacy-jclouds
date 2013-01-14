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
package org.jclouds.gogrid.compute;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.gogrid.GoGridAsyncClient;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.predicates.ServerLatestJobCompleted;
import org.jclouds.rest.RestContext;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

/**
 * @author Oleksiy Yarmula
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "live", singleThreaded = true, testName = "GoGridComputeServiceLiveTest")
public class GoGridComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   public GoGridComputeServiceLiveTest() {
      provider = "gogrid";
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   // gogrid does not support metadata
   @Override
   protected void checkUserMetadataContains(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      assert node.getUserMetadata().equals(ImmutableMap.<String, String> of()) : String.format(
            "node userMetadata did not match %s %s", userMetadata, node);
   }
   
   protected void checkResponseEqualsHostname(ExecResponse execResponse, NodeMetadata node1) {
      // hostname is not predictable based on node metadata
   }

   public void testResizeRam() throws Exception {
      String group = this.group + "ram";
      RestContext<GoGridClient, GoGridAsyncClient> providerContext = view.unwrap();
      try {
         client.destroyNodesMatching(inGroup(group));
      } catch (Exception e) {

      }
      Predicate<Server> serverLatestJobCompleted = retry(new ServerLatestJobCompleted(providerContext.getApi()
            .getJobServices()), 800, 20, SECONDS);

      String ram = Iterables.get(providerContext.getApi().getServerServices().getRamSizes(), 1).getName();
      try {
         NodeMetadata node = getOnlyElement(client.createNodesInGroup(group, 1));

         Server updatedServer = providerContext.getApi().getServerServices().editServerRam(Long.valueOf(node.getId()), ram);
         assertNotNull(updatedServer);
         assert serverLatestJobCompleted.apply(updatedServer);

         assertEquals(
               Iterables.getLast(providerContext.getApi().getServerServices().getServersById(Long.valueOf(node.getId())))
                     .getRam().getName(), ram);

      } finally {
         client.destroyNodesMatching(inGroup(group));
      }
   }
}
