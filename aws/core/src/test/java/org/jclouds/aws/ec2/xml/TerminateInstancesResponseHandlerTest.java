/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2.xml;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.domain.TerminatedInstance;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code TerminateInstancesResponseHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.TerminateInstancesResponseHandlerTest")
public class TerminateInstancesResponseHandlerTest extends BaseHandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream("/ec2/terminate_instances.xml");

      Set<TerminatedInstance> expected = ImmutableSet.of(new TerminatedInstance(Region.DEFAULT,
               "i-3ea74257", InstanceState.SHUTTING_DOWN, InstanceState.RUNNING));

      TerminateInstancesResponseHandler handler = injector
               .getInstance(TerminateInstancesResponseHandler.class);
      addDefaultRegionToHandler(handler);
      Set<TerminatedInstance> result = factory.create(handler).parse(is);
      assertEquals(result, expected);
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(new Object[] { Region.DEFAULT }).atLeastOnce();
      replay(request);
      handler.setContext(request);
   }
}
