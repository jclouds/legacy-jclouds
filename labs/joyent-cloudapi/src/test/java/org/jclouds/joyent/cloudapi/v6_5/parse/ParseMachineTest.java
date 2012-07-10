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
import org.jclouds.domain.JsonBall;
import org.jclouds.joyent.cloudapi.v6_5.config.JoyentCloudParserModule;
import org.jclouds.joyent.cloudapi.v6_5.domain.Machine;
import org.jclouds.joyent.cloudapi.v6_5.domain.Type;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Gerald Pereira
 */
@Test(groups = "unit", testName = "ParseMachineTest")
public class ParseMachineTest extends BaseItemParserTest<Machine> {

   @Override
   public String resource() {
      return "/machine.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Machine expected() {
      return Machine
            .builder()
            .id("94eba336-ecb7-49f5-8a27-52f5e4dd57a1")
            .name("sample-e92")
            .type(Type.VIRTUALMACHINE)
            .state(Machine.State.STOPPED)
            .dataset("sdc:sdc:centos-5.7:1.2.1")
            .ips(ImmutableSet.<String> builder().add("37.153.96.62").add("10.224.0.63").build())
            .memorySizeMb(1024)
            .diskSizeGb(61440)
            .metadata(
                  ImmutableMap.<String, JsonBall> builder()
                        .put("root_authorized_keys", new JsonBall("ssh-rsa XXXXXX== test@xxxx.ovh.net\n")).build())
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-05-09T13:32:46+00:00"))
            .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-05-11T08:44:53+00:00")).build();
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
