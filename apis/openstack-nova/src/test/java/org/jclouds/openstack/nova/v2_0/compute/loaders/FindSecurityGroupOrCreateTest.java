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
package org.jclouds.openstack.nova.v2_0.compute.loaders;

import static org.easymock.EasyMock.createMock;
import static org.testng.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.openstack.nova.v2_0.compute.loaders.FindSecurityGroupOrCreate;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.SecurityGroupInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndName;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneSecurityGroupNameAndPorts;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "FindSecurityGroupOrCreateTest")
public class FindSecurityGroupOrCreateTest {

   @Test
   public void testWhenNotFoundCreatesANewSecurityGroup() throws Exception {
      Predicate<AtomicReference<ZoneAndName>> returnSecurityGroupExistsInZone = Predicates.alwaysFalse();

      SecurityGroupInZone securityGroupInZone = createMock(SecurityGroupInZone.class);

      ZoneSecurityGroupNameAndPorts input = new ZoneSecurityGroupNameAndPorts("zone", "groupName", ImmutableSet
               .<Integer> of(22, 8080));

      Function<ZoneSecurityGroupNameAndPorts, SecurityGroupInZone> groupCreator = Functions.forMap(ImmutableMap
               .<ZoneSecurityGroupNameAndPorts, SecurityGroupInZone> of(input, securityGroupInZone));

      FindSecurityGroupOrCreate parser = new FindSecurityGroupOrCreate(
               returnSecurityGroupExistsInZone, groupCreator);

      assertEquals(parser.load(input), securityGroupInZone);

   }
   
   @Test
   public void testWhenFoundReturnsSecurityGroupFromAtomicReferenceValueUpdatedDuringPredicateCheck() throws Exception {
      final SecurityGroupInZone securityGroupInZone = createMock(SecurityGroupInZone.class);

      Predicate<AtomicReference<ZoneAndName>> returnSecurityGroupExistsInZone = new Predicate<AtomicReference<ZoneAndName>>(){

         @Override
         public boolean apply(AtomicReference<ZoneAndName> input) {
            input.set(securityGroupInZone);
            return true;
         }
         
      };

      ZoneAndName input = ZoneAndName.fromZoneAndName("zone", "groupName");

      Function<ZoneSecurityGroupNameAndPorts, SecurityGroupInZone> groupCreator = new Function<ZoneSecurityGroupNameAndPorts, SecurityGroupInZone>() {

         @Override
         public SecurityGroupInZone apply(ZoneSecurityGroupNameAndPorts input) {
            assert false;
            return null;
         }

      };

      FindSecurityGroupOrCreate parser = new FindSecurityGroupOrCreate(
               returnSecurityGroupExistsInZone, groupCreator);

      assertEquals(parser.load(input), securityGroupInZone);

   }

   
   @Test(expectedExceptions = IllegalStateException.class)
   public void testWhenFoundPredicateMustUpdateAtomicReference() throws Exception {

      Predicate<AtomicReference<ZoneAndName>> returnSecurityGroupExistsInZone = Predicates.alwaysTrue();

      ZoneAndName input = ZoneAndName.fromZoneAndName("zone", "groupName");

      Function<ZoneSecurityGroupNameAndPorts, SecurityGroupInZone> groupCreator = new Function<ZoneSecurityGroupNameAndPorts, SecurityGroupInZone>() {

         @Override
         public SecurityGroupInZone apply(ZoneSecurityGroupNameAndPorts input) {
            assert false;
            return null;
         }

      };

      FindSecurityGroupOrCreate parser = new FindSecurityGroupOrCreate(
               returnSecurityGroupExistsInZone, groupCreator);

      parser.load(input);

   }



   @Test(expectedExceptions = IllegalStateException.class)
   public void testWhenNotFoundInputMustBeZoneSecurityGroupNameAndPorts() throws Exception {
      Predicate<AtomicReference<ZoneAndName>> returnSecurityGroupExistsInZone = Predicates.alwaysFalse();

      ZoneAndName input = ZoneAndName.fromZoneAndName("zone", "groupName");

      Function<ZoneSecurityGroupNameAndPorts, SecurityGroupInZone> groupCreator = new Function<ZoneSecurityGroupNameAndPorts, SecurityGroupInZone>() {

         @Override
         public SecurityGroupInZone apply(ZoneSecurityGroupNameAndPorts input) {
            assert false;
            return null;
         }

      };

      FindSecurityGroupOrCreate parser = new FindSecurityGroupOrCreate(
               returnSecurityGroupExistsInZone, groupCreator);

      parser.load(input);

   }
}
