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

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.Node;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.Node.Status;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal.BaseNode;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ParseMetadata.CLBMetadata;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Everett Toews
 */
public class ParseNode implements Function<HttpResponse, Node>, InvocationContext<ParseNode> {

   private final ParseJson<Map<String, NodeWithCLBMetadata>> json;

   @Inject
   ParseNode(ParseJson<Map<String, NodeWithCLBMetadata>> json) {
      this.json = checkNotNull(json, "json");
   }

   @Override
   public Node apply(HttpResponse response) {
      Map<String, NodeWithCLBMetadata> map = json.apply(response);
      
      if (map == null || map.size() == 0)
         return null;
      
      NodeWithCLBMetadata nodeWithCLBMetadata = Iterables.get(map.values(), 0);
      Node node = Node.builder()
            .address(nodeWithCLBMetadata.getAddress())
            .port(nodeWithCLBMetadata.getPort())
            .condition(nodeWithCLBMetadata.getCondition())
            .type(nodeWithCLBMetadata.getType())
            .weight(nodeWithCLBMetadata.getWeight())
            .id(nodeWithCLBMetadata.id)
            .status(nodeWithCLBMetadata.status)
            .metadata(ParseMetadata.transformCLBMetadataToMetadata(nodeWithCLBMetadata.metadata))
            .build();
      
      return node;
   }

   @Override
   public ParseNode setContext(HttpRequest request) {
      return this;
   }
   
   /**
    * This class is here only to deal with the metadata format in CLB.
    */
   private static class NodeWithCLBMetadata extends BaseNode<Node> {
      private int id;
      private Status status;
      private List<CLBMetadata> metadata;
   }
}
