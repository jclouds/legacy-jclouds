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
package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.NetworkConnectionSection;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code NetworkConnectionSectionHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class NetworkConnectionSectionHandlerTest {
   public void testVCloud1_0() {
      InputStream is = getClass().getResourceAsStream("/networkconnectionsection.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      NetworkConnectionSection result = factory.create(injector.getInstance(NetworkConnectionSectionHandler.class))
               .parse(is);
      checkNetworkConnectionSection(result);
   }

   @Test(enabled = false)
   static void checkNetworkConnectionSection(NetworkConnectionSection result) {
      assertEquals(result.getHref(), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/vm-2087535248/networkConnectionSection/"));
      assertEquals(result.getType(), VCloudMediaType.NETWORKCONNECTIONSECTION_XML);
      assertEquals(result.getInfo(), "Specifies the available VM network connections");
      assertEquals(result.getPrimaryNetworkConnectionIndex(), Integer.valueOf(0));
      assertEquals(result.getEdit(), new ReferenceTypeImpl(null, VCloudMediaType.NETWORKCONNECTIONSECTION_XML, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/vm-2087535248/networkConnectionSection/")));
      NetworkConnectionHandlerTest.checkNetworkConnection(Iterables.getOnlyElement(result.getConnections()));
   }
}
