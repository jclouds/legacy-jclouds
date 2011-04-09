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
package org.jclouds.savvis.vpdc.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.savvis.vpdc.domain.NetworkConfigSection;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code NetworkConfigSectionHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class NetworkConfigSectionHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/networkconfigsection.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      NetworkConfigSection result = factory.create(injector.getInstance(NetworkConfigSectionHandler.class)).parse(is);
      assertEquals(result.toString(), NetworkConfigSection.builder().network("VM Tier01").gateway("0.0.0.0").netmask(
               "0.0.0.0").info("MAC=00:00:00:00:00:00").fenceMode("allowInOut").dhcp(true).internalToExternalNATRule(
               "10.76.2.4", "206.24.124.1").build().toString());
   }
}
