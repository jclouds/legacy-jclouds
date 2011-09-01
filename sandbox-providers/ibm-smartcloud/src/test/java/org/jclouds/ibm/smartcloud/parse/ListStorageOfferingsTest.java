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
package org.jclouds.ibm.smartcloud.parse;

import java.util.Date;
import java.util.Set;

import org.jclouds.ibm.smartcloud.config.IBMSmartCloudParserModule;
import org.jclouds.ibm.smartcloud.domain.Price;
import org.jclouds.ibm.smartcloud.domain.StorageOffering;
import org.jclouds.ibm.smartcloud.domain.StorageOffering.Format;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.Unwrap;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ListStorageOfferingsTest")
public class ListStorageOfferingsTest extends BaseSetParserTest<StorageOffering> {

   protected Injector injector() {
      return Guice.createInjector(new GsonModule(), new IBMSmartCloudParserModule());
   }

   @Override
   public String resource() {
      return "/storage-offerings.json";
   }

   @Override
   @Unwrap
   public Set<StorageOffering> expected() {
      return ImmutableSet.of(//
               new StorageOffering("41", new Price(0.039, "UHR", "897", new Date(1279497600000l), "USD", 1), 256,
                        "Small", "20001208", ImmutableSet.of(new Format("ext3", "EXT3"))),//
               new StorageOffering("41", new Price(0.312, "UHR", "897", new Date(1279497600000l), "USD", 1), 2048,
                        "Large", "20001210", ImmutableSet.of(new Format("ext3", "EXT3"))),//
               new StorageOffering("41", new Price(0.078, "UHR", "897", new Date(1279497600000l), "USD", 1), 512,
                        "Medium", "20001209", ImmutableSet.of(new Format("ext3", "EXT3")))//
               );
   }

}
