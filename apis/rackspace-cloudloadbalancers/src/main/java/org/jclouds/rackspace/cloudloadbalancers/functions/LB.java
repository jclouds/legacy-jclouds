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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.rackspace.cloudloadbalancers.domain.AccessRuleWithId;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer.Status;
import org.jclouds.rackspace.cloudloadbalancers.domain.Node;
import org.jclouds.rackspace.cloudloadbalancers.domain.SSLTermination;
import org.jclouds.rackspace.cloudloadbalancers.domain.SourceAddresses;
import org.jclouds.rackspace.cloudloadbalancers.domain.VirtualIPWithId;
import org.jclouds.rackspace.cloudloadbalancers.domain.internal.BaseLoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseMetadata.CLBMetadata;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Only here as the datatype for cloudloadbalancers is awkward.
 */
class LB extends BaseLoadBalancer<Node, LB> {
   int id;
   int nodeCount;
   Status status;
   Set<VirtualIPWithId> virtualIps = Sets.newLinkedHashSet();
   Map<String, String> cluster = Maps.newLinkedHashMap();
   Map<String, Date> created = Maps.newLinkedHashMap();
   Map<String, Date> updated = Maps.newLinkedHashMap();
   Map<String, Boolean> contentCaching = Maps.newLinkedHashMap();
   SSLTermination sslTermination;
   SourceAddresses sourceAddresses;
   Set<AccessRuleWithId> accessList;
   List<CLBMetadata> metadata;

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      
      LB that = LB.class.cast(obj);      
      return Objects.equal(this.id, that.id);
   }
}
