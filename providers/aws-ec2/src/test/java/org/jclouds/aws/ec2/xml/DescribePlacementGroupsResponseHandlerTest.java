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
package org.jclouds.aws.ec2.xml;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.ec2.xml.BaseEC2HandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code DescribePlacementGroupsResponseHandler}
 * 
 * @author Adrian Cole
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DescribePlacementGroupsResponseHandlerTest")
public class DescribePlacementGroupsResponseHandlerTest extends BaseEC2HandlerTest {
   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/describe_placement_groups.xml");

      PlacementGroup expected = new PlacementGroup(defaultRegion, "XYZ-cluster", "cluster",
               PlacementGroup.State.AVAILABLE);
      DescribePlacementGroupsResponseHandler handler = injector
               .getInstance(DescribePlacementGroupsResponseHandler.class);
      addDefaultRegionToHandler(handler);
      PlacementGroup result = getOnlyElement(factory.create(handler).parse(is));

      assertEquals(result, expected);
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      handler.setContext(request);
   }
}
