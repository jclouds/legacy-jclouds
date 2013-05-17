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
package org.jclouds.compute.predicates;

import static org.jclouds.compute.predicates.OperatingSystemPredicates.supportsApt;
import static org.jclouds.compute.predicates.OperatingSystemPredicates.supportsYum;
import static org.jclouds.compute.predicates.OperatingSystemPredicates.supportsZypper;

import org.jclouds.cim.OSType;
import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.testng.annotations.Test;

/**
 * Tests possible uses of OperatingSystemPredicates
 * 
 * @author Adrian Cole
 */
@Test
public class OperatingSystemPredicatesTest {

   public void testCIMCENTOSDoesntSupportApt() {
      assert !supportsApt().apply(new CIMOperatingSystem(OSType.CENTOS, "", null, "description"));
      assert !supportsApt().apply(new CIMOperatingSystem(OSType.CENTOS_64, "", null, "description"));
   }

   public void testCIMRHELDoesntSupportApt() {
      assert !supportsApt().apply(new CIMOperatingSystem(OSType.RHEL, "", null, "description"));
      assert !supportsApt().apply(new CIMOperatingSystem(OSType.RHEL_64, "", null, "description"));
   }

   public void testCIMDEBIANSupportsApt() {
      assert supportsApt().apply(new CIMOperatingSystem(OSType.DEBIAN, "", null, "description"));
      assert supportsApt().apply(new CIMOperatingSystem(OSType.DEBIAN_64, "", null, "description"));
   }

   public void testCIMUBUNTUSupportsApt() {
      assert supportsApt().apply(new CIMOperatingSystem(OSType.UBUNTU, "", null, "description"));
      assert supportsApt().apply(new CIMOperatingSystem(OSType.UBUNTU_64, "", null, "description"));
   }

   public void testUbuntuNameSupportsApt() {
      assert supportsApt().apply(new OperatingSystem(null, "Ubuntu", "", null, "description", false));
   }

   public void testCIMCENTOSSupportsYum() {
      assert supportsYum().apply(new CIMOperatingSystem(OSType.CENTOS, "", null, "description"));
      assert supportsYum().apply(new CIMOperatingSystem(OSType.CENTOS_64, "", null, "description"));
   }

   public void testCIMRHELSupportsYum() {
      assert supportsYum().apply(new CIMOperatingSystem(OSType.RHEL, "", null, "description"));
      assert supportsYum().apply(new CIMOperatingSystem(OSType.RHEL_64, "", null, "description"));
   }

   public void testCIMDEBIANDoesntSupportYum() {
      assert !supportsYum().apply(new CIMOperatingSystem(OSType.DEBIAN, "", null, "description"));
      assert !supportsYum().apply(new CIMOperatingSystem(OSType.DEBIAN_64, "", null, "description"));
   }

   public void testCIMUBUNTUDoesntSupportYum() {
      assert !supportsYum().apply(new CIMOperatingSystem(OSType.UBUNTU, "", null, "description"));
      assert !supportsYum().apply(new CIMOperatingSystem(OSType.UBUNTU_64, "", null, "description"));
   }

   public void testSuseTypeSupportsZypper() {
      assert supportsZypper().apply(new OperatingSystem(OsFamily.SUSE, null, "", null, "description", false));
   }

   public void testSuseDescriptionSupportsZypper() {
      assert supportsZypper().apply(new OperatingSystem(null, "", null, null, "Suse", false));
   }

   public void testSuseNameSupportsZypper() {
      assert supportsZypper().apply(new OperatingSystem(null, "Suse", "", null, "description", false));
   }

   public void testCentosTypeSupportsYum() {
      assert supportsYum().apply(new OperatingSystem(OsFamily.CENTOS, null, "", null, "description", false));
   }

   public void testAmzTypeSupportsYum() {
      assert supportsYum().apply(new OperatingSystem(OsFamily.AMZN_LINUX, null, "", null, "description", false));
   }

   public void testRhelTypeSupportsYum() {
      assert supportsYum().apply(new OperatingSystem(OsFamily.RHEL, null, "", null, "description", false));
   }

   public void testFedoraTypeSupportsYum() {
      assert supportsYum().apply(new OperatingSystem(OsFamily.FEDORA, null, "", null, "description", false));
   }

   public void testCentosNameSupportsYum() {
      assert supportsYum().apply(new OperatingSystem(null, "Centos", "", null, "description", false));
   }

   public void testRhelNameSupportsYum() {
      assert supportsYum().apply(new OperatingSystem(null, "RHEL", "", null, "description", false));
   }

   public void testFedoraNameSupportsYum() {
      assert supportsYum().apply(new OperatingSystem(null, "Fedora", "", null, "description", false));
   }

   public void testRedHatEnterpriseLinuxNameSupportsYum() {
      assert supportsYum().apply(new OperatingSystem(null, "Red Hat Enterprise Linux", "", null, "description", false));
   }

   public void testCentosDescriptionSupportsYum() {
      assert supportsYum().apply(new OperatingSystem(null, "", null, null, "Centos", false));
   }

   public void testRhelDescriptionSupportsYum() {
      assert supportsYum().apply(new OperatingSystem(null, "", null, null, "RHEL", false));
   }

   public void testFedoraDescriptionSupportsYum() {
      assert supportsYum().apply(new OperatingSystem(null, "", null, null, "Fedora", false));
   }

   public void testRedHatEnterpriseLinuxDescriptionSupportsYum() {
      assert supportsYum().apply(new OperatingSystem(null, "", null, null, "Red Hat Enterprise Linux", false));
   }
}
