/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.xml.ovf;

import java.io.InputStream;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.domain.ovf.OvfEnvelope;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code OvfEnvelopeHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class OvfEnvelopeHandlerTest {
   public void testVCloud1_0() {
      InputStream is = getClass().getResourceAsStream("/ovf.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      OvfEnvelope result = factory.create(injector.getInstance(OvfEnvelopeHandler.class)).parse(is);
      checkOvfEnvelope(result);
   }

   static void checkOvfEnvelope(OvfEnvelope result) {
      VirtualSystemHandlerTest.checkVirtualSystem(result.getVirtualSystem());
   }

}
