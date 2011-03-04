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

package org.jclouds.savvis.vpdc.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.internal.OrgImpl;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.xml.OrgHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code OrgHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class OrgHandlerTest {

   public void testSavvis() {
      InputStream is = getClass().getResourceAsStream("/savvis/org.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      Org result = (Org) factory.create(injector.getInstance(OrgHandler.class)).parse(is);
      assertEquals(
            result,
            new OrgImpl("100000.0", null, null, "100000.0", "SAVVISStation Integration Testing", ImmutableMap
                  .<String, ReferenceType> of(), ImmutableMap.<String, ReferenceType> of(
                  "demo_vpdcname",
                  new ReferenceTypeImpl("demo_vpdcname", "application/vnd.vmware.vcloud.vdc+xml", URI
                        .create("https://api.sandbox.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736"))),
                  ImmutableMap.<String, ReferenceType> of(), null, ImmutableSet.<Task> of()));

   }
}
