/**
 *
 * Copyright (C) 2010 Cloud Conscious) LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License) Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing) software
 * distributed under the License is distributed on an "AS IS" BASIS)
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND) either express or implied.
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

      Set<SecurityGroup> expects = ImmutableSet.<SecurityGroup> of(SecurityGroup.builder().id(3).name("default")
               .description("Default Security Group").account("adrian").domainId(1).domain("ROOT").ingressRules(
                        ImmutableSet.of(IngressRule.builder().id(8).protocol("tcp").startPort(22).endPort(22).CIDR(
                                 "0.0.0.0/32").build(),

                        IngressRule.builder().id(9).protocol("icmp").ICMPType(-1).ICMPCode(-1).securityGroupName(
                                 "default").account("adrian").build(),

                        IngressRule.builder().id(10).protocol("tcp").startPort(80).endPort(80).securityGroupName(
                                 "default").account("adrian").build())).build(),

      SecurityGroup.builder().id(15).name("adriancole").account("adrian").domainId(1).domain("ROOT").build());

      UnwrapOnlyNestedJsonValue<Set<SecurityGroup>> parser = i.getInstance(Key
               .get(new TypeLiteral<UnwrapOnlyNestedJsonValue<Set<SecurityGroup>>>() {
               }));
      Set<SecurityGroup> response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));

      assertEquals(Sets.newHashSet(response), expects);
   }

}
