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
package org.jclouds.deltacloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import org.jclouds.deltacloud.domain.DeltacloudCollection;
import org.jclouds.deltacloud.domain.Feature;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code DeltacloudCollectionsHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DeltacloudCollectionsHandlerTest")
public class DeltacloudCollectionsHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/links.xml");
      Set<DeltacloudCollection> expects = ImmutableSet.of(
            new DeltacloudCollection(URI.create("http://localhost:3001/api/realms"), "realms"),
            new DeltacloudCollection(URI.create("http://localhost:3001/api/images"), "images"),
            new DeltacloudCollection(URI.create("http://localhost:3001/api/instance_states"), "instance_states"),
            new DeltacloudCollection(URI.create("http://localhost:3001/api/instances"), "instances", ImmutableSet
                  .<Feature> of(new Feature("hardware_profiles"), new Feature("user_name"), new Feature(
                        "authentication_key"))),
            new DeltacloudCollection(URI.create("http://localhost:3001/api/hardware_profiles"), "hardware_profiles"),
            new DeltacloudCollection(URI.create("http://localhost:3001/api/storage_snapshots"), "storage_snapshots"),
            new DeltacloudCollection(URI.create("http://localhost:3001/api/storage_volumes"), "storage_volumes"),
            new DeltacloudCollection(URI.create("http://localhost:3001/api/keys"), "keys"), new DeltacloudCollection(
                  URI.create("http://localhost:3001/api/buckets"), "buckets")

      );
      assertEquals(factory.create(injector.getInstance(DeltacloudCollectionsHandler.class)).parse(is), expects);
   }
}
