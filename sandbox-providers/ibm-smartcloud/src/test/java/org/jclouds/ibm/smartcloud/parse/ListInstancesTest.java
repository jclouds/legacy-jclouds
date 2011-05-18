/**
 *
 * Copyright (C) 2011 Cloud Conscious) LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License) Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing) software
 * distributed under the License is distributed on an "AS IS" BASIS)
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND) either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ibm.smartcloud.parse;

import java.util.Date;
import java.util.Set;

import org.jclouds.ibm.smartcloud.config.IBMSmartCloudParserModule;
import org.jclouds.ibm.smartcloud.domain.IP;
import org.jclouds.ibm.smartcloud.domain.Instance;
import org.jclouds.ibm.smartcloud.domain.Instance.Software;
import org.jclouds.ibm.smartcloud.domain.Instance.Status;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.Unwrap;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ListInstancesTest")
public class ListInstancesTest extends BaseSetParserTest<Instance> {

   protected Injector injector() {
      return Guice.createInjector(new GsonModule(), new IBMSmartCloudParserModule());
   }

   @Override
   public String resource() {
      return "/instances.json";
   }

   @Override
   @Unwrap
   public Set<Instance> expected() {

      return ImmutableSet.of(Instance.builder().launchTime(new Date(1305351683883l)).software(
               ImmutableSet.of(new Software("SUSE Linux Enterprise Server", "OS", "11 SP1"))).primaryIP(
               new IP("                ", "                ", 0)).requestId("80890").keyName("adriancole").name("adriancole").instanceType(
               "COP32.1/2048/60").status(Status.NEW).owner("adrian@cloudconscious.com").location("101").imageId(
               "20015393").rootOnly(true).requestName("adriancole").id("80590")
               .expirationTime(new Date(1368423692824l)).diskSize(60).build());
   }

}
