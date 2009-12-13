/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.domain;

import java.util.Date;

/**
 * @author Adrian Cole
 */
public class VirtualSystem {

   private final String automaticRecoveryAction;
   private final String automaticShutdownAction;
   private final String automaticStartupAction;
   private final String automaticStartupActionDelay;
   private final String automaticStartupActionSequenceNumber;
   private final String caption;
   private final String configurationDataRoot;
   private final String configurationFile;
   private final String configurationID;
   private final Date creationTime;
   private final String description;
   private final String elementName;
   private final int instanceID;
   private final String logDataRoot;
   private final String recoveryFile;
   private final String snapshotDataRoot;
   private final String suspendDataRoot;
   private final String swapFileDataRoot;
   private final String virtualSystemIdentifier;
   private final String virtualSystemType;

   public VirtualSystem(String automaticRecoveryAction, String automaticShutdownAction,
            String automaticStartupAction, String automaticStartupActionDelay,
            String automaticStartupActionSequenceNumber, String caption,
            String configurationDataRoot, String configurationFile, String configurationID,
            Date creationTime, String description, String elementName, int instanceID,
            String logDataRoot, String recoveryFile, String snapshotDataRoot,
            String suspendDataRoot, String swapFileDataRoot, String virtualSystemIdentifier,
            String virtualSystemType) {
      this.automaticRecoveryAction = automaticRecoveryAction;
      this.automaticShutdownAction = automaticShutdownAction;
      this.automaticStartupAction = automaticStartupAction;
      this.automaticStartupActionDelay = automaticStartupActionDelay;
      this.automaticStartupActionSequenceNumber = automaticStartupActionSequenceNumber;
      this.caption = caption;
      this.configurationDataRoot = configurationDataRoot;
      this.configurationFile = configurationFile;
      this.configurationID = configurationID;
      this.creationTime = creationTime;
      this.description = description;
      this.elementName = elementName;
      this.instanceID = instanceID;
      this.logDataRoot = logDataRoot;
      this.recoveryFile = recoveryFile;
      this.snapshotDataRoot = snapshotDataRoot;
      this.suspendDataRoot = suspendDataRoot;
      this.swapFileDataRoot = swapFileDataRoot;
      this.virtualSystemIdentifier = virtualSystemIdentifier;
      this.virtualSystemType = virtualSystemType;
   }

   public String getAutomaticRecoveryAction() {
      return automaticRecoveryAction;
   }

   public String getAutomaticShutdownAction() {
      return automaticShutdownAction;
   }

   public String getAutomaticStartupAction() {
      return automaticStartupAction;
   }

   public String getAutomaticStartupActionDelay() {
      return automaticStartupActionDelay;
   }

   public String getAutomaticStartupActionSequenceNumber() {
      return automaticStartupActionSequenceNumber;
   }

   public String getCaption() {
      return caption;
   }

   public String getConfigurationDataRoot() {
      return configurationDataRoot;
   }

   public String getConfigurationFile() {
      return configurationFile;
   }

   public String getConfigurationID() {
      return configurationID;
   }

   public Date getCreationTime() {
      return creationTime;
   }

   public String getDescription() {
      return description;
   }

   public String getElementName() {
      return elementName;
   }

   public int getInstanceID() {
      return instanceID;
   }

   public String getLogDataRoot() {
      return logDataRoot;
   }

   public String getRecoveryFile() {
      return recoveryFile;
   }

   public String getSnapshotDataRoot() {
      return snapshotDataRoot;
   }

   public String getSuspendDataRoot() {
      return suspendDataRoot;
   }

   public String getSwapFileDataRoot() {
      return swapFileDataRoot;
   }

   public String getVirtualSystemIdentifier() {
      return virtualSystemIdentifier;
   }

