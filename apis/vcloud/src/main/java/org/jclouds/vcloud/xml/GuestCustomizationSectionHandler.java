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
package org.jclouds.vcloud.xml;

import static org.jclouds.vcloud.util.Utils.newReferenceType;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.jclouds.vcloud.domain.GuestCustomizationSection;
import org.jclouds.vcloud.domain.ReferenceType;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public class GuestCustomizationSectionHandler extends ParseSax.HandlerWithResult<GuestCustomizationSection> {
   protected StringBuilder currentText = new StringBuilder();
   private ReferenceType guest;
   private ReferenceType edit;

   protected String info;
   protected String name;
   protected Boolean enabled;
   protected Boolean changeSid;
   protected String virtualMachineId;
   protected Boolean joinDomainEnabled;
   protected Boolean useOrgSettings;
   protected String domainName;
   protected String domainUserName;
   protected String domainUserPassword;
   protected Boolean adminPasswordEnabled;
   protected Boolean adminPasswordAuto;
   protected String adminPassword;
   protected Boolean resetPasswordRequired;
   protected String customizationScript;
   protected String computerName;

   public GuestCustomizationSection getResult() {
      GuestCustomizationSection system = new GuestCustomizationSection(guest.getType(), guest.getHref(), info, enabled,
            changeSid, virtualMachineId, joinDomainEnabled, useOrgSettings, domainName, domainUserName,
            domainUserPassword, adminPasswordEnabled, adminPasswordAuto, adminPassword, resetPasswordRequired,
            customizationScript, computerName, edit);
      this.guest = null;
      this.info = null;
      this.edit = null;
      this.enabled = null;
      this.changeSid = null;
      this.virtualMachineId = null;
      this.joinDomainEnabled = null;
      this.useOrgSettings = null;
      this.domainName = null;
      this.domainUserName = null;
      this.domainUserPassword = null;
      this.adminPasswordEnabled = null;
      this.adminPasswordAuto = null;
      this.adminPassword = null;
      this.resetPasswordRequired = null;
      this.customizationScript = null;
      this.computerName = null;
      return system;
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      this.currentText = new StringBuilder();
      if (qName.endsWith("GuestCustomizationSection")) {
         guest = newReferenceType(attributes);
      } else if (qName.endsWith("Link") && "edit".equals(attributes.get("rel"))) {
         edit = newReferenceType(attributes);
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (qName.endsWith("Info")) {
         this.info = currentOrNull();
      } else if (qName.endsWith("AdminPasswordEnabled")) {
         this.adminPasswordEnabled = Boolean.parseBoolean(currentOrNull());
      } else if (qName.endsWith("JoinDomainEnabled")) {
         this.joinDomainEnabled = Boolean.parseBoolean(currentOrNull());
      } else if (qName.endsWith("Enabled")) {
         this.enabled = Boolean.parseBoolean(currentOrNull());
      } else if (qName.endsWith("ChangeSid")) {
         this.changeSid = Boolean.parseBoolean(currentOrNull());
      } else if (qName.endsWith("VirtualMachineId")) {
         this.virtualMachineId = currentOrNull();
      } else if (qName.endsWith("UseOrgSettings")) {
         this.useOrgSettings = Boolean.parseBoolean(currentOrNull());
      } else if (qName.endsWith("DomainName")) {
         this.domainName = currentOrNull();
      } else if (qName.endsWith("DomainUserName")) {
         this.domainUserName = currentOrNull();
      } else if (qName.endsWith("DomainUserPassword")) {
         this.domainUserPassword = currentOrNull();
      } else if (qName.endsWith("AdminPasswordAuto")) {
         this.adminPasswordAuto = Boolean.parseBoolean(currentOrNull());
      } else if (qName.endsWith("AdminPassword")) {
         this.adminPassword = currentOrNull();
      } else if (qName.endsWith("ResetPasswordRequired")) {
         this.resetPasswordRequired = Boolean.parseBoolean(currentOrNull());
      } else if (qName.endsWith("CustomizationScript")) {
         this.customizationScript = currentOrNull();
         if (this.customizationScript != null)
            customizationScript = customizationScript.replace("&gt;", ">");
      } else if (qName.endsWith("ComputerName")) {
         this.computerName = currentOrNull();
      } else if (qName.endsWith("Name")) {
         this.name = currentOrNull();
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString();
      return returnVal.equals("") ? null : returnVal;
   }
}
