/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.terremark.xml;

import java.util.Date;

import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class VirtualSystemHandler extends
         ParseSax.HandlerWithResult<org.jclouds.vcloud.terremark.domain.TerremarkVirtualSystem> {
   private StringBuilder currentText = new StringBuilder();

   protected DateService dateService;

   private String automaticRecoveryAction;
   private String automaticShutdownAction;
   private String automaticStartupAction;
   private String automaticStartupActionDelay;
   private String automaticStartupActionSequenceNumber;
   private String caption;
   private String configurationDataRoot;
   private String configurationFile;
   private String configurationID;
   private Date creationTime;
   private String description;
   private String elementName;
   private int instanceID;
   private String logDataRoot;
   private String recoveryFile;
   private String snapshotDataRoot;
   private String suspendDataRoot;
   private String swapFileDataRoot;
   private String virtualSystemIdentifier;
   private String virtualSystemType;

   private org.jclouds.vcloud.terremark.domain.TerremarkVirtualSystem system;

   private boolean skip;

   @Inject
   public VirtualSystemHandler(DateService dateService) {
      this.dateService = dateService;
   }

   public org.jclouds.vcloud.terremark.domain.TerremarkVirtualSystem getResult() {
      return system;
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (attributes.getIndex("xsi:nil") != -1
               || attributes.getIndex("xmlns") == -1
               || !"http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_VirtualSystemSettingData"
                        .equals(attributes.getValue("xmlns"))) {
         skip = true;
         return;
      } else {
         skip = false;
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (!skip) {
         if (qName.equals("AutomaticRecoveryAction")) {
            this.automaticRecoveryAction = currentText.toString().trim();
         } else if (qName.equals("AutomaticShutdownAction")) {
            this.automaticShutdownAction = currentText.toString().trim();
         } else if (qName.equals("AutomaticStartupAction")) {
            this.automaticStartupAction = currentText.toString().trim();
         } else if (qName.equals("AutomaticStartupActionDelay")) {
            this.automaticStartupActionDelay = currentText.toString().trim();
         } else if (qName.equals("AutomaticStartupActionSequenceNumber")) {
            this.automaticStartupActionSequenceNumber = currentText.toString().trim();
         } else if (qName.equals("Caption")) {
            this.caption = currentText.toString().trim();
         } else if (qName.equals("ConfigurationDataRoot")) {
            this.configurationDataRoot = currentText.toString().trim();
         } else if (qName.equals("ConfigurationDataRoot")) {
            this.configurationDataRoot = currentText.toString().trim();
         } else if (qName.equals("ConfigurationFile")) {
            this.configurationFile = currentText.toString().trim();
         } else if (qName.equals("ConfigurationID")) {
            this.configurationID = currentText.toString().trim();
         } else if (qName.equals("CreationTime")) {
            this.creationTime = dateService.iso8601DateParse(currentText.toString().trim());
         } else if (qName.equals("Description")) {
            this.description = currentText.toString().trim();
         } else if (qName.equals("ElementName")) {
            this.elementName = currentText.toString().trim();
         } else if (qName.equals("InstanceID")) {
            this.instanceID = Integer.parseInt(currentText.toString().trim());
         } else if (qName.equals("LogDataRoot")) {
            this.logDataRoot = currentText.toString().trim();
         } else if (qName.equals("RecoveryFile")) {
            this.recoveryFile = currentText.toString().trim();
         } else if (qName.equals("SnapshotDataRoot")) {
            this.snapshotDataRoot = currentText.toString().trim();
         } else if (qName.equals("SuspendDataRoot")) {
            this.suspendDataRoot = currentText.toString().trim();
         } else if (qName.equals("SwapFileDataRoot")) {
            this.swapFileDataRoot = currentText.toString().trim();
         } else if (qName.equals("VirtualSystemIdentifier")) {
            this.virtualSystemIdentifier = currentText.toString().trim();
         } else if (qName.equals("VirtualSystemType")) {
            this.virtualSystemType = currentText.toString().trim();
         } else if (qName.equals("System")) {
            this.system = new org.jclouds.vcloud.terremark.domain.TerremarkVirtualSystem(
                     automaticRecoveryAction, automaticShutdownAction, automaticStartupAction,
                     automaticStartupActionDelay, automaticStartupActionSequenceNumber, caption,
                     configurationDataRoot, configurationFile, configurationID, creationTime,
                     description, elementName, instanceID, logDataRoot, recoveryFile,
                     snapshotDataRoot, suspendDataRoot, swapFileDataRoot, virtualSystemIdentifier,
                     virtualSystemType);
            this.automaticRecoveryAction = null;
            this.automaticShutdownAction = null;
            this.automaticStartupAction = null;
            this.automaticStartupActionDelay = null;
            this.automaticStartupActionSequenceNumber = null;
            this.caption = null;
            this.configurationDataRoot = null;
            this.configurationFile = null;
            this.configurationID = null;
            this.creationTime = null;
            this.description = null;
            this.elementName = null;
            this.instanceID = -1;
            this.logDataRoot = null;
            this.recoveryFile = null;
            this.snapshotDataRoot = null;
            this.suspendDataRoot = null;
            this.swapFileDataRoot = null;
            this.virtualSystemIdentifier = null;
            this.virtualSystemType = null;
         }
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
