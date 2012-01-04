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


import static org.testng.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.glesys.config.GleSYSParserModule;
import org.jclouds.glesys.domain.Domain;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ParseDomainListTest")
public class ParseDomainListTest extends BaseSetParserTest<Domain> {

   @Override
   public String resource() {
      return "/domain_list.json";
   }

   @Override
   @SelectJson("domains")
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<Domain> expected() {
      Date creationTime = null;
      try {
         creationTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2011-12-20 10:58:51");
      } catch (ParseException e) {
         fail("Bad dates!");
      }
      Domain domain = Domain.builder().domain("adamlowe.net").createTime(creationTime).recordCount(9).glesysNameServer(false).build();
      return Sets.newHashSet(domain);
   }

   protected Injector injector() {
      return Guice.createInjector(new GleSYSParserModule(), new GsonModule());
   }
}