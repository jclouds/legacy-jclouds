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
package org.jclouds.ec2.xml;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.InstanceStateChange;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code InstanceStateChangeHandler}
 * 
 * @author Adrian Cole
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "InstanceStateChangeHandlerTest")
public class InstanceStateChangeHandlerTest extends BaseEC2HandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void testTerminate() {

      InputStream is = getClass().getResourceAsStream("/terminate_instances.xml");

      Set<InstanceStateChange> expected = ImmutableSet.of(new InstanceStateChange(defaultRegion,
               "i-3ea74257", InstanceState.SHUTTING_DOWN, InstanceState.RUNNING));

      InstanceStateChangeHandler handler = injector.getInstance(InstanceStateChangeHandler.class);
      addDefaultRegionToHandler(handler);
      Set<InstanceStateChange> result = factory.create(handler).parse(is);
      assertEquals(result, expected);
   }

   public void testStart() {

      InputStream is = getClass().getResourceAsStream("/start_instances.xml");

      Set<InstanceStateChange> expected = ImmutableSet.of(new InstanceStateChange(defaultRegion,
               "i-10a64379", InstanceState.PENDING, InstanceState.STOPPED));
      InstanceStateChangeHandler handler = injector.getInstance(InstanceStateChangeHandler.class);
      addDefaultRegionToHandler(handler);
      Set<InstanceStateChange> result = factory.create(handler).parse(is);
      assertEquals(result, expected);
   }

   public void testStop() {

      InputStream is = getClass().getResourceAsStream("/stop_instances.xml");

      Set<InstanceStateChange> expected = ImmutableSet.of(new InstanceStateChange(defaultRegion,
               "i-10a64379", InstanceState.STOPPING, InstanceState.RUNNING));

      InstanceStateChangeHandler handler = injector.getInstance(InstanceStateChangeHandler.class);
      addDefaultRegionToHandler(handler);
      Set<InstanceStateChange> result = factory.create(handler).parse(is);
      assertEquals(result, expected);
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(ImmutableList.<Object>of()).atLeastOnce();
      replay(request);
      handler.setContext(request);
   }
}
