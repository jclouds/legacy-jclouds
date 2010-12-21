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

package org.jclouds.deltacloud.handlers;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import org.jclouds.deltacloud.collections.DeltacloudCollection;
import org.jclouds.deltacloud.xml.LinksHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code LinksHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class LinksHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/links.xml");

      Map<DeltacloudCollection, URI> result = factory.create(injector.getInstance(LinksHandler.class)).parse(is);
      assertEquals(result, ImmutableMap.of(//
            DeltacloudCollection.HARDWARE_PROFILES, URI.create("http://fancycloudprovider.com/api/hardware_profiles"),//
            DeltacloudCollection.INSTANCE_STATES, URI.create("http://fancycloudprovider.com/api/instance_states"),//
            DeltacloudCollection.REALMS, URI.create("http://fancycloudprovider.com/api/realms"),//
            DeltacloudCollection.IMAGES, URI.create("http://fancycloudprovider.com/api/images"),//
            DeltacloudCollection.INSTANCES, URI.create("http://fancycloudprovider.com/api/instances")

      ));

   }

}
