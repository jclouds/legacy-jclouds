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
package org.jclouds.epc.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.OsFamilyVersion64Bit;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.LocationScope;
import org.jclouds.ec2.compute.EC2TemplateBuilderLiveTest;
import org.jclouds.ec2.compute.util.EC2ComputeUtils;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class EucalyptusPartnerCloudEucalyptusTemplateBuilderLiveTest extends EC2TemplateBuilderLiveTest {

   public EucalyptusPartnerCloudEucalyptusTemplateBuilderLiveTest() {
      provider = "eucalyptus-partnercloud-ec2";
   }

   /**
    * Note, assertion fails on eucalyptus-partner-cloud, taking approx  timeByImageId=541; timeByOsFamily=277ms.
    * However, testTemplateBuilderCanUseImageIdWithoutFetchingAllImages and inspection of the debug logs shows
    * that we are submitting the HTTP request for a single image, e.g:
    *     Action=DescribeImages&ImageId.1=emi-E0641459&Signature=YihCSyPfIAvGa6ZoJSeQtXVXBJ6zfikspJUxYoIXXh4%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2011-12-13T11%3A21%3A51.455Z&Version=2010-06-15&AWSAccessKeyId=NB0zdTG4CtdvijzOFj47W0nlyl4cBzcfPw
    * <p>
    * Therefore disabled here.
    */
   @Override
   @Test(enabled = false)
   public void testTemplateBuildsFasterByImageIdThanBySearchingAllImages() throws Exception {
      super.testTemplateBuildsFasterByImageIdThanBySearchingAllImages();
   }

   @Override
   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
      return Predicates.not(new Predicate<OsFamilyVersion64Bit>() {

         @Override
         public boolean apply(OsFamilyVersion64Bit input) {
            switch (input.family) {
               case UBUNTU:
                  return (input.version.equals("") || input.version.equals("10.04")) && input.is64Bit;
               case DEBIAN:
                  return (input.version.equals("") || input.version.equals("6.0")) && input.is64Bit;
               case CENTOS:
               return (input.version.equals("") || input.version.equals("5.0") || input.version.equals("5.5"))
                     && input.is64Bit;
               default:
                  return false;
            }
         }

      });
   }

   @Test
   public void testDefaultTemplateBuilder() throws IOException {
      Template defaultTemplate = context.getComputeService().templateBuilder().build();
      assert (defaultTemplate.getImage().getProviderId().startsWith("emi-")) : defaultTemplate;
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "10.04");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(defaultTemplate.getImage().getUserMetadata().get("rootDeviceType"), "instance-store");
      assertEquals(defaultTemplate.getHardware().getId(), "m1.small");
      assertEquals(defaultTemplate.getLocation().getId(), "kvm-cluster");
      assertEquals(defaultTemplate.getLocation().getScope(), LocationScope.ZONE);
      assertEquals(AWSUtils.getRegionFromLocationOrNull(defaultTemplate.getLocation()), "Eucalyptus");
      assertEquals(EC2ComputeUtils.getZoneFromLocationOrNull(defaultTemplate.getLocation()), "kvm-cluster");
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
   }

   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.<String> of("US-CA");
   }
}
