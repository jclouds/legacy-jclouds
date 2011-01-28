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

package org.jclouds.byon.suppliers;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
public class SupplyFromProviderURIOrNodesPropertyTest {

   @Test
   public void testFromURI() throws Exception {

      String path = getClass().getResource("/test1.yaml").getPath();
      SupplyFromProviderURIOrNodesProperty supplier = new SupplyFromProviderURIOrNodesProperty(URI.create("file://"
               + path));

      assertEquals(Strings2.toStringAndClose(supplier.get()), Strings2.toStringAndClose(getClass().getResourceAsStream(
               "/test1.yaml")));

   }

   @Test
   public void testFromURIClasspath() throws Exception {

      SupplyFromProviderURIOrNodesProperty supplier = new SupplyFromProviderURIOrNodesProperty(URI
               .create("classpath:///test1.yaml"));

      assertEquals(Strings2.toStringAndClose(supplier.get()), Strings2.toStringAndClose(getClass().getResourceAsStream(
               "/test1.yaml")));

   }

   @Test
   public void testFromProperty() throws Exception {

      SupplyFromProviderURIOrNodesProperty supplier = new SupplyFromProviderURIOrNodesProperty(URI.create("file://bar"));
      supplier.nodes = Strings2.toStringAndClose(getClass().getResourceAsStream("/test1.yaml"));

      assertEquals(Strings2.toStringAndClose(supplier.get()), Strings2.toStringAndClose(getClass().getResourceAsStream(
               "/test1.yaml")));

   }

   @Test
   public void testSupplyMultipleTimes() throws Exception {
      String path = getClass().getResource("/test1.yaml").getPath();
      SupplyFromProviderURIOrNodesProperty supplier = new SupplyFromProviderURIOrNodesProperty(URI.create("file://"
               + path));
      for (int i = 0; i < 5; i++)
         assertEquals(Strings2.toStringAndClose(supplier.get()), Strings2.toStringAndClose(getClass()
                  .getResourceAsStream("/test1.yaml")));
   }
}
