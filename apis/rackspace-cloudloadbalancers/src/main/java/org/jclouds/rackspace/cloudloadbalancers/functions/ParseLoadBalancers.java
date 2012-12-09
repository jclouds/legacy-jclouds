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
package org.jclouds.rackspace.cloudloadbalancers.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.openstack.v2_0.options.PaginationOptions.Builder.marker;

import java.beans.ConstructorProperties;

import javax.inject.Inject;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.collect.internal.CallerArg0ToPagedIterable;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.rackspace.cloudloadbalancers.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.features.LoadBalancerApi;
import org.jclouds.rackspace.cloudloadbalancers.functions.ConvertLB.Factory;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
public class ParseLoadBalancers implements Function<HttpResponse, IterableWithMarker<LoadBalancer>>,
         InvocationContext<ParseLoadBalancers> {

   private final ParseJson<LoadBalancers> json;
   private final Factory factory;

   private ConvertLB convertLB;

   @Inject
   ParseLoadBalancers(ParseJson<LoadBalancers> json, ConvertLB.Factory factory) {
      this.json = checkNotNull(json, "json");
      this.factory = checkNotNull(factory, "factory");
   }

   @SuppressWarnings("unchecked")
   @Override
   public IterableWithMarker<LoadBalancer> apply(HttpResponse arg0) {
      LoadBalancers lbs = json.apply(arg0);
      
      if (lbs.size() == 0)
         return IterableWithMarkers.EMPTY;
      
      Iterable<LoadBalancer> transform = Iterables.transform(lbs, convertLB);      
      IterableWithMarker<LoadBalancer> iterableWithMarker = IterableWithMarkers.from(transform);
      
      return iterableWithMarker;
   }

   @Override
   public ParseLoadBalancers setContext(HttpRequest request) {
      return setRegion(request.getEndpoint().getHost().substring(0, request.getEndpoint().getHost().indexOf('.')));
   }

   ParseLoadBalancers setRegion(String region) {
      this.convertLB = factory.createForRegion(region);
      return this;
   }

   static class LoadBalancers extends PaginatedCollection<LB> {

      @ConstructorProperties({ "loadBalancers", "loadBalancers_links" })
      protected LoadBalancers(Iterable<LB> loadBalancers, Iterable<Link> loadBalancers_links) {
         super(loadBalancers, loadBalancers_links);
      }

   }

   public static class ToPagedIterable extends CallerArg0ToPagedIterable<LoadBalancer, ToPagedIterable> {

      private final CloudLoadBalancersApi api;

      @Inject
      protected ToPagedIterable(CloudLoadBalancersApi api) {
         this.api = checkNotNull(api, "api");
      }

      @Override
      protected Function<Object, IterableWithMarker<LoadBalancer>> markerToNextForCallingArg0(final String zone) {
         final LoadBalancerApi loadBalancerApi = api.getLoadBalancerApiForZone(zone);
         
         return new Function<Object, IterableWithMarker<LoadBalancer>>() {

            @Override
            public IterableWithMarker<LoadBalancer> apply(Object input) {
               IterableWithMarker<LoadBalancer> list = loadBalancerApi.list(marker(input.toString()));
               return list;
            }

            @Override
            public String toString() {
               return "list()";
            }
         };
      }

   }
}
