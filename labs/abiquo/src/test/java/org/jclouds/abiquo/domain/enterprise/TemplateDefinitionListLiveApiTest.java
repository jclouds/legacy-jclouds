/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.enterprise;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.List;

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.enterprise.TemplateDefinitionListPredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Live integration tests for the {@link TemplateDefinitionList} domain class.
 * 
 * @author Francesc Montserrat
 */
@Test(groups = "api", testName = "TemplateDefinitionListLiveApiTest")
public class TemplateDefinitionListLiveApiTest extends BaseAbiquoApiLiveApiTest {
   private TemplateDefinitionList list;

   public void testUpdate() {
      list.setName(list.getName() + "Updated");
      list.update();

      List<TemplateDefinitionList> lists = env.enterprise.listTemplateDefinitionLists(TemplateDefinitionListPredicates
            .name("myListUpdated"));

      assertEquals(lists.size(), 1);
   }

   public void testListStates() {
      List<TemplateState> states = list.listStatus(env.datacenter);
      assertNotNull(states);
   }

   @BeforeClass
   public void setup() {
      list = TemplateDefinitionList.builder(env.context.getApiContext(), env.enterprise).name("myList")
            .url("http://virtualapp-repository.com/vapp1.ovf").build();

      list.save();

      assertNotNull(list.getId());
   }

   @AfterClass
   public void tearDown() {
      Integer idTemplateList = list.getId();
      list.delete();
      assertNull(env.enterprise.getTemplateDefinitionList(idTemplateList));
   }
}
