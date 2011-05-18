/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ibm.smartcloud.parse;

import java.util.Date;
import java.util.Set;

import org.jclouds.ibm.smartcloud.config.IBMSmartCloudParserModule;
import org.jclouds.ibm.smartcloud.domain.Instance;
import org.jclouds.ibm.smartcloud.domain.Instance.Software;
import org.jclouds.ibm.smartcloud.domain.Instance.Status;
import org.jclouds.json.BaseItemParserTest;
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
@Test(groups = "unit", testName = "GetInstanceTest")
public class NewInstanceTest extends BaseItemParserTest<Instance> {

   protected Injector injector() {
      return Guice.createInjector(new GsonModule(), new IBMSmartCloudParserModule());
   }

   @Override
   public String resource() {
      return "/new_instance.json";
   }

   @Override
   @Unwrap(depth = 2, edgeCollection = Set.class)
   public Instance expected() {
      return Instance.builder().launchTime(new Date(1305359078796l)).software(
               ImmutableSet.of(new Software("SUSE Linux Enterprise Server", "OS", "11 SP1"))).requestId("80904")
               .keyName("adriancole").name("adriancole").instanceType("COP32.1/2048/60").status(Status.NEW).owner(
                        "adrian@cloudconscious.com").location("101").imageId("20015393").requestName("adriancole").id(
                        "80604").expirationTime(new Date(1368431087355l)).build();
   }
}
