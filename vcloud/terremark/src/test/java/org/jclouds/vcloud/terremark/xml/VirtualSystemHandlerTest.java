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
import org.jclouds.vcloud.terremark.domain.VirtualSystem;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code VirtualSystemHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VirtualSystemHandlerTest")
public class VirtualSystemHandlerTest extends BaseHandlerTest {

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/terremark/system.xml");

      VirtualSystem result = (VirtualSystem) factory.create(
               injector.getInstance(VirtualSystemHandler.class)).parse(is);
      assertEquals(result.getElementName(), "Virtual Hardware Family");
      assertEquals(result.getInstanceID(), 0);
      assertEquals(result.getVirtualSystemIdentifier(), "adriantest1");
      assertEquals(result.getVirtualSystemType(), "vmx-07");
   }
}
