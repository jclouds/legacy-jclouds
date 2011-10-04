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
package org.jclouds.softlayer;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_CPU_REGEX;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_DISK0_TYPE;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_PACKAGE_NAME;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_PORT_SPEED;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_PRICES;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Builds properties used in SoftLayer Clients
 * 
 * @author Adrian Cole
 */
public class SoftLayerPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ENDPOINT, "https://api.softlayer.com/rest");
      properties.setProperty(PROPERTY_API_VERSION, "3");
      properties.setProperty(PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY, "" + 60 * 60 * 1000);
      properties.setProperty(PROPERTY_SOFTLAYER_VIRTUALGUEST_PACKAGE_NAME, "Cloud Server");
      // ex: for private (ex. don't share hardware) use "Private [0-9]+ x ([.0-9]+) GHz Core[s]?"
      // ex: for private and public use ".*[0-9]+ x ([.0-9]+) GHz Core[s]?"
      properties.setProperty(PROPERTY_SOFTLAYER_VIRTUALGUEST_CPU_REGEX, "[0-9]+ x ([0-9.]+) GHz Core[s]?");
      // SAN or LOCAL
      properties.setProperty(PROPERTY_SOFTLAYER_VIRTUALGUEST_DISK0_TYPE, "LOCAL");
      // 10, 100, 1000
      properties.setProperty(PROPERTY_SOFTLAYER_VIRTUALGUEST_PORT_SPEED, "10");
      properties.setProperty(PROPERTY_ISO3166_CODES, "SG,US-CA,US-TX,US-VA,US-WA,US-TX");
      Builder<String> prices = ImmutableSet.<String> builder();
      prices.add("21"); // 1 IP Address
      prices.add("55"); // Host Ping: categoryCode: monitoring, notification
      prices.add("57"); // Email and Ticket: categoryCode: notification
      prices.add("58"); // Automated Notification: categoryCode: response
      prices.add("1800"); // 0 GB Bandwidth: categoryCode: bandwidth
      prices.add("905"); // Reboot / Remote Console: categoryCode: remote_management
      prices.add("418"); // Nessus Vulnerability Assessment & Reporting: categoryCode:
                         // vulnerability_scanner
      prices.add("420"); // Unlimited SSL VPN Users & 1 PPTP VPN User per account: categoryCode:
                         // vpn_management
      properties.setProperty(PROPERTY_SOFTLAYER_VIRTUALGUEST_PRICES, Joiner.on(',').join(prices.build()));
      return properties;
   }

   public SoftLayerPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
