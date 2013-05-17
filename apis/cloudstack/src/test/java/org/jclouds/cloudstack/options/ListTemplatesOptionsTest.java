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
package org.jclouds.cloudstack.options;

import static org.jclouds.cloudstack.options.ListTemplatesOptions.Builder.accountInDomain;
import static org.jclouds.cloudstack.options.ListTemplatesOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.ListTemplatesOptions.Builder.filter;
import static org.jclouds.cloudstack.options.ListTemplatesOptions.Builder.hypervisor;
import static org.jclouds.cloudstack.options.ListTemplatesOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListTemplatesOptions.Builder.zoneId;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.domain.TemplateFilter;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListTemplatesOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListTemplatesOptionsTest {

   public void testId() {
      ListTemplatesOptions options = new ListTemplatesOptions().id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testIdStatic() {
      ListTemplatesOptions options = id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testDomainId() {
      ListTemplatesOptions options = new ListTemplatesOptions().domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainIdStatic() {
      ListTemplatesOptions options = domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountInDomainId() {
      ListTemplatesOptions options = new ListTemplatesOptions().accountInDomain("adrian", "6");
      assertEquals(ImmutableList.of("adrian"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountInDomainIdStatic() {
      ListTemplatesOptions options = accountInDomain("adrian", "6");
      assertEquals(ImmutableList.of("adrian"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testHypervisor() {
      ListTemplatesOptions options = new ListTemplatesOptions().hypervisor("KVM");
      assertEquals(ImmutableList.of("KVM"), options.buildQueryParameters().get("hypervisor"));
   }

   public void testHypervisorStatic() {
      ListTemplatesOptions options = hypervisor("KVM");
      assertEquals(ImmutableList.of("KVM"), options.buildQueryParameters().get("hypervisor"));
   }

   public void testName() {
      ListTemplatesOptions options = new ListTemplatesOptions().id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testNameStatic() {
      ListTemplatesOptions options = id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testZoneId() {
      ListTemplatesOptions options = new ListTemplatesOptions().zoneId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("zoneid"));
   }

   public void testZoneIdStatic() {
      ListTemplatesOptions options = zoneId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("zoneid"));
   }

   public void testFilterDefault() {
      assertEquals(ImmutableList.of("executable"),
            new ListTemplatesOptions().buildQueryParameters().get("templatefilter"));
   }

   public void testFilter() {
      ListTemplatesOptions options = new ListTemplatesOptions().filter(TemplateFilter.SELF_EXECUTABLE);
      assertEquals(ImmutableList.of("self-executable"), options.buildQueryParameters().get("templatefilter"));
   }

   public void testFilterStatic() {
      ListTemplatesOptions options = filter(TemplateFilter.SELF_EXECUTABLE);
      assertEquals(ImmutableList.of("self-executable"), options.buildQueryParameters().get("templatefilter"));
   }
}
