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
package org.jclouds.ninefold.compute.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.OsFamilyVersion64Bit;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseTemplateBuilderLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class NinefoldComputeTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {

   public NinefoldComputeTemplateBuilderLiveTest() {
      provider = "ninefold-compute";
   }

   @Override
   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
      return Predicates.not(new Predicate<OsFamilyVersion64Bit>() {

         @Override
         public boolean apply(OsFamilyVersion64Bit input) {
            switch (input.family) {
               case UBUNTU:
                  return input.version.equals("") || input.version.equals("10.04");
               case SUSE:
                  return (input.version.equals("") || input.version.equals("11")) && input.is64Bit;
               case DEBIAN:
                  return (input.version.equals("") || input.version.equals("6.0")) && !input.is64Bit;
               case CENTOS:
                  return (input.version.equals("") || input.version.equals("5.5")) && input.is64Bit;
               case WINDOWS:
                  return input.version.equals("") || (input.version.equals("2008") && !input.is64Bit);
               default:
                  return false;
            }
         }

      });
   }

   @Test
   public void testDefaultTemplateBuilder() throws IOException {
      Template defaultTemplate = this.view.getComputeService().templateBuilder().build();
      if (template == null) {
         assert defaultTemplate.getImage().getOperatingSystem().getVersion().matches("1[012].[10][04]") : defaultTemplate
                  .getImage().getOperatingSystem().getVersion();
         assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
         assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
         assertEquals(defaultTemplate.getLocation().getId(), "1");
         assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
         assertEquals(defaultTemplate.getOptions().getLoginUser(), "user");
         assertEquals(defaultTemplate.getOptions().getLoginPassword(), "Password01");
         assertEquals(defaultTemplate.getOptions().getLoginPrivateKey(), null);
         assertEquals(defaultTemplate.getOptions().shouldAuthenticateSudo(), Boolean.TRUE);
      } else {
         assertEquals(defaultTemplate.getImage(), this.view.getComputeService().templateBuilder().from(template)
                  .build().getImage());
      }
   }

   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.<String> of("AU-NSW");
   }
}
