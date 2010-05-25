/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloud.terremark.domain.Node;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code NodeServiceHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.NodeServiceHandlerTest")
public class NodeHandlerTest extends BaseHandlerTest {

   public void test1() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/terremark/NodeService.xml");

      Node result = (Node) factory.create(injector.getInstance(NodeHandler.class)).parse(is);
      assertEquals(result, new Node(242, "Node for Jim", URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/NodeServices/242"),
               "172.16.20.3", 80, false, "Some test node"));
   }
}
