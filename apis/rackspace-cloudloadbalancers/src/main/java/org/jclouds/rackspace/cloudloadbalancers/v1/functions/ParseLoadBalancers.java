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
package org.jclouds.rackspace.cloudloadbalancers.v1.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.openstack.v2_0.options.PaginationOptions.Builder.marker;

import java.beans.ConstructorProperties;
import java.net.URI;

import javax.inject.Inject;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.collect.internal.Arg0ToPagedIterable;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.rackspace.cloudloadbalancers.v1.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.LoadBalancerApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ConvertLB.Factory;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;
import com.google.common.base.Optional;
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
      return setEndpointAndRegion(request.getEndpoint());
   }

   ParseLoadBalancers setEndpointAndRegion(URI endpoint) {
      String region = endpoint.getHost().substring(0, endpoint.getHost().indexOf('.'));
      
      this.convertLB = factory.createForEndpointAndRegion(endpoint, region);
      
      return this;
   }

   static class LoadBalancers extends PaginatedCollection<LB> {

      @ConstructorProperties({ "loadBalancers", "loadBalancers_links" })
      protected LoadBalancers(Iterable<LB> loadBalancers, Iterable<Link> loadBalancers_links) {
         super(loadBalancers, loadBalancers_links);
      }

   }

   public static class ToPagedIterable extends Arg0ToPagedIterable.FromCaller<LoadBalancer, ToPagedIterable> {

      private final CloudLoadBalancersApi api;

      @Inject
      protected ToPagedIterable(CloudLoadBalancersApi api) {
         this.api = checkNotNull(api, "api");
      }

      @Override
      protected Function<Object, IterableWithMarker<LoadBalancer>> markerToNextForArg0(Optional<Object> arg0) {
         String zone = arg0.isPresent() ? arg0.get().toString() : null;
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
