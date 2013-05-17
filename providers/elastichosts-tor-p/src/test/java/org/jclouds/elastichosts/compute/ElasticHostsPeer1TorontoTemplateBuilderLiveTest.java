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
package org.jclouds.elastichosts.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

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
public class ElasticHostsPeer1TorontoTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {

   public ElasticHostsPeer1TorontoTemplateBuilderLiveTest() {
      provider = "elastichosts-tor-p";
   }

   @Override
   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
      return Predicates.not(new Predicate<OsFamilyVersion64Bit>() {

         @Override
         public boolean apply(OsFamilyVersion64Bit input) {
            switch (input.family) {
               case UBUNTU:
                  return (input.version.equals("") || ImmutableSet.of("11.10", "10.04").contains(input.version)) && input.is64Bit;
               case DEBIAN:
                  return (input.version.equals("") || input.version.equals("6.0")) && input.is64Bit;
               case CENTOS:
                  return (input.version.equals("") || input.version.equals("6.0")) && input.is64Bit;
               case WINDOWS:
                  return (input.version.equals("") || input.version.equals("2008 R2") || input.version.equals("2008"))
                           && input.is64Bit;
               default:
                  return false;
            }
         }

      });
   }

   @Test
   public void testTemplateBuilder() {
      Template defaultTemplate = this.view.getComputeService().templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "11.10");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(defaultTemplate.getLocation().getId(), "elastichosts-tor-p");
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
   }

   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.<String> of("CA-ON");
   }
}
