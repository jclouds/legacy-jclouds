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
package org.jclouds.trmk.vcloud_0_8.xml.ovf;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.cim.VirtualSystemSettingData;
import org.jclouds.cim.xml.VirtualSystemSettingDataHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code SystemHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "SystemHandlerTest")
public class SystemHandlerTest extends BaseHandlerTest {

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/system.xml");

      VirtualSystemSettingData result = factory.create(injector.getInstance(VirtualSystemSettingDataHandler.class))
               .parse(is);
      assertEquals(result.getElementName(), "Virtual Hardware Family");
      assertEquals(result.getInstanceID(), "0");
      assertEquals(result.getVirtualSystemIdentifier(), "adriantest1");
      assertEquals(result.getVirtualSystemTypes(), ImmutableSet.of("vmx-07"));
   }
}
