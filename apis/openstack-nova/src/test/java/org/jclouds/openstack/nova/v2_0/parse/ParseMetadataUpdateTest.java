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
package org.jclouds.openstack.nova.v2_0.parse;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Jeremy Daggett
 */
@Test(groups = "unit", testName = "ParseMetadataUpdateTest")
public class ParseMetadataUpdateTest extends BaseItemParserTest<Map<String, String>> {

   @Override
   public String resource() {
      return "/metadata_updated.json";
   }

   @Override
   @SelectJson("metadata")
   @Consumes(MediaType.APPLICATION_JSON)
   public Map<String, String> expected() {
      ImmutableMap<String, String> metadata =
         new ImmutableMap.Builder<String, String>()
               .put("Server Label", "Web Head 2")
               .put("Image Version", "2.1")
               .put("Server Description", "Simple Server")
               .build();
  
      return metadata;
   }

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }

}
