/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.terremark.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.SortedSet;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloud.terremark.domain.Node;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;

/**
 * Tests behavior of {@code NodesHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.NodesHandlerTest")
public class NodesHandlerTest extends BaseHandlerTest {

   public void test1() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/terremark/NodeServices.xml");

      SortedSet<Node> result = factory.create(injector.getInstance(NodesHandler.class)).parse(is);
      assertEquals(result, ImmutableSortedSet.of(new Node(242 + "", "Node for Jim", URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/NodeServices/242"),
               InetAddress.getByName("172.16.20.3"), 80, false, "Some test node")));
   }
}
