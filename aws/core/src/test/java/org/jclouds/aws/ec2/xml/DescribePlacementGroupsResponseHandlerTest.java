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
package org.jclouds.aws.ec2.xml;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code DescribePlacementGroupsResponseHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.DescribePlacementGroupsResponseHandlerTest")
public class DescribePlacementGroupsResponseHandlerTest extends BaseEC2HandlerTest {
   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/ec2/describe_placement_groups.xml");

      PlacementGroup expected = new PlacementGroup(defaultRegion, "XYZ-cluster", "cluster",
               PlacementGroup.State.AVAILABLE);
      DescribePlacementGroupsResponseHandler handler = injector
               .getInstance(DescribePlacementGroupsResponseHandler.class);
      addDefaultRegionToHandler(handler);
      PlacementGroup result = Iterables.getOnlyElement(factory.create(handler).parse(is));

      assertEquals(result, expected);
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(new Object[] { null });
      replay(request);
      handler.setContext(request);
   }
}