   public String getVirtualSystemType() {
      return virtualSystemType;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
               + ((automaticRecoveryAction == null) ? 0 : automaticRecoveryAction.hashCode());
      result = prime * result
               + ((automaticShutdownAction == null) ? 0 : automaticShutdownAction.hashCode());
      result = prime * result
               + ((automaticStartupAction == null) ? 0 : automaticStartupAction.hashCode());
      result = prime
               * result
               + ((automaticStartupActionDelay == null) ? 0 : automaticStartupActionDelay
                        .hashCode());
      result = prime
               * result
               + ((automaticStartupActionSequenceNumber == null) ? 0
                        : automaticStartupActionSequenceNumber.hashCode());
      result = prime * result + ((caption == null) ? 0 : caption.hashCode());
      result = prime * result
               + ((configurationDataRoot == null) ? 0 : configurationDataRoot.hashCode());
      result = prime * result + ((configurationFile == null) ? 0 : configurationFile.hashCode());
      result = prime * result + ((configurationID == null) ? 0 : configurationID.hashCode());
      result = prime * result + ((creationTime == null) ? 0 : creationTime.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((elementName == null) ? 0 : elementName.hashCode());
      result = prime * result + instanceID;
      result = prime * result + ((logDataRoot == null) ? 0 : logDataRoot.hashCode());
      result = prime * result + ((recoveryFile == null) ? 0 : recoveryFile.hashCode());
      result = prime * result + ((snapshotDataRoot == null) ? 0 : snapshotDataRoot.hashCode());
      result = prime * result + ((suspendDataRoot == null) ? 0 : suspendDataRoot.hashCode());
      result = prime * result + ((swapFileDataRoot == null) ? 0 : swapFileDataRoot.hashCode());
      result = prime * result
               + ((virtualSystemIdentifier == null) ? 0 : virtualSystemIdentifier.hashCode());
      result = prime * result + ((virtualSystemType == null) ? 0 : virtualSystemType.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      VirtualSystem other = (VirtualSystem) obj;
      if (automaticRecoveryAction == null) {
         if (other.automaticRecoveryAction != null)
            return false;
      } else if (!automaticRecoveryAction.equals(other.automaticRecoveryAction))
         return false;
      if (automaticShutdownAction == null) {
         if (other.automaticShutdownAction != null)
            return false;
      } else if (!automaticShutdownAction.equals(other.automaticShutdownAction))
         return false;
      if (automaticStartupAction == null) {
         if (other.automaticStartupAction != null)
            return false;
      } else if (!automaticStartupAction.equals(other.automaticStartupAction))
         return false;
      if (automaticStartupActionDelay == null) {
         if (other.automaticStartupActionDelay != null)
            return false;
      } else if (!automaticStartupActionDelay.equals(other.automaticStartupActionDelay))
         return false;
      if (automaticStartupActionSequenceNumber == null) {
         if (other.automaticStartupActionSequenceNumber != null)
            return false;
      } else if (!automaticStartupActionSequenceNumber
               .equals(other.automaticStartupActionSequenceNumber))
         return false;
      if (caption == null) {
         if (other.caption != null)
            return false;
      } else if (!caption.equals(other.caption))
         return false;
      if (configurationDataRoot == null) {
         if (other.configurationDataRoot != null)
            return false;
      } else if (!configurationDataRoot.equals(other.configurationDataRoot))
         return false;
      if (configurationFile == null) {
         if (other.configurationFile != null)
            return false;
      } else if (!configurationFile.equals(other.configurationFile))
         return false;
      if (configurationID == null) {
         if (other.configurationID != null)
            return false;
      } else if (!configurationID.equals(other.configurationID))
         return false;
      if (creationTime == null) {
         if (other.creationTime != null)
            return false;
      } else if (!creationTime.equals(other.creationTime))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (elementName == null) {
         if (other.elementName != null)
            return false;
      } else if (!elementName.equals(other.elementName))
         return false;
      if (instanceID != other.instanceID)
         return false;
      if (logDataRoot == null) {
         if (other.logDataRoot != null)
            return false;
      } else if (!logDataRoot.equals(other.logDataRoot))
         return false;
      if (recoveryFile == null) {
         if (other.recoveryFile != null)
            return false;
      } else if (!recoveryFile.equals(other.recoveryFile))
         return false;
      if (snapshotDataRoot == null) {
         if (other.snapshotDataRoot != null)
            return false;
      } else if (!snapshotDataRoot.equals(other.snapshotDataRoot))
         return false;
      if (suspendDataRoot == null) {
         if (other.suspendDataRoot != null)
            return false;
      } else if (!suspendDataRoot.equals(other.suspendDataRoot))
         return false;
      if (swapFileDataRoot == null) {
         if (other.swapFileDataRoot != null)
            return false;
      } else if (!swapFileDataRoot.equals(other.swapFileDataRoot))
         return false;
      if (virtualSystemIdentifier == null) {
         if (other.virtualSystemIdentifier != null)
            return false;
      } else if (!virtualSystemIdentifier.equals(other.virtualSystemIdentifier))
         return false;
      if (virtualSystemType == null) {
         if (other.virtualSystemType != null)
            return false;
      } else if (!virtualSystemType.equals(other.virtualSystemType))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "VirtualSystem [automaticRecoveryAction=" + automaticRecoveryAction
               + ", automaticShutdownAction=" + automaticShutdownAction
               + ", automaticStartupAction=" + automaticStartupAction
               + ", automaticStartupActionDelay=" + automaticStartupActionDelay
               + ", automaticStartupActionSequenceNumber=" + automaticStartupActionSequenceNumber
               + ", caption=" + caption + ", configurationDataRoot=" + configurationDataRoot
               + ", configurationFile=" + configurationFile + ", configurationID="
               + configurationID + ", creationTime=" + creationTime + ", description="
               + description + ", elementName=" + elementName + ", instanceID=" + instanceID
               + ", logDataRoot=" + logDataRoot + ", recoveryFile=" + recoveryFile
               + ", snapshotDataRoot=" + snapshotDataRoot + ", suspendDataRoot=" + suspendDataRoot
               + ", swapFileDataRoot=" + swapFileDataRoot + ", virtualSystemIdentifier="
               + virtualSystemIdentifier + ", virtualSystemType=" + virtualSystemType + "]";
   }

}