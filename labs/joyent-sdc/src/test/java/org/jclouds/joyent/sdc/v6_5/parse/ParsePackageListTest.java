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
package org.jclouds.joyent.sdc.v6_5.parse;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.joyent.sdc.v6_5.config.SDCParserModule;
import org.jclouds.joyent.sdc.v6_5.domain.Package;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Gerald Pereira
 */
@Test(groups = "unit", testName = "ParsePackageListTest")
public class ParsePackageListTest extends BaseSetParserTest<Package> {

   @Override
   public String resource() {
      return "/package_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<Package> expected() {
      return ImmutableSet.of(org.jclouds.joyent.sdc.v6_5.domain.Package.builder().name("Small 1GB").memorySizeMb(1024)
            .diskSizeGb(30720).swapSizeMb(2048).isDefault(true).build(),

      org.jclouds.joyent.sdc.v6_5.domain.Package.builder().name("Medium 2GB").memorySizeMb(2048).diskSizeGb(61440)
            .swapSizeMb(4096).isDefault(false).build()

      );
   }

   protected Injector injector() {
      return Guice.createInjector(new SDCParserModule(), new GsonModule() {

         @Override
         protected void configure() {
            bind(DateAdapter.class).to(Iso8601DateAdapter.class);
            super.configure();
         }

      });
   }
}
