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
package org.jclouds.savvis.vpdc.features;

import org.jclouds.savvis.vpdc.domain.FirewallRule;
import org.jclouds.savvis.vpdc.domain.Task;

/**
 * Provides access to Symphony VPDC resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://api.sandbox.savvis.net/doc/spec/api/" />
 * @author Adrian Cole
 */
public interface FirewallApi {
	
	/**
	 * Add a new firewall rule
	 * 		
	 * @param billingSiteId
	 * 		billing site Id, or null for default
	 * @param vpdcId
	 * 		vpdc Id
	 * @param firewallRule
	 * 		firewall rule to be added
	 * @return
	 */
	Task addFirewallRule(String billingSiteId, String vpdcId, FirewallRule firewallRule);
	
	/**
	 * Delete a firewall rule
	 * 		
	 * @param billingSiteId
	 * 		billing site Id, or null for default
	 * @param vpdcId
	 * 		vpdc Id
	 * @param firewallRule
	 * 		firewall rule to be deleted
	 * @return
	 */
	Task deleteFirewallRule(String billingSiteId, String vpdcId, FirewallRule firewallRule);
}
