/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rackspace.cloudservers.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudservers.domain.Flavor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseFlavorListFromGsonResponseTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudFlavors.ParseFlavorListFromGsonResponseTest")
public class ParseFlavorListFromGsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_flavors.json");

      List<Flavor> expects = ImmutableList.of(new Flavor(1, "256 MB Server"), new Flavor(2,
               "512 MB Server"));

      ParseFlavorListFromGsonResponse parser = new ParseFlavorListFromGsonResponse(i
               .getInstance(Gson.class));
      assertEquals(parser.apply(is), expects);
   }

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_list_flavors_detail.json");

      ParseFlavorListFromGsonResponse parser = new ParseFlavorListFromGsonResponse(i
               .getInstance(Gson.class));
      List<Flavor> response = parser.apply(is);
      assertEquals(response.get(0).getId(), 1);
      assertEquals(response.get(0).getName(), "256 MB Server");
      assertEquals(response.get(0).getDisk(), new Integer(10));
      assertEquals(response.get(0).getRam(), new Integer(256));
     
      assertEquals(response.get(1).getId(), 2);
      assertEquals(response.get(1).getName(), "512 MB Server");
      assertEquals(response.get(1).getDisk(), new Integer(20));
      assertEquals(response.get(1).getRam(), new Integer(512));
     
   }

}
