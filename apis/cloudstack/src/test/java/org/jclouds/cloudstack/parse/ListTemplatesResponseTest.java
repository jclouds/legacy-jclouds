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
package org.jclouds.cloudstack.parse;

import java.util.Set;

import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.Template.Format;
import org.jclouds.cloudstack.domain.Template.Type;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListTemplatesResponseTest extends BaseSetParserTest<Template> {

   @Override
   public String resource() {
      // grep listtemplatesresponse ./target/test-data/jclouds-wire.log|tail
      // -1|sed -e 's/.*<< "//g' -e 's/"$//g'
      return "/listtemplatesresponse.json";
   }

   @Override
   @SelectJson("template")
   public Set<Template> expected() {
      return ImmutableSet.of(
            Template.builder().id("2").name("CentOS 5.3(64-bit) no GUI (XenServer)")
                  .displayText("CentOS 5.3(64-bit) no GUI (XenServer)").isPublic(true)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-03-20T19:17:48-0700"))
                  .ready(false).passwordEnabled(false).format(Format.VHD).featured(true).crossZones(true).OSTypeId("11")
                  .OSType("CentOS 5.3 (32-bit)").account("system").zoneId("2").zone("Chicago").type(Type.BUILTIN)
                  .hypervisor("XenServer").domain("ROOT").domainId("1").extractable(true).build(),
            Template.builder().id("4").name("CentOS 5.5(64-bit) no GUI (KVM)")
                  .displayText("CentOS 5.5(64-bit) no GUI (KVM)").isPublic(true)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-03-20T19:17:48-0700"))
                  .ready(true).passwordEnabled(false).format(Format.QCOW2).featured(true).crossZones(true)
                  .OSTypeId("112").OSType("CentOS 5.5 (64-bit)").account("system").zoneId("2").zone("Chicago")
                  .size(8589934592l).type(Type.BUILTIN).hypervisor("KVM").domain("ROOT").domainId("1").extractable(true)
                  .build(),
            Template.builder().id("203").name("Windows 7 KVM").displayText("Windows 7 KVM").isPublic(true)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-03-20T22:02:18-0700"))
                  .ready(true).passwordEnabled(false).format(Format.QCOW2).featured(true).crossZones(false)
                  .OSTypeId("48").OSType("Windows 7 (32-bit)").account("admin").zoneId("2").zone("Chicago")
                  .size(17179869184l).type(Type.USER).hypervisor("KVM").domain("ROOT").domainId("1").extractable(false)
                  .build(),
            Template.builder().id("7").name("CentOS 5.3(64-bit) no GUI (vSphere)")
                  .displayText("CentOS 5.3(64-bit) no GUI (vSphere)").isPublic(true)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-03-20T19:17:48-0700"))
                  .ready(false).passwordEnabled(false).format(Format.OVA).featured(true).crossZones(true).OSTypeId("12")
                  .OSType("CentOS 5.3 (64-bit)").account("system").zoneId("2").zone("Chicago").type(Type.BUILTIN)
                  .hypervisor("VMware").domain("ROOT").domainId("1").extractable(true).build(),
            Template.builder().id("241").name("kvmdev4").displayText("v5.6.28_Dev4").isPublic(true)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-04-21T09:43:25-0700"))
                  .ready(true).passwordEnabled(false).format(Format.QCOW2).featured(false).crossZones(false)
                  .OSTypeId("14").OSType("CentOS 5.4 (64-bit)").account("rs3").zoneId("2").zone("Chicago")
                  .size(10737418240l).type(Type.USER).hypervisor("KVM").domain("ROOT").domainId("1").extractable(false)
                  .build());
   }
}
