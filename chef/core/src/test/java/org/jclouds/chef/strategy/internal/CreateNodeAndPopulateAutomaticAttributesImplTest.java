/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.chef.strategy.internal;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.Map;

import org.jclouds.chef.ChefClient;
import org.jclouds.chef.domain.Node;
import org.jclouds.domain.JsonBall;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code CreateNodeAndPopulateAutomaticAttributesImpl}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ohai.CreateNodeAndPopulateAutomaticAttributesImplTest")
public class CreateNodeAndPopulateAutomaticAttributesImplTest {

   @Test
   public void testWithNoRunlist() {
      ChefClient chef = createMock(ChefClient.class);

      Map<String, JsonBall> automatic = ImmutableMap.<String, JsonBall> of();

      Node node = new Node("name", ImmutableSet.<String> of());

      Supplier<Map<String, JsonBall>> automaticSupplier = Suppliers.<Map<String, JsonBall>> ofInstance(automatic);

      Node nodeWithAutomatic = new Node("name", ImmutableMap.<String, JsonBall> of(), ImmutableMap
            .<String, JsonBall> of(), ImmutableMap.<String, JsonBall> of(), automatic, ImmutableSet.<String> of());

      node.getAutomatic().putAll(automaticSupplier.get());
      chef.createNode(nodeWithAutomatic);

      replay(chef);

      CreateNodeAndPopulateAutomaticAttributesImpl updater = new CreateNodeAndPopulateAutomaticAttributesImpl(chef, automaticSupplier);

      updater.execute("name", ImmutableSet.<String> of());
      verify(chef);

   }
}
