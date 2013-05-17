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
import org.jclouds.location.predicates.ZoneIdFilter;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code AnyOrConfiguredZoneId}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "AnyOrConfiguredZoneIdTest")
public class AnyOrConfiguredZoneIdTest {

   @Test
   public void testWithoutConfigAllIdsMatch() {
      Set<String> zoneIds = ImmutableSet.of("us-east-1a", "us-east-1b");
     
      ZoneIdFilter filter = Guice.createInjector(new AbstractModule(){

         @Override
         protected void configure() {
            bindConstant().annotatedWith(Provider.class).to("aws-ec2");
         }
         
      }).getInstance(AnyOrConfiguredZoneId.class);
      assertEquals(Sets.filter(zoneIds, filter), ImmutableSet.of("us-east-1a", "us-east-1b"));
   }
   
   @Test
   public void testWithConfigOnlyMatchingIds() {
      Set<String> zoneIds = ImmutableSet.of("us-east-1a", "us-east-1b");
     
      ZoneIdFilter filter = Guice.createInjector(new AbstractModule(){

         @Override
         protected void configure() {
            bindConstant().annotatedWith(Provider.class).to("aws-ec2");
            bindConstant().annotatedWith(Names.named("jclouds.zones")).to("us-east-1a,us-east-1d");
         }
         
      }).getInstance(AnyOrConfiguredZoneId.class);
      
      assertEquals(Sets.filter(zoneIds, filter), ImmutableSet.of("us-east-1a"));
   }
}
