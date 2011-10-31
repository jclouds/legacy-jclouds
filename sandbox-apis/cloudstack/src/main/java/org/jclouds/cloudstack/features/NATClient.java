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
package org.jclouds.cloudstack.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.options.CreateIPForwardingRuleOptions;
import org.jclouds.cloudstack.options.ListIPForwardingRulesOptions;
import org.jclouds.concurrent.Timeout;

/**
 * Provides synchronous access to CloudStack IPForwardingRule features.
 * <p/>
 * 
 * @see IPForwardingRuleAsyncClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api/TOC_User.html" />
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface NATClient {
   /**
    * List the ip forwarding rules
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return IPForwardingRulees matching query, or empty set, if no
    *         IPForwardingRulees are found
    */
   Set<IPForwardingRule> listIPForwardingRules(ListIPForwardingRulesOptions... options);

   /**
    * get a specific IPForwardingRule by id
    * 
    * @param id
    *           IPForwardingRule to get
    * @return IPForwardingRule or null if not found
    */
   IPForwardingRule getIPForwardingRule(long id);

   /**
    * Creates an ip forwarding rule
    * 
    * @param IPAddressId
    *           the public IP address id of the forwarding rule, already
    *           associated via associateIp
    * @param protocol
    *           the protocol for the rule. Valid values are TCP or UDP.
    * @param startPort
    *           the start port for the rule
    * @return response used to track creation
    */
   AsyncCreateResponse createIPForwardingRule(long IPAddressId, String protocol, int startPort,
         CreateIPForwardingRuleOptions... options);

   /**
    * Deletes an ip forwarding rule
    * 
    * @param id
    *           the id of the forwarding rule
    */
   Long deleteIPForwardingRule(long id);

   AsyncCreateResponse enableStaticNATForVirtualMachine(long virtualMachineId, long IPAddressId);
}
