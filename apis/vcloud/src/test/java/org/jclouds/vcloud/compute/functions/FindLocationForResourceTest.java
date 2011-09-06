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
package org.jclouds.vcloud.compute.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code FindLocationForResource}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class FindLocationForResourceTest {
   public void testMatchWhenIdIsHref() {
      Location location = new LocationBuilder().id("http://foo").description("description")
            .scope(LocationScope.PROVIDER).build();
      FindLocationForResource converter = new FindLocationForResource(
            Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(location)));
      assertEquals(converter.apply(new ReferenceTypeImpl("name", "type", URI.create("http://foo"))), location);
   }

   @Test(expectedExceptions = NoSuchElementException.class)
   public void testGracefulWhenHrefIsntLocationId() {
      FindLocationForResource converter = new FindLocationForResource(
            Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(new LocationBuilder()
                  .id("http://bar").description("description").scope(LocationScope.PROVIDER).build())));
      converter.apply(new ReferenceTypeImpl("name", "type", URI.create("http://foo")));
   }

   @Test(expectedExceptions = NoSuchElementException.class)
   public void testGracefulWhenLocationIdIsntURI() {
      FindLocationForResource converter = new FindLocationForResource(
            Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(new LocationBuilder().id("1")
                  .description("description").scope(LocationScope.PROVIDER).build())));
      converter.apply(new ReferenceTypeImpl("name", "type", URI.create("http://foo")));
   }
}
