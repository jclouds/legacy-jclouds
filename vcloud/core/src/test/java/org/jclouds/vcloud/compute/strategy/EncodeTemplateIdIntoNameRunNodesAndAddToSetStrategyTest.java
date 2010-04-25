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
package org.jclouds.vcloud.compute.strategy;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.concurrent.ExecutorService;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.EncodeTemplateIdIntoNameRunNodesAndAddToSetStrategyTest")
public class EncodeTemplateIdIntoNameRunNodesAndAddToSetStrategyTest {

   @Test
   public void testGetNextName() {
      EncodeTemplateIdIntoNameRunNodesAndAddToSetStrategy strategy = new EncodeTemplateIdIntoNameRunNodesAndAddToSetStrategy(
               createNiceMock(AddNodeWithTagStrategy.class),
               createNiceMock(ListNodesStrategy.class), "%s-%s%s",
               createNiceMock(ComputeUtils.class), createNiceMock(ExecutorService.class));

      String oldName = null;
      for (int i = 0; i < 5; i++) {
         Template template = createMock(Template.class);
         Image image = createMock(Image.class);
         expect(template.getImage()).andReturn(image);
         expect(image.getId()).andReturn("233");
         replay(template);
         replay(image);

         String name = strategy.getNextName("test", template);
         if (oldName != null) {
            assert !oldName.equals(name);
            oldName = name;
         }

         assertEquals(name.length(), 10);
         assertEquals(name.substring(0, 4), "test");
         System.out.println(name);
         assertEquals(name.substring(5, 8), "0e9");
         assert name.substring(8, 9).matches("[0-9a-f]+");
         verify(template);
         verify(image);

      }
   }
}
