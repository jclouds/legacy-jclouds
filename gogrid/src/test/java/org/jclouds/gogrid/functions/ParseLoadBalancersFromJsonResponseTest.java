/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.gogrid.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.SortedSet;

import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.gogrid.config.GoGridContextModule;
import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.IpPortPair;
import org.jclouds.gogrid.domain.IpState;
import org.jclouds.gogrid.domain.LoadBalancer;
import org.jclouds.gogrid.domain.LoadBalancerOs;
import org.jclouds.gogrid.domain.LoadBalancerPersistenceType;
import org.jclouds.gogrid.domain.LoadBalancerState;
import org.jclouds.gogrid.domain.LoadBalancerType;
import org.jclouds.gogrid.functions.internal.CustomDeserializers;
import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * @author Oleksiy Yarmula
 */
@Test(groups = "unit", testName = "gogrid.ParseLoadBalancersFromJsonResponseTest")
public class ParseLoadBalancersFromJsonResponseTest {

   @Test
   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_get_load_balancer_list.json");

      ParseLoadBalancerListFromJsonResponse parser = new ParseLoadBalancerListFromJsonResponse(i
               .getInstance(Gson.class));
      SortedSet<LoadBalancer> response = parser.apply(is);

      LoadBalancer loadBalancer = new LoadBalancer(6372L, "Balancer", null, new IpPortPair(
               new Ip(1313082L, "204.51.240.181", "204.51.240.176/255.255.255.240", true,
                        IpState.ASSIGNED), 80), ImmutableSortedSet.of(new IpPortPair(
               new Ip(1313086L, "204.51.240.185", "204.51.240.176/255.255.255.240", true,
                        IpState.ASSIGNED), 80), new IpPortPair(new Ip(1313089L, "204.51.240.188",
               "204.51.240.176/255.255.255.240", true, IpState.ASSIGNED), 80)),
               LoadBalancerType.ROUND_ROBIN, LoadBalancerPersistenceType.NONE, LoadBalancerOs.F5,
               LoadBalancerState.ON);
      assertEquals(Iterables.getOnlyElement(response), loadBalancer);
   }

   Injector i = Guice.createInjector(new ParserModule() {
      @Override
      protected void configure() {
         bind(DateAdapter.class).to(GoGridContextModule.DateSecondsAdapter.class);
         super.configure();
      }

      @Provides
      @Singleton
      @SuppressWarnings( { "unused", "unchecked" })
      @com.google.inject.name.Named(Constants.PROPERTY_GSON_ADAPTERS)
      public Map<Class, Object> provideCustomAdapterBindings() {
         Map<Class, Object> bindings = Maps.newHashMap();
         bindings.put(LoadBalancerOs.class, new CustomDeserializers.LoadBalancerOsAdapter());
         bindings.put(LoadBalancerState.class, new CustomDeserializers.LoadBalancerStateAdapter());
         bindings.put(LoadBalancerPersistenceType.class,
                  new CustomDeserializers.LoadBalancerPersistenceTypeAdapter());
         bindings.put(LoadBalancerType.class, new CustomDeserializers.LoadBalancerTypeAdapter());
         bindings.put(IpState.class, new CustomDeserializers.IpStateAdapter());
         return bindings;
      }
   });
}
