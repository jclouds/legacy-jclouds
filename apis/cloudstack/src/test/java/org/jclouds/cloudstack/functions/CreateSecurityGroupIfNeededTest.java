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
package org.jclouds.cloudstack.functions;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.util.Predicates2.retry;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;
import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackApi;
import org.jclouds.cloudstack.domain.IngressRule;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.domain.ZoneSecurityGroupNamePortsCidrs;
import org.jclouds.cloudstack.features.AsyncJobApi;
import org.jclouds.cloudstack.features.SecurityGroupApi;
import org.jclouds.cloudstack.features.ZoneApi;
import org.jclouds.cloudstack.functions.ZoneIdToZone;
import org.jclouds.cloudstack.predicates.JobComplete;
import org.jclouds.cloudstack.suppliers.ZoneIdToZoneSupplier;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * @author Andrew Bayer
 */
@Test(groups = "unit", testName = "CreateSecurityGroupIfNeededTest")
public class CreateSecurityGroupIfNeededTest {

   @Test
   public void testApply() throws UnknownHostException {
      final CloudStackApi client = createMock(CloudStackApi.class);
      SecurityGroupApi secClient = createMock(SecurityGroupApi.class);
      ZoneApi zoneClient = createMock(ZoneApi.class);
      AsyncJobApi jobClient = createMock(AsyncJobApi.class);
      
      SecurityGroup group = createMock(SecurityGroup.class);
      
      Zone zone = createMock(Zone.class);

      expect(group.getIngressRules()).andReturn(ImmutableSet.<IngressRule> of());
      expect(group.getId()).andReturn("sec-1234").anyTimes();
      expect(zone.isSecurityGroupsEnabled()).andReturn(true);
      
      expect(client.getSecurityGroupApi()).andReturn(secClient)
         .anyTimes();
      expect(client.getZoneApi()).andReturn(zoneClient);
      expect(client.getAsyncJobApi()).andReturn(jobClient).anyTimes();

      expect(zoneClient.getZone("zone-abc1")).andReturn(zone);
      expect(secClient.createSecurityGroup("group-1")).andReturn(group);
      expect(secClient.authorizeIngressPortsToCIDRs("sec-1234",
                                                    "TCP",
                                                    22,
                                                    22,
                                                    ImmutableSet.of("0.0.0.0/0"))).andReturn("job-1234");

      replay(client, secClient, zoneClient, zone, group);

      ZoneSecurityGroupNamePortsCidrs input = ZoneSecurityGroupNamePortsCidrs.builder()
         .zone("zone-abc1")
         .name("group-1")
         .ports(ImmutableSet.of(22))
         .cidrs(ImmutableSet.<String> of()).build();
      
      CreateSecurityGroupIfNeeded parser = Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
               bind(new TypeLiteral<Supplier<String>>() {
                  }).toInstance(Suppliers.ofInstance("1"));
               bind(CloudStackApi.class).toInstance(client);
               bind(new TypeLiteral<CacheLoader<String, Zone>>() {}).
                  to(ZoneIdToZone.class);
               bind(new TypeLiteral<Supplier<LoadingCache<String, Zone>>>() {}).
                  to(ZoneIdToZoneSupplier.class);
               bind(String.class).annotatedWith(Names.named(PROPERTY_SESSION_INTERVAL)).toInstance("60");
            }
            
