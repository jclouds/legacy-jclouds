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
package org.jclouds.glesys.parse;


import java.util.Arrays;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.glesys.config.GleSYSParserModule;
import org.jclouds.glesys.domain.DomainRecord;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ParseDomainRecordListTest")
public class ParseDomainRecordListTest extends BaseSetParserTest<DomainRecord> {

   @Override
   public String resource() {
      return "/domain_list_records.json";
   }

   @Override
   @SelectJson("records")
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<DomainRecord> expected() {
      return ImmutableSortedSet.copyOf(
            Arrays.asList(
                  DomainRecord.builder().id("213227").zone("adamlowe.net").host("@").type("NS").data("ns1.namesystem.se.").ttl(3600).build(),
                  DomainRecord.builder().id("213228").zone("adamlowe.net").host("@").type("NS").data("ns2.namesystem.se.").ttl(3600).build(),
                  DomainRecord.builder().id("213229").zone("adamlowe.net").host("@").type("NS").data("ns3.namesystem.se.").ttl(3600).build(),
                  DomainRecord.builder().id("213230").zone("adamlowe.net").host("@").type("A").data("127.0.0.1").ttl(3600).build(),
                  DomainRecord.builder().id("213231").zone("adamlowe.net").host("www").type("A").data("127.0.0.1").ttl(3600).build(),
                  DomainRecord.builder().id("213232").zone("adamlowe.net").host("mail").type("A").data("79.99.4.40").ttl(3600).build(),
                  DomainRecord.builder().id("213233").zone("adamlowe.net").host("@").type("MX").data("mx01.glesys.se.").ttl(3600).build(),
                  DomainRecord.builder().id("213234").zone("adamlowe.net").host("@").type("MX").data("mx02.glesys.se.").ttl(3600).build(),
                  DomainRecord.builder().id("213235").zone("adamlowe.net").host("@").type("TXT").data("v=spf1 include:spf.glesys.se -all").ttl(3600).build()
            ));
   }

   protected Injector injector() {
      return Guice.createInjector(new GleSYSParserModule(), new GsonModule());
   }
}