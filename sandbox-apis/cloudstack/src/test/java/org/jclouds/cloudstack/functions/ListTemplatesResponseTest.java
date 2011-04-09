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
package org.jclouds.cloudstack.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.Template.Format;
import org.jclouds.cloudstack.domain.Template.Type;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValue;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListTemplatesResponseTest {

   Injector i = Guice.createInjector(new GsonModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         super.configure();
      }

   });

   public void test() {
      InputStream is = getClass().getResourceAsStream("/listtemplatesresponse.json");

      Set<Template> expects = ImmutableSortedSet.<Template> of(
            Template.builder().id(2).name("CentOS 5.3(64-bit) no GUI (XenServer)")
                  .displayText("CentOS 5.3(64-bit) no GUI (XenServer)").isPublic(true)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-02-11T18:45:54-0800"))
                  .ready(true).passwordEnabled(false).format(Format.VHD).featured(true).crossZones(true).OSTypeId(11)
                  .OSType("CentOS 5.3 (32-bit)").account("system").zoneId(1).zone("San Jose 1").size(8589934592l)
                  .type(Template.Type.BUILTIN).hypervisor("XenServer").domain("ROOT").domainId(1).extractable(true)
                  .build(),
            Template.builder().id(4).name("CentOS 5.5(64-bit) no GUI (KVM)")
                  .displayText("CentOS 5.5(64-bit) no GUI (KVM)").isPublic(true)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-02-11T18:45:54-0800"))
                  .ready(true).passwordEnabled(false).format(Format.QCOW2).featured(true).crossZones(true)
                  .OSTypeId(112).OSType("CentOS 5.5 (64-bit)").account("system").zoneId(1).zone("San Jose 1")
                  .size(8589934592l).type(Type.BUILTIN).hypervisor("KVM").domain("ROOT").domainId(1).extractable(true)
                  .build(),
            Template.builder().id(7).name("CentOS 5.3(64-bit) no GUI (vSphere)")
                  .displayText("CentOS 5.3(64-bit) no GUI (vSphere)").isPublic(true)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-02-11T18:45:54-0800"))
                  .ready(true).passwordEnabled(false).format(Format.OVA).featured(true).crossZones(true).OSTypeId(12)
                  .OSType("CentOS 5.3 (64-bit)").account("system").zoneId(1).zone("San Jose 1").size(459320832l)
                  .type(Type.BUILTIN).hypervisor("VMware").domain("ROOT").domainId(1).extractable(true).build());

      UnwrapOnlyNestedJsonValue<Set<Template>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyNestedJsonValue<Set<Template>>>() {
            }));
      Set<Template> response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));

      assertEquals(Sets.newTreeSet(response), expects);
   }

}
