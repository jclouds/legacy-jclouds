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

package org.jclouds.vcloud.bluelock.compute.config.providers;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.functions.FindLocationForResource;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.xml.VDCHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseVAppTemplatesInVDCToSizeProvider}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.ParseVAppTemplatesInVDCToSizeProviderTest")
public class ParseVAppTemplatesInVDCToSizeProviderTest {

   public void testParse() {
      InputStream is = getClass().getResourceAsStream("/bluelock/vdc.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());

      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VDC vdc = factory.create(injector.getInstance(VDCHandler.class)).parse(is);

      VCloudClient client = createMock(VCloudClient.class);
      FindLocationForResource findLocationForResourceInVDC = createMock(FindLocationForResource.class);

      ParseVAppTemplatesInVDCToSizeProvider providerParser = new ParseVAppTemplatesInVDCToSizeProvider(client,
               findLocationForResourceInVDC);

      expect(findLocationForResourceInVDC.apply((NamedResource) anyObject())).andReturn(null).atLeastOnce();

      replay(client);
      replay(findLocationForResourceInVDC);

      Set<Size> sizes = Sets.newLinkedHashSet();
      providerParser.addSizesFromVAppTemplatesInVDC(vdc, sizes);
      assertEquals(sizes.size(), vdc.getResourceEntities().size());

      assertEquals(Iterables.get(sizes, 0), new SizeImpl("https://express3.bluelock.com/api/v0.8/vAppTemplate/396", "4CPUx2GBx20GB", "https://express3.bluelock.com/api/v0.8/vAppTemplate/396", null, null, ImmutableMap
               .<String, String> of(), 4.0, 2048, 20, ImagePredicates.idEquals("https://express3.bluelock.com/api/v0.8/vAppTemplate/396")));

      assertEquals(Iterables.get(sizes, 116), new SizeImpl("https://express3.bluelock.com/api/v0.8/vAppTemplate/434", "1CPUx512MBx40GB", "https://express3.bluelock.com/api/v0.8/vAppTemplate/434", null, null, ImmutableMap
               .<String, String> of(), 1.0, 512, 40, ImagePredicates.idEquals("https://express3.bluelock.com/api/v0.8/vAppTemplate/434")));

      assertEquals(Iterables.getLast(sizes), new SizeImpl("https://express3.bluelock.com/api/v0.8/vAppTemplate/383", "1CPUx1GBx20GB", "https://express3.bluelock.com/api/v0.8/vAppTemplate/383", null, null, ImmutableMap
               .<String, String> of(), 1.0, 1024, 20, ImagePredicates.idEquals("https://express3.bluelock.com/api/v0.8/vAppTemplate/383")));

      verify(client);
      verify(findLocationForResourceInVDC);

   }
}
