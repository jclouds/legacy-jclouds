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
package org.jclouds.vcloud.terremark.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.ResourceType;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ResourceAllocationHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.ResourceAllocationHandlerTest")
public class ResourceAllocationHandlerTest extends BaseHandlerTest {

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/terremark/item.xml");

      ResourceAllocation result = (ResourceAllocation) factory.create(
               injector.getInstance(ResourceAllocationHandler.class)).parse(is);
      assertEquals(result.getAddress(), new Integer(0));
      assertEquals(result.getDescription(), "SCSI Controller");
      assertEquals(result.getName(), "SCSI Controller 0");
      assertEquals(result.getId(), 3);
      assertEquals(result.getSubType(), "lsilogic");
      assertEquals(result.getType(), ResourceType.SCSI_CONTROLLER);
      assertEquals(result.getVirtualQuantity(), 1);

   }
}
