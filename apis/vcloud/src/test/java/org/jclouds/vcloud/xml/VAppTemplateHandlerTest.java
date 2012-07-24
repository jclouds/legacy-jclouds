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

import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.ovf.Network;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.GuestCustomizationSection;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.TaskStatus;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.domain.internal.TaskImpl;
import org.jclouds.vcloud.domain.ovf.VCloudNetworkSection;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code VAppTemplateHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class VAppTemplateHandlerTest {

   public void testUbuntuTemplate() {
      VAppTemplate result = parseTemplate();
      assertEquals(result.getName(), "Ubuntu Template");
      assertEquals(result.getHref(), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1201908921"));
      assertEquals(result.getType(), "application/vnd.vmware.vcloud.vAppTemplate+xml");
      assertEquals(result.getStatus(), Status.OFF);
      assertEquals(result.getVDC(), new ReferenceTypeImpl(null, VCloudMediaType.VDC_XML, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1014839439")));
      assertEquals(result.getDescription(), null);
      assertEquals(result.getTasks(), ImmutableList.of());
      assertEquals(result.getVAppScopedLocalId(), null);
      assert result.isOvfDescriptorUploaded();
      Vm vm = Iterables.getOnlyElement(result.getChildren());
      assertEquals(vm.getName(), "Ubuntu1004");
      assertEquals(vm.getHref(), URI.create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vm-172837194"));
      // NOTE this is vAppTemplate not VM!
      assertEquals(vm.getType(), "application/vnd.vmware.vcloud.vAppTemplate+xml");
      assertEquals(vm.getStatus(), null);
      assertEquals(vm.getParent(), new ReferenceTypeImpl(null, VCloudMediaType.VAPPTEMPLATE_XML, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1201908921")));
      assertEquals(vm.getDescription(), null);
      assertEquals(vm.getTasks(), ImmutableList.of());
      assertEquals(vm.getVAppScopedLocalId(), "02_ubuntu_template");

      GuestCustomizationSection guestC = vm.getGuestCustomizationSection();

      assertEquals(guestC.getType(), VCloudMediaType.GUESTCUSTOMIZATIONSECTION_XML);
      assertEquals(
               guestC.getHref(),
               URI
                        .create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vm-172837194/guestCustomizationSection/"));
      assertEquals(guestC.getInfo(), "Specifies Guest OS Customization Settings");
      assertEquals(guestC.isEnabled(), Boolean.TRUE);
      assertEquals(guestC.shouldChangeSid(), Boolean.FALSE);
      assertEquals(guestC.getVirtualMachineId(), "172837194");
      assertEquals(guestC.isJoinDomainEnabled(), Boolean.FALSE);
      assertEquals(guestC.useOrgSettings(), Boolean.FALSE);
      assertEquals(guestC.getDomainName(), null);
      assertEquals(guestC.getDomainUserName(), null);
      assertEquals(guestC.getDomainUserPassword(), null);
      assertEquals(guestC.isAdminPasswordEnabled(), Boolean.TRUE);
      assertEquals(guestC.isAdminPasswordAuto(), Boolean.TRUE);
      assertEquals(guestC.getAdminPassword(), "%3eD%gmF");
      assertEquals(guestC.isResetPasswordRequired(), Boolean.FALSE);
      assertEquals(
               guestC.getCustomizationScript(),
               "#!/bin/bash if [ \"$1\" = \"postcustomization\" ]; then echo \"post customization\" touch /root/.postcustomization sleep 30 #regenerate keys /bin/rm /etc/ssh/ssh_host_* /usr/sbin/dpkg-reconfigure openssh-server echo \"completed\" fi");
      assertEquals(guestC.getComputerName(), "Ubuntu1004");
      assertEquals(guestC.getEdit(), null);

      VCloudNetworkSection network = result.getNetworkSection();
      assertEquals(
               network.getHref(),
               URI
                        .create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1201908921/networkSection/"));
      assertEquals(network.getType(), VCloudMediaType.NETWORKSECTION_XML);
      assertEquals(network.getInfo(), "The list of logical networks");
      assertEquals(network.getNetworks(), ImmutableSet.of(new Network("vAppNet-vApp Internal", null)));

   }

   public static VAppTemplate parseTemplate() {
      InputStream is = VAppTemplateHandlerTest.class.getResourceAsStream("/vAppTemplate.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VAppTemplate result = factory.create(injector.getInstance(VAppTemplateHandler.class)).parse(is);
      return result;
   }

   public void testCopyingTemplate() {
      InputStream is = getClass().getResourceAsStream("/vAppTemplate-copying.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      DateService dateService = injector.getInstance(DateService.class);

      VAppTemplate result = factory.create(injector.getInstance(VAppTemplateHandler.class)).parse(is);
      assertEquals(result.getName(), "Ubuntu10.04_v2");
      assertEquals(result.getHref(), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-699683881"));
      assertEquals(result.getType(), "application/vnd.vmware.vcloud.vAppTemplate+xml");
      assertEquals(result.getStatus(), Status.UNRESOLVED);
      assertEquals(result.getVDC(), new ReferenceTypeImpl(null, VCloudMediaType.VDC_XML, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/105186609")));
      assertEquals(result.getDescription(), null);
      assertEquals(result.getTasks(), ImmutableList.of(new TaskImpl(URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/task/q62gxhi32xgd9yrqvr"),
               "Copying Virtual Application Template Ubuntu10.04_v2(699683881)", TaskStatus.RUNNING, dateService
                        .iso8601DateParse("2010-09-17T23:20:46.039-04:00"), dateService
                        .iso8601DateParse("9999-12-31T23:59:59.999-05:00"), dateService
                        .iso8601DateParse("2010-12-16T23:20:46.039-05:00"), new ReferenceTypeImpl("Ubuntu10.04_v2",
                        "application/vnd.vmware.vcloud.vAppTemplate+xml",
                        URI.create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-699683881")),
               null)));
      assertEquals(result.getVAppScopedLocalId(), null);
      assert result.isOvfDescriptorUploaded();
      assertEquals(result.getChildren(), ImmutableList.of());
      assertEquals(result.getNetworkSection(), null);

   }
   
   public void testVAppTemplateWithNewlinesAndNamespacedElements() {
      InputStream is = getClass().getResourceAsStream("/vAppTemplate1.0-vcd15_withNewlines.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);

      factory.create(injector.getInstance(VAppTemplateHandler.class)).parse(is);
   }
   
   
}
