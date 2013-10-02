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
package org.jclouds.glesys.parse;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.glesys.config.GleSYSParserModule;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "unit", testName = "ParseIpAddressFromResponseTest")
public class ParseIpAddressFromResponseTest extends BaseSetParserTest<String> {
   public static final Set<String> EXPECTED_IPS = ImmutableSet.of("109.74.10.13", "109.74.10.50", "109.74.10.109", "109.74.10.125",
         "109.74.10.131", "109.74.10.148", "109.74.10.171", "109.74.10.173", "109.74.10.191", "109.74.10.215",
         "109.74.10.216", "109.74.10.219", "109.74.10.223", "109.74.10.224", "109.74.10.236", "109.74.10.249",
         "109.74.11.49", "109.74.11.58", "109.74.11.62", "109.74.11.63", "109.74.11.73", "109.74.11.76",
         "109.74.11.86", "109.74.11.98", "109.74.11.118", "109.74.11.124", "109.74.11.131", "109.74.11.137",
         "109.74.11.146", "109.74.11.157", "109.74.11.159", "109.74.11.173", "109.74.11.178", "109.74.11.187",
         "109.74.11.190", "109.74.11.205", "109.74.11.213", "109.74.11.234", "109.74.11.236", "109.74.11.241",
         "109.74.11.243", "109.74.11.246", "109.74.11.247");
   
   @Override
   public String resource() {
      return "/ip_list_free.json";
   }

   @Override
   @SelectJson("ipaddresses")
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<String> expected() {
      return EXPECTED_IPS;
   }

   protected Injector injector() {
      return Guice.createInjector(new GleSYSParserModule(), new GsonModule());
   }
}
