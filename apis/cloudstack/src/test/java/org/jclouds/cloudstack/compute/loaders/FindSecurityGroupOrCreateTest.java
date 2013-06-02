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
package org.jclouds.cloudstack.compute.loaders;

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

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.IngressRule;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.domain.ZoneAndName;
import org.jclouds.cloudstack.domain.ZoneSecurityGroupNamePortsCidrs;
import org.jclouds.cloudstack.features.AsyncJobClient;
import org.jclouds.cloudstack.features.SecurityGroupClient;
import org.jclouds.cloudstack.features.ZoneClient;
import org.jclouds.cloudstack.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.cloudstack.functions.ZoneIdToZone;
import org.jclouds.cloudstack.predicates.JobComplete;
import org.jclouds.cloudstack.suppliers.ZoneIdToZoneSupplier;
import org.testng.annotations.Test;

import com.google.common.base.Function;
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
 * @author Adam Lowe
 * @author Andrew Bayer
 */
@Test(groups = "unit", testName = "FindSecurityGroupOrCreateTest")
public class FindSecurityGroupOrCreateTest {

   @Test
   public void testLoad() throws UnknownHostException {
      final CloudStackClient client = createMock(CloudStackClient.class);
      SecurityGroupClient secClient = createMock(SecurityGroupClient.class);
      ZoneClient zoneClient = createMock(ZoneClient.class);
      AsyncJobClient jobClient = createMock(AsyncJobClient.class);
      
      SecurityGroup group = createMock(SecurityGroup.class);
      
      Zone zone = createMock(Zone.class);

      expect(group.getIngressRules()).andReturn(ImmutableSet.<IngressRule> of());
      expect(group.getId()).andReturn("sec-1234").anyTimes();
      expect(zone.isSecurityGroupsEnabled()).andReturn(true);
      
      expect(client.getSecurityGroupClient()).andReturn(secClient)
         .anyTimes();
      expect(client.getZoneClient()).andReturn(zoneClient);
      expect(client.getAsyncJobClient()).andReturn(jobClient).anyTimes();

      expect(zoneClient.getZone("zone-1")).andReturn(zone);
      expect(secClient.getSecurityGroupByName("group-1")).andReturn(null);
      expect(secClient.createSecurityGroup("group-1")).andReturn(group);
      expect(secClient.authorizeIngressPortsToCIDRs("sec-1234",
                                                    "TCP",
                                                    22,
                                                    22,
                                                    ImmutableSet.of("0.0.0.0/0"))).andReturn("job-1234");

      replay(client, secClient, zoneClient, zone, group);

      ZoneSecurityGroupNamePortsCidrs input = ZoneSecurityGroupNamePortsCidrs.builder()
         .zone("zone-1")
         .name("group-1")
         .ports(ImmutableSet.of(22))
         .cidrs(ImmutableSet.<String> of()).build();
      
      FindSecurityGroupOrCreate parser = Guice.createInjector(new AbstractModule() {
            
            @Override
            protected void configure() {
               bind(new TypeLiteral<Supplier<String>>() {
                  }).toInstance(Suppliers.ofInstance("1"));
               bind(CloudStackClient.class).toInstance(client);
               bind(new TypeLiteral<CacheLoader<String, Zone>>() {}).
                  to(ZoneIdToZone.class);
               bind(new TypeLiteral<Supplier<LoadingCache<String, Zone>>>() {}).
                  to(ZoneIdToZoneSupplier.class);
               bind(String.class).annotatedWith(Names.named(PROPERTY_SESSION_INTERVAL)).toInstance("60");

               bind(new TypeLiteral<Function<ZoneSecurityGroupNamePortsCidrs, SecurityGroup>>() {
                  }).to(CreateSecurityGroupIfNeeded.class);
               
               bind(new TypeLiteral<CacheLoader<ZoneAndName, SecurityGroup>>() {
                  }).to(FindSecurityGroupOrCreate.class);
            }
            
            @Provides
            @Singleton
            protected Predicate<String> jobComplete(JobComplete jobComplete) {
               return retry(jobComplete, 1200, 1, 5, SECONDS);
            }

      }).getInstance(FindSecurityGroupOrCreate.class);

      assertEquals(parser.load(input), group);

      verify(client, secClient, zoneClient, zone, group);
   }

   
   @Test
   public void testLoadAlreadyExists() throws UnknownHostException {
      final CloudStackClient client = createMock(CloudStackClient.class);
      SecurityGroupClient secClient = createMock(SecurityGroupClient.class);
      ZoneClient zoneClient = createMock(ZoneClient.class);
      AsyncJobClient jobClient = createMock(AsyncJobClient.class);
      
      SecurityGroup group = createMock(SecurityGroup.class);
      
      Zone zone = createMock(Zone.class);

      expect(group.getId()).andReturn("sec-1234").anyTimes();
      
      expect(client.getSecurityGroupClient()).andReturn(secClient)
         .anyTimes();
      expect(client.getZoneClient()).andReturn(zoneClient);
      expect(client.getAsyncJobClient()).andReturn(jobClient).anyTimes();

      expect(secClient.getSecurityGroupByName("group-1")).andReturn(group);

      replay(client, secClient, zoneClient, zone, group);

      ZoneSecurityGroupNamePortsCidrs input = ZoneSecurityGroupNamePortsCidrs.builder()
         .zone("zone-1")
         .name("group-1")
         .ports(ImmutableSet.of(22))
         .cidrs(ImmutableSet.<String> of()).build();
      
      FindSecurityGroupOrCreate parser = Guice.createInjector(new AbstractModule() {
            
            @Override
            protected void configure() {
               bind(new TypeLiteral<Supplier<String>>() {
                  }).toInstance(Suppliers.ofInstance("1"));
               bind(CloudStackClient.class).toInstance(client);
               bind(new TypeLiteral<CacheLoader<String, Zone>>() {}).
                  to(ZoneIdToZone.class);
               bind(new TypeLiteral<Supplier<LoadingCache<String, Zone>>>() {}).
                  to(ZoneIdToZoneSupplier.class);
               bind(String.class).annotatedWith(Names.named(PROPERTY_SESSION_INTERVAL)).toInstance("60");

               bind(new TypeLiteral<Function<ZoneSecurityGroupNamePortsCidrs, SecurityGroup>>() {
                  }).to(CreateSecurityGroupIfNeeded.class);
               
               bind(new TypeLiteral<CacheLoader<ZoneAndName, SecurityGroup>>() {
                  }).to(FindSecurityGroupOrCreate.class);
            }
            
            @Provides
            @Singleton
            protected Predicate<String> jobComplete(JobComplete jobComplete) {
               return retry(jobComplete, 1200, 1, 5, SECONDS);
            }

      }).getInstance(FindSecurityGroupOrCreate.class);

      assertEquals(parser.load(input), group);

      verify(client, secClient, zoneClient, zone, group);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testLoadZoneNoSecurityGroups() throws UnknownHostException {
      final CloudStackClient client = createMock(CloudStackClient.class);
      SecurityGroupClient secClient = createMock(SecurityGroupClient.class);
      ZoneClient zoneClient = createMock(ZoneClient.class);
      AsyncJobClient jobClient = createMock(AsyncJobClient.class);
      
      SecurityGroup group = createMock(SecurityGroup.class);
      
      Zone zone = createMock(Zone.class);

      expect(zone.isSecurityGroupsEnabled()).andReturn(false);
      
      expect(client.getSecurityGroupClient()).andReturn(secClient)
         .anyTimes();
      expect(client.getZoneClient()).andReturn(zoneClient);
      expect(client.getAsyncJobClient()).andReturn(jobClient).anyTimes();

      expect(zoneClient.getZone("zone-1")).andReturn(zone);
      expect(secClient.getSecurityGroupByName("group-1")).andReturn(null);

      replay(client, secClient, zoneClient, zone, group);

      ZoneSecurityGroupNamePortsCidrs input = ZoneSecurityGroupNamePortsCidrs.builder()
         .zone("zone-1")
         .name("group-1")
         .ports(ImmutableSet.of(22))
         .cidrs(ImmutableSet.<String> of()).build();
      
      FindSecurityGroupOrCreate parser = Guice.createInjector(new AbstractModule() {
            
            @Override
            protected void configure() {
               bind(new TypeLiteral<Supplier<String>>() {
                  }).toInstance(Suppliers.ofInstance("1"));
               bind(CloudStackClient.class).toInstance(client);
               bind(new TypeLiteral<CacheLoader<String, Zone>>() {}).
                  to(ZoneIdToZone.class);
               bind(new TypeLiteral<Supplier<LoadingCache<String, Zone>>>() {}).
                  to(ZoneIdToZoneSupplier.class);
               bind(String.class).annotatedWith(Names.named(PROPERTY_SESSION_INTERVAL)).toInstance("60");

               bind(new TypeLiteral<Function<ZoneSecurityGroupNamePortsCidrs, SecurityGroup>>() {
                  }).to(CreateSecurityGroupIfNeeded.class);
               
               bind(new TypeLiteral<CacheLoader<ZoneAndName, SecurityGroup>>() {
                  }).to(FindSecurityGroupOrCreate.class);
            }
            
            @Provides
            @Singleton
            protected Predicate<String> jobComplete(JobComplete jobComplete) {
               return retry(jobComplete, 1200, 1, 5, SECONDS);
            }

      }).getInstance(FindSecurityGroupOrCreate.class);

      assertEquals(parser.load(input), group);

      verify(client, secClient, zoneClient, zone, group);
   }

}
