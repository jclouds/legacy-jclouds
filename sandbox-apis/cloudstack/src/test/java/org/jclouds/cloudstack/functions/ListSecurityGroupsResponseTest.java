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

import org.jclouds.cloudstack.domain.IngressRule;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValue;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListSecurityGroupsResponseTest {

   Injector i = Guice.createInjector(new GsonModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         super.configure();
      }

   });

   public void test() {
      InputStream is = getClass().getResourceAsStream("/listsecuritygroupsresponse.json");

      Set<SecurityGroup> expects = ImmutableSortedSet.<SecurityGroup> naturalOrder().add(

      SecurityGroup.builder().id(12).name("adriancole").account("adrian").domainId(1).domain("ROOT").build()).add(
               SecurityGroup.builder().id(13).name("default").description("description").account("adrian").domainId(1)
                        .domain("ROOT").ingressRules(
                                 ImmutableSet.of(

                                 IngressRule.builder().id(5).protocol("tcp").startPort(22).endPort(22)
                                          .securityGroupName("adriancole").account("adrian").build(),

                                 IngressRule.builder().id(6).protocol("udp").startPort(11).endPort(11).CIDR(
                                          "1.1.1.1/24").build())).build()).add(
               SecurityGroup.builder().id(14).name("1").description("description").account("adrian").domainId(1)
                        .domain("ROOT").ingressRules(
                                 ImmutableSet.of(

                                 IngressRule.builder().id(7).protocol("tcp").startPort(10).endPort(10).CIDR(
                                          "1.1.1.1/24").build(),

                                 IngressRule.builder().id(8).protocol("tcp").startPort(10).endPort(10).CIDR(
                                          "2.2.2.2/16").build())).build()).add(
               SecurityGroup.builder().id(15).name("2").description("description").account("adrian").domainId(1)
                        .domain("ROOT").build(),
               SecurityGroup.builder().id(16).name("with1and2").description("description").account("adrian")
                        .domainId(1).domain("ROOT").ingressRules(
                                 ImmutableSet.of(

                                 IngressRule.builder().id(9).protocol("icmp").ICMPType(-1).ICMPCode(-1)
                                          .securityGroupName("1").account("adrian").build(),

                                 IngressRule.builder().id(10).protocol("tcp").startPort(22).endPort(22)
                                          .securityGroupName("1").account("adrian").build(),

                                 IngressRule.builder().id(11).protocol("tcp").startPort(22).endPort(22)
                                          .securityGroupName("2").account("adrian").build())).build()).build();

      UnwrapOnlyNestedJsonValue<Set<SecurityGroup>> parser = i.getInstance(Key
               .get(new TypeLiteral<UnwrapOnlyNestedJsonValue<Set<SecurityGroup>>>() {
               }));
      Set<SecurityGroup> response = ImmutableSortedSet.copyOf(parser.apply(new HttpResponse(200, "ok", Payloads
               .newInputStreamPayload(is))));

      assertEquals(response, expects);
   }

}
