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

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.joyent.cloudapi.v6_5.config.JoyentCloudParserModule;
import org.jclouds.joyent.cloudapi.v6_5.domain.Dataset;
import org.jclouds.joyent.cloudapi.v6_5.domain.Type;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Gerald Pereira
 */
@Test(groups = "unit", testName = "ParseDatasetTest")
public class ParseDatasetTest extends BaseItemParserTest<Dataset> {

   @Override
   public String resource() {
      return "/dataset.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Dataset expected() {
      return Dataset.builder().id("e4cd7b9e-4330-11e1-81cf-3bb50a972bda").name("centos-6")
            .urn("sdc:sdc:centos-6:1.0.1").type(Type.VIRTUALMACHINE).version("1.0.1").isDefault(false)
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-02-13T06:30:33+00:00")).build();
   }

   protected Injector injector() {
      return Guice.createInjector(new JoyentCloudParserModule(), new GsonModule() {

         @Override
         protected void configure() {
            bind(DateAdapter.class).to(Iso8601DateAdapter.class);
            super.configure();
         }

      });
   }
}
