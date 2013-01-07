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
package org.jclouds.savvis.vpdc.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Properties;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.savvis.vpdc.domain.FirewallRule;

import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Kedar Dave
 * 
 */
@Singleton
public class BindFirewallRuleToXmlPayload extends BindToStringPayload implements MapBinder {
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("BindFirewallRuleToXmlPayload needs parameters");

   }

   protected FirewallRule findRuleInArgsOrNull(GeneratedHttpRequest gRequest) {
      for (Object arg : gRequest.getInvocation().getArgs()) {
         if (arg instanceof FirewallRule) {
            return (FirewallRule) arg;
         } else if (arg instanceof FirewallRule[]) {
        	FirewallRule[] rules = (FirewallRule[]) arg;
            return (rules.length > 0) ? rules[0] : null;
         }
      }
      return null;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
            "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest gRequest = (GeneratedHttpRequest) request;
      request = super.bindToRequest(request,
            generateXml(findRuleInArgsOrNull(gRequest)));
      request.getPayload().getContentMetadata().setContentType(MediaType.APPLICATION_XML);
      return request;
   }

   public String generateXml(FirewallRule firewallRule) {
      checkNotNull(firewallRule, "FirewallRule");

      try {
         XMLBuilder rootBuilder = buildRoot();
         addFirewallRuleSection(rootBuilder, firewallRule);
         Properties outputProperties = new Properties();
         outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
         return rootBuilder.asString(outputProperties);
      } catch (Exception e) {
         return null;
      }
   }

   void addFirewallRuleSection(XMLBuilder rootBuilder, FirewallRule firewallRule) {
      XMLBuilder firewallRuleBuilder = rootBuilder.e("svvs:FirewallRule");
      firewallRuleBuilder.e("svvs:IsEnabled").t(firewallRule.isEnabled() ? "true" : "false");
      firewallRuleBuilder.e("svvs:Description").t("Server Tier Firewall Rule");
      firewallRuleBuilder.e("svvs:Type").t(firewallRule.getFirewallType());
      firewallRuleBuilder.e("svvs:Log").t(firewallRule.isLogged() ? "yes" : "no");
      firewallRuleBuilder.e("svvs:Policy").t(firewallRule.getPolicy());
      firewallRuleBuilder.e("svvs:Protocols").e("svvs:"+firewallRule.getProtocol()).t("true").up().up();
      firewallRuleBuilder.e("svvs:Port").t(firewallRule.getPort());
      firewallRuleBuilder.e("svvs:Destination").t(firewallRule.getDestination());
      firewallRuleBuilder.e("svvs:Source").t(firewallRule.getSource());
   }

   protected XMLBuilder buildRoot() throws ParserConfigurationException, FactoryConfigurationError {
      XMLBuilder rootBuilder = XMLBuilder.create("svvs:FirewallService")
            .a("xmlns:common", "http://schemas.dmtf.org/wbem/wscim/1/common")
            .a("xmlns:vApp", "http://www.vmware.com/vcloud/v0.8")
            .a("xmlns:rasd", "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData")
            .a("xmlns:vssd", "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_VirtualSystemSettingData")
            .a("xmlns:ovf", "http://schemas.dmtf.org/ovf/envelope/1")
            .a("xmlns:svvs", "http://schemas.api.sandbox.savvis.net/vpdci");
      return rootBuilder;
   }

   protected String ifNullDefaultTo(String value, String defaultValue) {
      return value != null ? value : checkNotNull(defaultValue, "defaultValue");
   }

}
