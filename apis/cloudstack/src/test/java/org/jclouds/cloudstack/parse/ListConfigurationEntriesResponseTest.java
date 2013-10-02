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

import org.jclouds.cloudstack.domain.ConfigurationEntry;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class ListConfigurationEntriesResponseTest extends BaseSetParserTest<ConfigurationEntry> {

   @Override
   protected Injector injector() {
      return Guice.createInjector(new GsonModule() {

         @Override
         protected void configure() {
            bind(DateAdapter.class).to(Iso8601DateAdapter.class);
            super.configure();
         }

      });
   }

   @Override
   public String resource() {
      return "/listconfigurationsresponse.json";
   }

   @Override
   @SelectJson("configuration")
   public Set<ConfigurationEntry> expected() {
      return ImmutableSet.of(
         ConfigurationEntry.builder().category("Advanced").name("account.cleanup.interval").value("86400")
            .description("The interval (in seconds) between cleanup for removed accounts").build(),
         ConfigurationEntry.builder().category("Advanced").name("agent.lb.enabled").value("true")
            .description("If agent load balancing enabled in cluster setup").build()
      );
   }

}
