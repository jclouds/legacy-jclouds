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
package org.jclouds.cim.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.net.URI;

import org.jclouds.cim.VirtualSystemSettingData;
import org.jclouds.cim.VirtualSystemSettingData.AutomaticRecoveryAction;
import org.jclouds.cim.VirtualSystemSettingData.AutomaticShutdownAction;
import org.jclouds.cim.VirtualSystemSettingData.AutomaticStartupAction;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.base.Splitter;

/**
 * @author Adrian Cole
 */
public class VirtualSystemSettingDataHandler extends ParseSax.HandlerWithResult<VirtualSystemSettingData> {
   protected StringBuilder currentText = new StringBuilder();

   protected VirtualSystemSettingData.Builder builder = VirtualSystemSettingData.builder();

   public VirtualSystemSettingData getResult() {
      try {
         return builder.build();
      } finally {
         builder = VirtualSystemSettingData.builder();
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      String current = currentOrNull(currentText);
      if (current != null) {
         if (equalsOrSuffix(qName, "ElementName")) {
            builder.elementName(current);
         } else if (equalsOrSuffix(qName, "InstanceID")) {
            builder.instanceID(current);
         } else if (equalsOrSuffix(qName, "Caption")) {
            builder.caption(current);
         } else if (equalsOrSuffix(qName, "Description")) {
            builder.description(current);
         } else if (equalsOrSuffix(qName, "AutomaticRecoveryAction")) {
            builder.automaticRecoveryAction(AutomaticRecoveryAction.fromValue(current));
         } else if (equalsOrSuffix(qName, "AutomaticShutdownAction")) {
            builder.automaticShutdownAction(AutomaticShutdownAction.fromValue(current));
         } else if (equalsOrSuffix(qName, "AutomaticStartupAction")) {
            builder.automaticStartupAction(AutomaticStartupAction.fromValue(current));
         } else if (equalsOrSuffix(qName, "AutomaticStartupActionDelay")) {
            // TODO parse the format for intervals: ddddddddhhmmss.mmmmmm:000
            builder.automaticStartupActionDelay(null);
         } else if (equalsOrSuffix(qName, "AutomaticStartupActionSequenceNumber")) {
            builder.automaticStartupActionSequenceNumber(Integer.valueOf(current));
         } else if (equalsOrSuffix(qName, "ConfigurationDataRoot")) {
            builder.configurationDataRoot(URI.create(current));
         } else if (equalsOrSuffix(qName, "ConfigurationFile")) {
            builder.configurationFile(URI.create(current));
         } else if (equalsOrSuffix(qName, "ConfigurationID")) {
            builder.configurationID(current);
         } else if (equalsOrSuffix(qName, "CreationTime")) {
            // TODO parse the format for timestamps: yyyymmddhhmmss.mmmmmmsutc
            builder.creationTime(null);
         } else if (equalsOrSuffix(qName, "LogDataRoot")) {
            builder.logDataRoot(URI.create(current));
         } else if (equalsOrSuffix(qName, "RecoveryFile")) {
            builder.recoveryFile(URI.create(current));
         } else if (equalsOrSuffix(qName, "RecoveryFile")) {
            builder.recoveryFile(URI.create(current));
         } else if (equalsOrSuffix(qName, "SuspendDataRoot")) {
            builder.suspendDataRoot(URI.create(current));
         } else if (equalsOrSuffix(qName, "SwapFileDataRoot")) {
            builder.swapFileDataRoot(URI.create(current));
         } else if (equalsOrSuffix(qName, "VirtualSystemIdentifier")) {
            builder.virtualSystemIdentifier(current);
         } else if (equalsOrSuffix(qName, "VirtualSystemType")) {
            builder.virtualSystemTypes(Splitter.on(',').trimResults().omitEmptyStrings().split(current));
         } else if (equalsOrSuffix(qName, "Notes")) {
            builder.notes(current);
         }
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
