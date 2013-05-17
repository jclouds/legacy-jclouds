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
package org.jclouds.trmk.ecloud;

import java.util.Properties;

import org.jclouds.trmk.vcloud_0_8.internal.BaseTerremarkClientLiveTest;
import org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true)
public class BaseTerremarkECloudClientLiveTest extends BaseTerremarkClientLiveTest {
   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.setProperty(VCloudConstants.PROPERTY_VCLOUD_DEFAULT_VDC,
            ".* - " + System.getProperty("test.trmk-ecloud.datacenter", "MIA"));
      return props;
   }

   protected TerremarkECloudClient api() {
      return TerremarkECloudClient.class.cast(api);
   }
}
