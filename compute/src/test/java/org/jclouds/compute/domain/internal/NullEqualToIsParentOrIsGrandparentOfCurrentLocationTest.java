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
package org.jclouds.compute.domain.internal;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

/**
 * 
 * @author Adrian Cole
 */
@Test(testName = "NullEqualToIsParentOrIsGrandparentOfCurrentLocationTest")
public class NullEqualToIsParentOrIsGrandparentOfCurrentLocationTest {
   Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("aws-ec2").description("aws-ec2").build();

   Location region = new LocationBuilder().scope(LocationScope.REGION).id("us-east-1").description("us-east-1")
         .parent(provider).build();

   Location zone = new LocationBuilder().scope(LocationScope.ZONE).id("us-east-1a").description("us-east-1a")
         .parent(region).build();

   Location host = new LocationBuilder().scope(LocationScope.HOST).id("xxxxx").description("xxxx").parent(zone).build();

   Location otherRegion = new LocationBuilder().scope(LocationScope.REGION).id("us-west-1").description("us-west-1")
         .parent(provider).build();

   Location otherZone = new LocationBuilder().scope(LocationScope.ZONE).id("us-west-1a").description("us-west-1a")
         .parent(otherRegion).build();

   Location orphanedRegion = new LocationBuilder().scope(LocationScope.REGION).id("us-east-1").description("us-east-1")
         .build();
   
   Location orphanedZone = new LocationBuilder().scope(LocationScope.ZONE).id("us-east-1a")
         .description("us-east-1a").build();
   /**
    * If the current location id is null, then we don't care where to launch a
    */
   public void testReturnTrueWhenIDontSpecifyALocation() {
      NullEqualToIsParentOrIsGrandparentOfCurrentLocation predicate = new NullEqualToIsParentOrIsGrandparentOfCurrentLocation(Suppliers.<Location> ofInstance(null));
      Hardware md = new HardwareBuilder().id("foo").location(region).build();
      assertTrue(predicate.apply(md));
   }

   /**
    * If the input location is null, then the data isn't location sensitive
    */
   public void testReturnTrueWhenISpecifyALocationAndInputLocationIsNull() {
      NullEqualToIsParentOrIsGrandparentOfCurrentLocation predicate = new NullEqualToIsParentOrIsGrandparentOfCurrentLocation(Suppliers.ofInstance(region));
      Hardware md = new HardwareBuilder().id("foo").location(null).build();
      assertTrue(predicate.apply(md));
   }

   /**
    * If the input location is null, then the data isn't location sensitive
    */
   public void testReturnTrueWhenIDontSpecifyALocationAndInputLocationIsNull() {
      NullEqualToIsParentOrIsGrandparentOfCurrentLocation predicate = new NullEqualToIsParentOrIsGrandparentOfCurrentLocation(Suppliers.<Location> ofInstance(null));
      Hardware md = new HardwareBuilder().id("foo").location(null).build();
      assertTrue(predicate.apply(md));
   }

   @Test
   public void testReturnTrueWhenISpecifyARegionAndInputLocationIsProvider() {
      NullEqualToIsParentOrIsGrandparentOfCurrentLocation predicate = new NullEqualToIsParentOrIsGrandparentOfCurrentLocation(Suppliers.ofInstance(region));
      Hardware md = new HardwareBuilder().id("foo").location(provider).build();
      assertTrue(predicate.apply(md));
   }

   /**
    * When locations are equal
    */
   @Test
   public void testReturnFalseWhenISpecifyALocationWhichTheSameScopeByNotEqualToInputLocationAndParentsAreNull() {
      NullEqualToIsParentOrIsGrandparentOfCurrentLocation predicate = new NullEqualToIsParentOrIsGrandparentOfCurrentLocation(Suppliers.ofInstance(region));
      Hardware md = new HardwareBuilder().id("foo").location(otherRegion).build();
      assertFalse(predicate.apply(md));
   }

   /**
    * If the input location is null, then the data isn't location sensitive
    */
   public void testReturnFalseWhenISpecifyALocationWhichTheSameScopeByNotEqualToInputLocationAndParentsAreNotNull() {
      NullEqualToIsParentOrIsGrandparentOfCurrentLocation predicate = new NullEqualToIsParentOrIsGrandparentOfCurrentLocation(Suppliers.ofInstance(zone));
      Hardware md = new HardwareBuilder().id("foo").location(otherZone).build();
      assertFalse(predicate.apply(md));
   }

