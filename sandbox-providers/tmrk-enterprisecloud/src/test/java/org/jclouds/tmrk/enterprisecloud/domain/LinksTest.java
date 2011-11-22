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
package org.jclouds.tmrk.enterprisecloud.domain;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import static org.jclouds.tmrk.enterprisecloud.domain.Link.Relationship;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Jason King
 */
@Test(groups = "unit", testName = "LinksTest")
public class LinksTest {

   private Link link;
   private Links links;

   @BeforeMethod()
   public void setUp() throws URISyntaxException {
      link = Link.builder().href(new URI("/1")).name("my link").type("test link").rel(Relationship.FIRST).build();
      links = Links.builder().addLink(link).build();

   }

   @Test
   public void testAddAction() throws URISyntaxException {
      Link link2 = Link.builder().href(new URI("/2")).name("my link 2").type("test link").rel(Relationship.LAST).build();
      Links twoLinks = links.toBuilder().addLink(link2).build();
      Set<Link> linkSet = twoLinks.getLinks();

      assertEquals(2, linkSet.size());
      assertTrue(linkSet.contains(link));
      assertTrue(linkSet.contains(link2));
   }
}
