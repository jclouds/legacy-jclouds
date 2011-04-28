/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.savvis.vpdc.features;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.savvis.vpdc.domain.FirewallRule;
import org.jclouds.savvis.vpdc.domain.Task;

/**
 * Provides access to Symphony VPDC resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://api.sandbox.symphonyvpdc.savvis.net/doc/spec/api/" />
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface FirewallClient {
	
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