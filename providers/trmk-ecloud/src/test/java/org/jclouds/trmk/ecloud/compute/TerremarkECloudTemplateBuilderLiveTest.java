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
package org.jclouds.trmk.ecloud.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.OsFamilyVersion64Bit;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseTemplateBuilderLiveTest;
import org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "TerremarkECloudTemplateBuilderLiveTest")
public class TerremarkECloudTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {
   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.setProperty(VCloudConstants.PROPERTY_VCLOUD_DEFAULT_VDC,
            ".* - " + System.getProperty("test.trmk-ecloud.datacenter", "MIA"));
      return props;
   }

   public TerremarkECloudTemplateBuilderLiveTest() {
      provider = "trmk-ecloud";
   }

   @Override
   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
      return new Predicate<OsFamilyVersion64Bit>() {

         @Override
         public boolean apply(OsFamilyVersion64Bit input) {
            switch (input.family) {
            case RHEL:
               return !input.version.equals("") && !input.version.matches("5.[50]");
            case SOLARIS:
               return !input.is64Bit;
            case CENTOS:
               return !input.version.equals("") && !input.version.matches("5.[50]");
            case UBUNTU:
               return !input.version.equals("") && !input.version.equals("10.04") && !input.version.equals("8.04");
            case WINDOWS:
               return !input.version.equals("") && !input.version.equals("2003 R2") //
                     && !(input.version.equals("2008") && !input.is64Bit) //
                     && !(input.version.matches("2008( R2)?") && input.is64Bit);
            default:
               return true;
            }
         }
      };
   }

   @Test
   public void testDefaultTemplateBuilder() throws IOException {
      Template defaultTemplate = view.getComputeService().templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "10.04");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
   }

   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.<String> of("US-FL", "US-VA", "NL-NH", "BR-SP");
   }
}
