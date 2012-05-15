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

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.joyent.sdc.v6_5.config.SDCParserModule;
import org.jclouds.joyent.sdc.v6_5.domain.Dataset;
import org.jclouds.joyent.sdc.v6_5.domain.Type;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Gerald Pereira
 */
@Test(groups = "unit", testName = "ParseDatasetListTest")
public class ParseDatasetListTest extends BaseSetParserTest<Dataset> {

   @Override
   public String resource() {
      return "/dataset_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<Dataset> expected() {
      return ImmutableSet.of(
            Dataset.builder().id("e4cd7b9e-4330-11e1-81cf-3bb50a972bda").name("centos-6").urn("sdc:sdc:centos-6:1.0.1")
                  .type(Type.VIRTUALMACHINE).version("1.0.1").isDefault(false)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-02-13T06:30:33+00:00"))
                  .build(),

            Dataset.builder().id("e62c30b4-cdda-11e0-9dd4-af4d032032e3").name("nodejs").urn("sdc:sdc:nodejs:1.2.3")
                  .type(Type.SMARTMACHINE).version("1.2.3").isDefault(false)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-09-15T08:15:29+00:00"))
                  .build()

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
