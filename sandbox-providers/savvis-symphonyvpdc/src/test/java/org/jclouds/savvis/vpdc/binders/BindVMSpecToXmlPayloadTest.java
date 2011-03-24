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

package org.jclouds.savvis.vpdc.binders;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.jclouds.cim.OSType;
import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.savvis.vpdc.domain.VMSpec;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code BindVMSpecToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindVMSpecToXmlPayloadTest {

   public void test() throws IOException {
      CIMOperatingSystem os = Iterables.find(new Gson().<Set<CIMOperatingSystem>> fromJson(
            Strings2.toStringAndClose(getClass().getResourceAsStream(
                  "/savvis-symphonyvpdc/predefined_operatingsystems.json")),
            new TypeLiteral<Set<CIMOperatingSystem>>() {
            }.getType()), new Predicate<CIMOperatingSystem>() {

         @Override
         public boolean apply(CIMOperatingSystem arg0) {
            return arg0.getOsType() == OSType.RHEL_64;
         }

      });

      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/vm-default.xml"));

      VMSpec spec = VMSpec.builder().operatingSystem(os).build();

      assertEquals(new BindVMSpecToXmlPayload().generateXml(spec, "DemoHost-1", "VM Tier01"), expected);
   }
}