   /**
    * If the input location is a parent of the specified location, then we are
    * ok.
    */
   public void testReturnTrueWhenISpecifyALocationWhichIsAChildOfInput() {
      NullEqualToIsParentOrIsGrandparentOfCurrentLocation predicate = new NullEqualToIsParentOrIsGrandparentOfCurrentLocation(Suppliers.ofInstance(zone));
      Hardware md = new HardwareBuilder().id("foo").location(region).build();
      assertTrue(predicate.apply(md));
   }

   /**
    * If the input location is a parent of the specified location, then we are
    * ok.
    */
   public void testReturnFalseWhenISpecifyALocationWhichIsNotAChildOfInput() {
      NullEqualToIsParentOrIsGrandparentOfCurrentLocation predicate = new NullEqualToIsParentOrIsGrandparentOfCurrentLocation(Suppliers.ofInstance(zone));
      Hardware md = new HardwareBuilder().id("foo").location(otherRegion).build();
      assertFalse(predicate.apply(md));
   }

   /**
    * If the input location is a grandparent of the specified location, then we
    * are ok.
    */
   public void testReturnTrueWhenISpecifyALocationWhichIsAGrandChildOfInput() {
      NullEqualToIsParentOrIsGrandparentOfCurrentLocation predicate = new NullEqualToIsParentOrIsGrandparentOfCurrentLocation(Suppliers.ofInstance(host));

      Hardware md = new HardwareBuilder().id("foo").location(host).build();
      assertTrue(predicate.apply(md));
   }

   /**
    * If the input location is a grandparent of the specified location, then we
    * are ok.
    */
   public void testReturnFalseWhenISpecifyALocationWhichIsNotAGrandChildOfInput() {
      NullEqualToIsParentOrIsGrandparentOfCurrentLocation predicate = new NullEqualToIsParentOrIsGrandparentOfCurrentLocation(Suppliers.ofInstance(host));
      Hardware md = new HardwareBuilder().id("foo").location(otherRegion).build();
      assertFalse(predicate.apply(md));
   }

   /**
    * Only the PROVIDER scope should have a null parent, It is an illegal state if a ZONE or REGION are orphaned
    * 
    */
   @Test(expectedExceptions = IllegalStateException.class)
   public void testThrowIllegalStateExceptionWhenInputIsAnOrphanedRegion() {
      NullEqualToIsParentOrIsGrandparentOfCurrentLocation predicate = new NullEqualToIsParentOrIsGrandparentOfCurrentLocation(Suppliers.ofInstance(region));
      Hardware md = new HardwareBuilder().id("foo").location(orphanedRegion).build();
      predicate.apply(md);
   }
   
   /**
    * Only the PROVIDER scope should have a null parent, It is an illegal state if a ZONE or REGION are orphaned
    * 
    */
   @Test(expectedExceptions = IllegalStateException.class)
   public void testThrowIllegalStateExceptionWhenInputIsAnOrphanedZone() {
      NullEqualToIsParentOrIsGrandparentOfCurrentLocation predicate = new NullEqualToIsParentOrIsGrandparentOfCurrentLocation(Suppliers.ofInstance(region));
      Hardware md = new HardwareBuilder().id("foo").location(orphanedZone).build();
      predicate.apply(md);
   }

   /**
    * Only the PROVIDER scope should have a null parent, It is an illegal state if a ZONE or REGION are orphaned
    * 
    */
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testThrowIllegalArgumentExceptionWhenWhenISpecifyAnOrphanedRegion() {
      NullEqualToIsParentOrIsGrandparentOfCurrentLocation predicate = new NullEqualToIsParentOrIsGrandparentOfCurrentLocation(Suppliers.ofInstance(orphanedRegion));
      Hardware md = new HardwareBuilder().id("foo").location(region).build();
      predicate.apply(md);
   }
   
   /**
    * Only the PROVIDER scope should have a null parent, It is an illegal state if a ZONE or REGION are orphaned
    * 
    */
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testThrowIllegalArgumentExceptionWhenWhenISpecifyAnOrphanedZone() {
      NullEqualToIsParentOrIsGrandparentOfCurrentLocation predicate = new NullEqualToIsParentOrIsGrandparentOfCurrentLocation(Suppliers.ofInstance(orphanedZone));
      Hardware md = new HardwareBuilder().id("foo").location(region).build();
      predicate.apply(md);
   }

}
