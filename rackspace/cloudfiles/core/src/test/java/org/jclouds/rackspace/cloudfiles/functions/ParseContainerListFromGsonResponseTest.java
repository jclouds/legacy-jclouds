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
package org.jclouds.rackspace.cloudfiles.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseContainerListFromGsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudfiles.ParseContainerListFromGsonResponse")
public class ParseContainerListFromGsonResponseTest {
   Injector i = Guice.createInjector(new ParserModule());

   @Test
   public void testApplyInputStream() {
      InputStream is = IOUtils
               .toInputStream("[ {\"name\":\"test_container_1\",\"count\":2,\"bytes\":78}, {\"name\":\"test_container_2\",\"count\":1,\"bytes\":17} ]   ");
      List<ContainerMetadata> expects = ImmutableList.of(new ContainerMetadata("test_container_1",
               2, 78), new ContainerMetadata("test_container_2", 1, 17));
      ParseContainerListFromGsonResponse parser = new ParseContainerListFromGsonResponse(i.getInstance(Gson.class));
      assertEquals(parser.apply(is), expects);
   }

}
