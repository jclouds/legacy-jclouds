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
package org.jclouds.savvis.vpdc.binders;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code BindVMSpecToXmlPayload}
 * 
 * @author Kedar Dave
 */
@Test(groups = "unit")
public class BindCloneVAppToXmlPayloadTest {

   public void test() throws IOException {
      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/cloneVApp-default.xml"));
      URI vAppURI = URI.create("https://api.savvis.net/vpdc/v1.0/org/100000.0/vdc/2736/vApp/1001");
      
      String xml = new BindCloneVMToXmlPayload().generateXml(vAppURI, "clonedvm", "VM Tier01");
      System.out.println(xml);
      
      assertEquals(new BindCloneVMToXmlPayload().generateXml(vAppURI, "clonedvm", "VM Tier01"), expected);
   }
}
