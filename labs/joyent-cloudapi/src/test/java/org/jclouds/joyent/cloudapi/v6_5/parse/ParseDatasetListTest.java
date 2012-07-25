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
package org.jclouds.joyent.cloudapi.v6_5.parse;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.joyent.cloudapi.v6_5.domain.Dataset;
import org.jclouds.joyent.cloudapi.v6_5.domain.Machine.Type;
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
        Dataset.builder()
               .id("71101322-43a5-11e1-8f01-cf2a3031a7f4")
               .urn("sdc:sdc:ubuntu-10.04:1.0.1")
               .name("ubuntu-10.04")
               .os("linux")
               .type(Type.VIRTUALMACHINE)
               .description("Ubuntu 10.04 VM 1.0.1")
               .version("1.0.1")
               .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-02-22T18:27:32+00:00"))
               .build(),
               
        Dataset.builder()
               .id("e4cd7b9e-4330-11e1-81cf-3bb50a972bda")
               .urn("sdc:sdc:centos-6:1.0.1")
               .name("centos-6")
               .os("linux")
               .type(Type.VIRTUALMACHINE)
               .description("Centos 6 VM 1.0.1")
               .version("1.0.1")
               .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-02-15T20:04:18+00:00"))
               .build(),
  
        Dataset.builder()
               .id("9551fdbc-cc9a-11e1-a9e7-eb1e788a8690")
               .urn("sdc:sdc:standard64:1.0.1")
               .name("standard64")
               .os("smartos")
               .type(Type.SMARTMACHINE)
               .description("64-bit machine image optimized for web development")
               .version("1.0.1")
               .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-07-13T03:30:22+00:00"))
               .build()
        );
   }

   protected Injector injector() {
      return Guice.createInjector(new GsonModule() {

         @Override
         protected void configure() {
            bind(DateAdapter.class).to(Iso8601DateAdapter.class);
            super.configure();
         }

      });
   }
}
