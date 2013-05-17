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
package org.jclouds.location.predicates.fromconfig;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.location.Provider;
import org.jclouds.location.predicates.RegionIdFilter;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code AnyOrConfiguredRegionId}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "AnyOrConfiguredRegionIdTest")
public class AnyOrConfiguredRegionIdTest {

   @Test
   public void testWithoutConfigAllIdsMatch() {
      Set<String> regionIds = ImmutableSet.of("us-east-1", "eu-west-1");
     
      RegionIdFilter filter = Guice.createInjector(new AbstractModule(){

         @Override
         protected void configure() {
            bindConstant().annotatedWith(Provider.class).to("aws-ec2");
         }
         
      }).getInstance(AnyOrConfiguredRegionId.class);
      assertEquals(Sets.filter(regionIds, filter), ImmutableSet.of("us-east-1", "eu-west-1"));
   }
   
   @Test
   public void testWithConfigOnlyMatchingIds() {
      Set<String> regionIds = ImmutableSet.of("us-east-1", "eu-west-1");
     
      RegionIdFilter filter = Guice.createInjector(new AbstractModule(){

         @Override
         protected void configure() {
            bindConstant().annotatedWith(Provider.class).to("aws-ec2");
            bindConstant().annotatedWith(Names.named("jclouds.regions")).to("us-east-1,unknown-1");
         }
         
      }).getInstance(AnyOrConfiguredRegionId.class);
      
      assertEquals(Sets.filter(regionIds, filter), ImmutableSet.of("us-east-1"));
   }
}
