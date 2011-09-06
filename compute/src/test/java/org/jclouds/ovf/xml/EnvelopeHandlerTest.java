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
package org.jclouds.ovf.xml;

import java.io.InputStream;

import org.jclouds.cim.xml.VirtualSystemSettingDataHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.ovf.Envelope;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code EnvelopeHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class EnvelopeHandlerTest {
   public void testVCloud1_0() {
      InputStream is = getClass().getResourceAsStream("/ovf.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      Envelope result = factory.create(injector.getInstance(EnvelopeHandler.class)).parse(is);
      checkOvfEnvelope(result);
   }

   static void checkOvfEnvelope(Envelope result) {
      VirtualSystemSettingDataHandlerTest.checkVirtualSystem(result.getVirtualSystem());
   }

}
