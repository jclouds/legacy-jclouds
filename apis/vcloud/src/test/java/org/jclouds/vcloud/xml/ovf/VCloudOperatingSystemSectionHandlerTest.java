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
package org.jclouds.vcloud.xml.ovf;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.domain.ovf.VCloudOperatingSystemSection;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code VCloudOperatingSystemSectionHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "VCloudOperatingSystemSectionHandlerTest")
public class VCloudOperatingSystemSectionHandlerTest extends BaseHandlerTest {

   public void testDefault() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/os.xml");

      VCloudOperatingSystemSection result = factory.create(injector.getInstance(VCloudOperatingSystemHandler.class)).parse(is);

      checkOs(result);

   }

   @Test(enabled = false)
   public static void checkOs(VCloudOperatingSystemSection result) {
      assertEquals(result.getHref(), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/vm-2087535248/operatingSystemSection/"));
      assertEquals(result.getDescription(), "Red Hat Enterprise Linux 5 (64-bit)");
      assertEquals(result.getEdit(), new ReferenceTypeImpl(null,
               "application/vnd.vmware.vcloud.operatingSystemSection+xml",
               URI.create("https://vcenterprise.bluelock.com/api/v1.0/vApp/vm-2087535248/operatingSystemSection/")));
      assertEquals(result.getId(), new Integer(80));
      assertEquals(result.getVmwOsType(), "rhel5_64Guest");
      assertEquals(result.getType(), "application/vnd.vmware.vcloud.operatingSystemSection+xml");
      assertEquals(result.getInfo(), "Specifies the operating system installed");
   }
}
