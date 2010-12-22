/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.ec2.compute;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Provider;

import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.services.PlacementGroupClient;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class EC2ComputeServiceTest {

   @SuppressWarnings({ "unchecked" })
   public void testUnsupportedOperationOkForPlacementGroups() {
      EC2Client client = createMock(EC2Client.class);
      EC2ComputeService service = new EC2ComputeService(createMock(ComputeServiceContext.class), createMock(Map.class),
            createMock(Supplier.class), createMock(Supplier.class), createMock(Supplier.class),
            createMock(ListNodesStrategy.class), createMock(GetNodeMetadataStrategy.class),
            createMock(RunNodesAndAddToSetStrategy.class), createMock(RebootNodeStrategy.class),
            createMock(DestroyNodeStrategy.class), createMock(ResumeNodeStrategy.class),
            createMock(SuspendNodeStrategy.class), createMock(Provider.class), createMock(Provider.class),
            createMock(Predicate.class), createMock(Predicate.class), createMock(Predicate.class),
            createMock(ComputeUtils.class), createMock(Timeouts.class), createMock(ExecutorService.class), client,
            createMock(Map.class), createMock(Map.class), createMock(Map.class), createMock(Predicate.class));

      PlacementGroupClient placementClient = createMock(PlacementGroupClient.class);

      // setup expectations
      expect(client.getPlacementGroupServices()).andReturn(placementClient).atLeastOnce();
      expect(placementClient.describePlacementGroupsInRegion("us-west-1", "jclouds#tag#us-west-1")).andThrow(
            new UnsupportedOperationException());

      // replay mocks
      replay(client);
      replay(placementClient);
      // run
      service.deletePlacementGroup("us-west-1", "tag");

      // verify mocks
      verify(client);
      verify(placementClient);

   }
}
