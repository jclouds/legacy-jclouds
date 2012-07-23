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
package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.GuestCustomizationSection;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code GuestCustomizationSectionHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GuestCustomizationSectionHandlerTest")
public class GuestCustomizationSectionHandlerTest extends BaseHandlerTest {

   public void testDefault() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/guestCustomization.xml");

      GuestCustomizationSection result = factory.create(injector.getInstance(GuestCustomizationSectionHandler.class))
            .parse(is);

      checkGuestCustomization(result);

   }

   @Test(enabled = false)
   public static void checkGuestCustomization(GuestCustomizationSection result) {
      assertEquals(result.getType(), VCloudMediaType.GUESTCUSTOMIZATIONSECTION_XML);
      assertEquals(result.getHref(),
            URI.create("https://vcenterprise.bluelock.com/api/v1.0/vApp/vm-2087535248/guestCustomizationSection/"));
      assertEquals(result.getInfo(), "Specifies Guest OS Customization Settings");
      assertEquals(result.isEnabled(), Boolean.TRUE);
      assertEquals(result.shouldChangeSid(), Boolean.FALSE);
      assertEquals(result.getVirtualMachineId(), "2087535248");
      assertEquals(result.isJoinDomainEnabled(), Boolean.FALSE);
      assertEquals(result.useOrgSettings(), Boolean.FALSE);
      assertEquals(result.getDomainName(), null);
      assertEquals(result.getDomainUserName(), null);
      assertEquals(result.getDomainUserPassword(), null);
      assertEquals(result.isAdminPasswordEnabled(), Boolean.TRUE);
      assertEquals(result.isAdminPasswordAuto(), Boolean.TRUE);
      assertEquals(result.getAdminPassword(), null);
      assertEquals(result.isResetPasswordRequired(), Boolean.FALSE);
      assertEquals(result.getCustomizationScript(), "cat > /root/foo.txt<<EOF\nI '\"love\"' {asc|!}*&\nEOF\n");
      assertEquals(result.getComputerName(), "RHEL5");
      assertEquals(
            result.getEdit(),
            new ReferenceTypeImpl(null, VCloudMediaType.GUESTCUSTOMIZATIONSECTION_XML, URI
                  .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/vm-2087535248/guestCustomizationSection/")));
   }
}
