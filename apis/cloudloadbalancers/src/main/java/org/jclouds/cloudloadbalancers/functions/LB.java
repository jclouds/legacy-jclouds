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
package org.jclouds.cloudloadbalancers.functions;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jclouds.cloudloadbalancers.domain.Node;
import org.jclouds.cloudloadbalancers.domain.VirtualIP;
import org.jclouds.cloudloadbalancers.domain.LoadBalancer.Status;
import org.jclouds.cloudloadbalancers.domain.internal.BaseLoadBalancer;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * only here as the datatype for cloudloadbalancers is awkward.
 * 
 **/
class LB extends BaseLoadBalancer<Node, LB> {
   int id;
   Status status;
   Set<VirtualIP> virtualIps = Sets.newLinkedHashSet();
   Map<String, String> sessionPersistence = Maps.newLinkedHashMap();
   Map<String, String> cluster = Maps.newLinkedHashMap();
   Map<String, Date> created = Maps.newLinkedHashMap();
   Map<String, Date> updated = Maps.newLinkedHashMap();
   Map<String, Boolean> connectionLogging = Maps.newLinkedHashMap();
}