            @Provides
            @Singleton
            protected Predicate<String> jobComplete(JobComplete jobComplete) {
               return retry(jobComplete, 1200, 1, 5, SECONDS);
            }
            
         }).getInstance(CreateSecurityGroupIfNeeded.class);
      
      assertEquals(parser.apply(input), group);

      verify(client, secClient, zoneClient, zone, group);
   }

   
   @Test
   public void testApplyGroupAlreadyExists() throws UnknownHostException {
      final CloudStackApi client = createMock(CloudStackApi.class);
      SecurityGroupApi secClient = createMock(SecurityGroupApi.class);
      ZoneApi zoneClient = createMock(ZoneApi.class);
      AsyncJobApi jobClient = createMock(AsyncJobApi.class);
      
      SecurityGroup group = createMock(SecurityGroup.class);
      
      Zone zone = createMock(Zone.class);

      expect(group.getId()).andReturn("sec-1234").anyTimes();
      expect(zone.isSecurityGroupsEnabled()).andReturn(true);
      
      expect(client.getSecurityGroupApi()).andReturn(secClient)
         .anyTimes();
      expect(client.getZoneApi()).andReturn(zoneClient);
      expect(client.getAsyncJobApi()).andReturn(jobClient).anyTimes();

      expect(zoneClient.getZone("zone-abc2")).andReturn(zone);
      expect(secClient.createSecurityGroup("group-1")).andThrow(new IllegalStateException());
      expect(secClient.getSecurityGroupByName("group-1")).andReturn(group);

      replay(client, secClient, zoneClient, zone, group);

      ZoneSecurityGroupNamePortsCidrs input = ZoneSecurityGroupNamePortsCidrs.builder()
         .zone("zone-abc2")
         .name("group-1")
         .ports(ImmutableSet.of(22))
         .cidrs(ImmutableSet.<String> of()).build();
      
      CreateSecurityGroupIfNeeded parser = Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
               bind(new TypeLiteral<Supplier<String>>() {
                  }).toInstance(Suppliers.ofInstance("1"));
               bind(CloudStackApi.class).toInstance(client);
               bind(new TypeLiteral<CacheLoader<String, Zone>>() {}).
                  to(ZoneIdToZone.class);
               bind(new TypeLiteral<Supplier<LoadingCache<String, Zone>>>() {}).
                  to(ZoneIdToZoneSupplier.class);
               bind(String.class).annotatedWith(Names.named(PROPERTY_SESSION_INTERVAL)).toInstance("60");
            }
            
            @Provides
            @Singleton
            protected Predicate<String> jobComplete(JobComplete jobComplete) {
               return retry(jobComplete, 1200, 1, 5, SECONDS);
            }
            
         }).getInstance(CreateSecurityGroupIfNeeded.class);
      
      assertEquals(parser.apply(input), group);

      verify(client, secClient, zoneClient, zone, group);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testApplyZoneNoSecurityGroups() throws UnknownHostException {
      final CloudStackApi client = createMock(CloudStackApi.class);
      SecurityGroupApi secClient = createMock(SecurityGroupApi.class);
      ZoneApi zoneClient = createMock(ZoneApi.class);
      AsyncJobApi jobClient = createMock(AsyncJobApi.class);
      
      SecurityGroup group = createMock(SecurityGroup.class);
      
      Zone zone = createMock(Zone.class);

      expect(zone.isSecurityGroupsEnabled()).andReturn(false);
      
      expect(client.getZoneApi()).andReturn(zoneClient);

      expect(zoneClient.getZone("zone-abc3")).andReturn(zone);

      replay(client, zoneClient, zone);

      ZoneSecurityGroupNamePortsCidrs input = ZoneSecurityGroupNamePortsCidrs.builder()
         .zone("zone-abc3")
         .name("group-1")
         .ports(ImmutableSet.of(22))
         .cidrs(ImmutableSet.<String> of()).build();
      
      CreateSecurityGroupIfNeeded parser = Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
               bind(new TypeLiteral<Supplier<String>>() {
                  }).toInstance(Suppliers.ofInstance("1"));
               bind(CloudStackApi.class).toInstance(client);
               bind(new TypeLiteral<CacheLoader<String, Zone>>() {}).
                  to(ZoneIdToZone.class);
               bind(new TypeLiteral<Supplier<LoadingCache<String, Zone>>>() {}).
                  to(ZoneIdToZoneSupplier.class);
               bind(String.class).annotatedWith(Names.named(PROPERTY_SESSION_INTERVAL)).toInstance("60");
            }
            
            @Provides
            @Singleton
            protected Predicate<String> jobComplete(JobComplete jobComplete) {
               return retry(jobComplete, 1200, 1, 5, SECONDS);
            }
            
         }).getInstance(CreateSecurityGroupIfNeeded.class);

      assertEquals(parser.apply(input), group);

      verify(client, zoneClient, zone);
   }

}
