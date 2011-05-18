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
import org.jclouds.ibm.smartcloud.domain.Volume;
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
@Test(groups = "unit", testName = "ListVolumesTest")
public class ListVolumesTest extends BaseSetParserTest<Volume> {
   protected Injector injector() {
      return Guice.createInjector(new GsonModule(), new IBMSmartCloudParserModule());
   }

   @Override
   public String resource() {
      return "/volumes.json";
   }

   @Override
   @Unwrap
   public Set<Volume> expected() {
      return ImmutableSet.of(new Volume("2", 5, 50, "aadelucc@us.ibm.com", new Date(1260469075119l), "1", ImmutableSet
               .<String> of(), "ext3", "New Storage", "67"), new Volume(null, 6, 51, "aadelucc@us.ibm.com", new Date(
               1260469075120l), "2", ImmutableSet.<String> of("abrad"), "ext3", "New Storage1", "68"));
   }
}
