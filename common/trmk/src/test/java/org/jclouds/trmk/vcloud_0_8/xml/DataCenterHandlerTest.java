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
package org.jclouds.trmk.vcloud_0_8.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.trmk.vcloud_0_8.domain.DataCenter;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code DataCenterHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "DataCenterHandlerTest")
public class DataCenterHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/datacenter.xml");

      DataCenter result = factory.create(injector.getInstance(DataCenterHandler.class)).parse(is);

      DataCenter expects = DataCenter.builder().id("1").name("Terremark - Miami").code("MIA").build();
      assertEquals(result.toString(), expects.toString());
   }
}
