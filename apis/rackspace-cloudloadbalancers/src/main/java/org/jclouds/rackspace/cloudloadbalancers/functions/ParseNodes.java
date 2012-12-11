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
import javax.inject.Singleton;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.CallerArg0ToPagedIterable;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.rackspace.cloudloadbalancers.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.domain.Node;
import org.jclouds.rackspace.cloudloadbalancers.features.NodeApi;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseNodes.Nodes;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

/**
 * boiler plate until we determine a better way
 * 
 * @author Everett Toews
 */
@Beta
@Singleton
public class ParseNodes extends ParseJson<Nodes> {
   static class Nodes extends PaginatedCollection<Node> {

      @ConstructorProperties({ "nodes", "nodes_links" })
      protected Nodes(Iterable<Node> nodes, Iterable<Link> nodes_links) {
         super(nodes, nodes_links);
      }

   }

   @Inject
   public ParseNodes(Json json) {
      super(json, new TypeLiteral<Nodes>() { });
   }

   public static class ToPagedIterable extends CallerArg0ToPagedIterable<Node, ToPagedIterable> {

      private final CloudLoadBalancersApi api;
      private int lbId;

      @Inject
      protected ToPagedIterable(CloudLoadBalancersApi api) {
         this.api = checkNotNull(api, "api");
      }

      @Override
      public ToPagedIterable setContext(HttpRequest request) {
         String path = request.getEndpoint().getPath();
         int lastSlash = path.lastIndexOf('/');
         int secondLastSlash = path.lastIndexOf('/', lastSlash-1);
         
         lbId = Integer.valueOf(path.substring(secondLastSlash+1, lastSlash));
         
         return super.setContext(request);
      }

      @Override
      protected Function<Object, IterableWithMarker<Node>> markerToNextForCallingArg0(final String zone) {
         final NodeApi nodeApi = api.getNodeApiForZoneAndLoadBalancer(zone, lbId);
         
         return new Function<Object, IterableWithMarker<Node>>() {

            @Override
            public IterableWithMarker<Node> apply(Object input) {
               IterableWithMarker<Node> list = nodeApi.list(marker(input.toString()));
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
