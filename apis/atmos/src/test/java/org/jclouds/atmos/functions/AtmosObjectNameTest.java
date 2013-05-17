/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.atmos.functions;

import static org.testng.Assert.assertEquals;

import java.io.File;

import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.atmos.domain.SystemMetadata;
import org.jclouds.atmos.domain.UserMetadata;
import org.jclouds.atmos.domain.AtmosObject.Factory;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class AtmosObjectNameTest {
   AtmosObjectName fn = new AtmosObjectName();
   private static final Factory BLOB_FACTORY = Guice.createInjector().getInstance(AtmosObject.Factory.class);

   @Test
   public void testCorrectContentMetadataName() throws SecurityException, NoSuchMethodException {

      AtmosObject blob = BLOB_FACTORY.create(null);
      blob.getContentMetadata().setName("foo");

      assertEquals(fn.apply(blob), "foo");
   }

   @Test
   public void testCorrectSystemMetadataObjectName() throws SecurityException, NoSuchMethodException {

      AtmosObject blob = BLOB_FACTORY.create(new SystemMetadata(null, null, null, null, null, null, 0, null, "foo",
            null, 0, null, null), new UserMetadata());
      assertEquals(fn.apply(blob), "foo");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeAtmosObject() {
      fn.apply(new File("foo"));
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      fn.apply(null);
   }
}
