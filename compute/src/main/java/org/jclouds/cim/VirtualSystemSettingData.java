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
package org.jclouds.cim;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * CIM_VirtualSystemSettingData defines the virtual aspects of a virtual system through a set of
 * virtualization specific properties. CIM_VirtualSystemSettingData is also used as the top level
 * class of virtual system configurations. Virtual system configurations model configuration
 * information about virtual systems and their components. A virtual system configuration consists
 * of one top-level instance of class CIM_VirtualSystemSettingData that aggregates a number of
 * instances of class CIM_ResourceAllocationSettingData, using association CIM_ConcreteComponent.
 * Virtual system configurations may for example be used to reflect configurations of - virtual
 * systems that are defined at a virtualization platform, - virtual systems that are currently
 * active, - input requests to create new virtual systems, - input requests to modify existing
 * virtual systems, or - snapshots of virtual systems.
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://dmtf.org/sites/default/files/cim/cim_schema_v2280/cim_schema_2.28.0Final-Doc.zip"
 *      />
 * 
 */
public class VirtualSystemSettingData extends ManagedElement {

   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return builder().fromVirtualSystemSettingData(this);
   }

   public static class Builder extends ManagedElement.Builder {
      private AutomaticRecoveryAction automaticRecoveryAction;
      private AutomaticShutdownAction automaticShutdownAction;
      private AutomaticStartupAction automaticStartupAction;
      private Long automaticStartupActionDelay;
      private Integer automaticStartupActionSequenceNumber;
      private URI configurationDataRoot;
      private URI configurationFile;
      private String configurationID;
      private Date creationTime;
      private URI logDataRoot;
      private URI recoveryFile;
      private URI snapshotDataRoot;
      private URI suspendDataRoot;
      private URI swapFileDataRoot;
      private String virtualSystemIdentifier;
      private Set<String> virtualSystemTypes = Sets.newLinkedHashSet();
      private String notes;

      public Builder automaticRecoveryAction(AutomaticRecoveryAction automaticRecoveryAction) {
         this.automaticRecoveryAction = automaticRecoveryAction;
         return this;
      }

      public Builder automaticShutdownAction(AutomaticShutdownAction automaticShutdownAction) {
         this.automaticShutdownAction = automaticShutdownAction;
         return this;
      }

      public Builder automaticStartupAction(AutomaticStartupAction automaticStartupAction) {
         this.automaticStartupAction = automaticStartupAction;
         return this;
      }

      public Builder automaticStartupActionDelay(Long automaticStartupActionDelay) {
         this.automaticStartupActionDelay = automaticStartupActionDelay;
         return this;
      }

      public Builder automaticStartupActionSequenceNumber(Integer automaticStartupActionSequenceNumber) {
         this.automaticStartupActionSequenceNumber = automaticStartupActionSequenceNumber;
         return this;
      }

      public Builder configurationDataRoot(URI configurationDataRoot) {
         this.configurationDataRoot = configurationDataRoot;
         return this;
      }

      public Builder configurationFile(URI configurationFile) {
         this.configurationFile = configurationFile;
         return this;
      }

      public Builder configurationID(String configurationID) {
         this.configurationID = configurationID;
         return this;
      }

      public Builder creationTime(Date creationTime) {
         this.creationTime = creationTime;
         return this;
      }

      public Builder logDataRoot(URI logDataRoot) {
         this.logDataRoot = logDataRoot;
         return this;
      }

      public Builder recoveryFile(URI recoveryFile) {
         this.recoveryFile = recoveryFile;
         return this;
      }

      public Builder snapshotDataRoot(URI snapshotDataRoot) {
         this.snapshotDataRoot = snapshotDataRoot;
         return this;
      }

      public Builder suspendDataRoot(URI suspendDataRoot) {
         this.suspendDataRoot = suspendDataRoot;
         return this;
      }

      public Builder swapFileDataRoot(URI swapFileDataRoot) {
         this.swapFileDataRoot = swapFileDataRoot;
         return this;
      }

      public Builder virtualSystemIdentifier(String virtualSystemIdentifier) {
         this.virtualSystemIdentifier = virtualSystemIdentifier;
         return this;
      }

      public Builder virtualSystemTypes(Iterable<String> virtualSystemTypes) {
         this.virtualSystemTypes = ImmutableSet.copyOf(checkNotNull(virtualSystemTypes, "virtualSystemTypes"));
         return this;
      }

      public Builder virtualSystemType(String virtualSystemType) {
         this.virtualSystemTypes.add(checkNotNull(virtualSystemType, "virtualSystemType"));
         return this;
      }

      public Builder notes(String notes) {
         this.notes = notes;
         return this;
      }

      public VirtualSystemSettingData build() {
         return new VirtualSystemSettingData(elementName, instanceID, caption, description, automaticRecoveryAction,
                  automaticShutdownAction, automaticStartupAction, automaticStartupActionDelay,
                  automaticStartupActionSequenceNumber, configurationDataRoot, configurationFile, configurationID,
                  creationTime, logDataRoot, recoveryFile, snapshotDataRoot, suspendDataRoot, swapFileDataRoot,
                  virtualSystemIdentifier, virtualSystemTypes, notes);
      }

      public Builder fromVirtualSystemSettingData(VirtualSystemSettingData in) {
         return fromManagedElement(in).automaticRecoveryAction(in.getAutomaticRecoveryAction())
                  .automaticShutdownAction(in.getAutomaticShutdownAction()).automaticStartupAction(
                           in.getAutomaticStartupAction()).automaticStartupActionDelay(
                           in.getAutomaticStartupActionDelay()).automaticStartupActionSequenceNumber(
                           in.getAutomaticStartupActionSequenceNumber()).configurationDataRoot(
                           in.getConfigurationDataRoot()).configurationFile(in.getConfigurationFile()).configurationID(
                           in.getConfigurationID()).creationTime(in.getCreationTime()).logDataRoot(in.getLogDataRoot())
                  .recoveryFile(in.getRecoveryFile()).snapshotDataRoot(in.getSnapshotDataRoot()).suspendDataRoot(
                           in.getSuspendDataRoot()).swapFileDataRoot(in.getSwapFileDataRoot()).virtualSystemIdentifier(
                           in.getVirtualSystemIdentifier()).virtualSystemTypes(in.getVirtualSystemTypes()).notes(
                           in.getNotes());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromManagedElement(ManagedElement in) {
         return Builder.class.cast(super.fromManagedElement(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder caption(String caption) {
         return Builder.class.cast(super.caption(caption));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder description(String description) {
         return Builder.class.cast(super.description(description));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder elementName(String elementName) {
         return Builder.class.cast(super.elementName(elementName));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder instanceID(String instanceID) {
         return Builder.class.cast(super.instanceID(instanceID));
      }

   }

   /**
    * Action to take for the virtual system when the software executed by the virtual system fails.
    * Failures in this case means a failure that is detectable by the host platform, such as a
    * non-interruptible wait state condition.
    */
   public static enum AutomaticRecoveryAction {

      NONE(2),

      RESTART(3),

      REVERT_TO_SNAPSHOT(4);

      protected final int code;

      AutomaticRecoveryAction(int code) {
         this.code = code;
      }

      public String value() {
         return code + "";
      }

      protected static final Map<Integer, AutomaticRecoveryAction> AUTOMATIC_RECOVERY_ACTION_BY_ID = Maps.uniqueIndex(
               ImmutableSet.copyOf(AutomaticRecoveryAction.values()), new Function<AutomaticRecoveryAction, Integer>() {

                  @Override
                  public Integer apply(AutomaticRecoveryAction input) {
                     return input.code;
                  }

               });

      public static AutomaticRecoveryAction fromValue(String automaticRecoveryAction) {
         return AUTOMATIC_RECOVERY_ACTION_BY_ID.get(Integer.valueOf(checkNotNull(automaticRecoveryAction,
                  "automaticRecoveryAction")));
      }
   }

   /**
    * Action to take for the virtual system when the host is shut down.
    */
   public static enum AutomaticShutdownAction {

      TURN_OFF(2),

      SAVE_STATE(3),

      SHUTDOWN(4);

      protected final int code;

      AutomaticShutdownAction(int code) {
         this.code = code;
      }

      public String value() {
         return code + "";
      }

      protected static final Map<Integer, AutomaticShutdownAction> AUTOMATIC_SHUTDOWN_ACTION_BY_ID = Maps.uniqueIndex(
               ImmutableSet.copyOf(AutomaticShutdownAction.values()), new Function<AutomaticShutdownAction, Integer>() {

                  @Override
                  public Integer apply(AutomaticShutdownAction input) {
                     return input.code;
                  }

               });

      public static AutomaticShutdownAction fromValue(String automaticShutdownAction) {
         return AUTOMATIC_SHUTDOWN_ACTION_BY_ID.get(Integer.valueOf(checkNotNull(automaticShutdownAction,
                  "automaticShutdownAction")));
      }
   }

   /**
    * Action to take for the virtual system when the host is started.
    */
   public static enum AutomaticStartupAction {

      NONE(2),

      RESTART_IF_PREVIOUSLY_ACTIVE(3),

      ALWAYS_STARTUP(4);

      protected final int code;

      AutomaticStartupAction(int code) {
         this.code = code;
      }

      public String value() {
         return code + "";
      }

      protected static final Map<Integer, AutomaticStartupAction> AUTOMATIC_STARTUP_ACTION_BY_ID = Maps.uniqueIndex(
               ImmutableSet.copyOf(AutomaticStartupAction.values()), new Function<AutomaticStartupAction, Integer>() {

                  @Override
                  public Integer apply(AutomaticStartupAction input) {
                     return input.code;
                  }

               });

      public static AutomaticStartupAction fromValue(String automaticStartupAction) {
         return AUTOMATIC_STARTUP_ACTION_BY_ID.get(Integer.valueOf(checkNotNull(automaticStartupAction,
                  "automaticStartupAction")));
      }
   }

   private final AutomaticRecoveryAction automaticRecoveryAction;
   private final AutomaticShutdownAction automaticShutdownAction;
   private final AutomaticStartupAction automaticStartupAction;
   private final Long automaticStartupActionDelay;
   private final Integer automaticStartupActionSequenceNumber;
   private final URI configurationDataRoot;
   private final URI configurationFile;
   private final String configurationID;
   private final Date creationTime;
   private final URI logDataRoot;
   private final URI recoveryFile;
   private final URI snapshotDataRoot;
   private final URI suspendDataRoot;
   private final URI swapFileDataRoot;
   private final String virtualSystemIdentifier;
   private final Set<String> virtualSystemTypes;
   private final String notes;

   public VirtualSystemSettingData(String elementName, String instanceID, String caption, String description,
            AutomaticRecoveryAction automaticRecoveryAction, AutomaticShutdownAction automaticShutdownAction,
            AutomaticStartupAction automaticStartupAction, Long automaticStartupActionDelay,
            Integer automaticStartupActionSequenceNumber, URI configurationDataRoot, URI configurationFile,
            String configurationID, Date creationTime, URI logDataRoot, URI recoveryFile, URI snapshotDataRoot,
            URI suspendDataRoot, URI swapFileDataRoot, String virtualSystemIdentifier,
            Iterable<String> virtualSystemTypes, String notes) {
      super(elementName, instanceID, caption, description);
      this.automaticRecoveryAction = automaticRecoveryAction;
      this.automaticShutdownAction = automaticShutdownAction;
      this.automaticStartupAction = automaticStartupAction;
      this.automaticStartupActionDelay = automaticStartupActionDelay;
      this.automaticStartupActionSequenceNumber = automaticStartupActionSequenceNumber;
      this.configurationDataRoot = configurationDataRoot;
      this.configurationFile = configurationFile;
      this.configurationID = configurationID;
      this.creationTime = creationTime;
      this.logDataRoot = logDataRoot;
      this.recoveryFile = recoveryFile;
      this.snapshotDataRoot = snapshotDataRoot;
      this.suspendDataRoot = suspendDataRoot;
      this.swapFileDataRoot = swapFileDataRoot;
      this.virtualSystemIdentifier = virtualSystemIdentifier;
      this.virtualSystemTypes = ImmutableSet.copyOf(checkNotNull(virtualSystemTypes, "virtualSystemTypes"));
      this.notes = notes;
   }

   /**
    * Action to take for the virtual system when the software executed by the virtual system fails.
    * Failures in this case means a failure that is detectable by the host platform, such as a
    * non-interruptible wait state condition.
    */
   public AutomaticRecoveryAction getAutomaticRecoveryAction() {
      return automaticRecoveryAction;
   }

   /**
    * Action to take for the virtual system when the host is shut down.
    */
   public AutomaticShutdownAction getAutomaticShutdownAction() {
      return automaticShutdownAction;
   }

   /**
    * Action to take for the virtual system when the host is started.
    */
   public AutomaticStartupAction getAutomaticStartupAction() {
      return automaticStartupAction;
   }

   /**
    * Delay applicable to startup action. The value shall be in the interval variant of the datetime
    * datatype.
    */
   public Long getAutomaticStartupActionDelay() {
      return automaticStartupActionDelay;
   }

   /**
    * Number indicating the relative sequence of virtual system activation when the host system is
    * started. A lower number indicates earlier activation. If one or more configurations show the
    * same value, the sequence is implementation dependent. A value of 0 indicates that the sequence
    * is implementation dependent.
    */
   public Integer getAutomaticStartupActionSequenceNumber() {
      return automaticStartupActionSequenceNumber;
   }

   /**
    * Filepath of a directory where information about the virtual system configuration is
    * stored.Format shall be URI based on RFC 2079.
    */
   public URI getConfigurationDataRoot() {
      return configurationDataRoot;
   }

   /**
    * Filepath of a file where information about the virtual system configuration is stored. A
    * relative path appends to the value of the ConfigurationDataRoot property.Format shall be URI
    * based on RFC 2079.
    */
   public URI getConfigurationFile() {
      return configurationFile;
   }

   /**
    * Unique id of the virtual system configuration. Note that the ConfigurationID is different from
    * the InstanceID as it is assigned by the implementation to a virtual system or a virtual system
    * configuration. It is not a key, and the same value may occur within more than one instance.
    */
   public String getConfigurationID() {
      return configurationID;
   }

   /**
    * Time when the virtual system configuration was created.
    */
   public Date getCreationTime() {
      return creationTime;
   }

   /**
    * Filepath of a directory where log information about the virtual system is stored. A relative
    * path appends to the value of the ConfigurationDataRoot property.Format shall be URI based on
    * RFC 2079.
    */
   public URI getLogDataRoot() {
      return logDataRoot;
   }

   /**
    * Filepath of a file where recovery related information of the virtual system is stored.Format
    * shall be URI based on RFC 2079.
    */
   public URI getRecoveryFile() {
      return recoveryFile;
   }

   /**
    * Filepath of a directory where information about virtual system snapshots is stored. A relative
    * path appends to the value of the ConfigurationDataRoot property.Format shall be URI based on
    * RFC 2079.
    */
   public URI getSnapshotDataRoot() {
      return snapshotDataRoot;
   }

   /**
    * Filepath of a directory where suspend related information about the virtual system is stored.
    * A relative path appends to the value of the ConfigurationDataRoot property.Format shall be URI
    * based on RFC 2079.
    */
   public URI getSuspendDataRoot() {
      return suspendDataRoot;
   }

   /**
    * Filepath of a directory where swapfiles of the virtual system are stored. A relative path
    * appends to the value of the ConfigurationDataRoot property.Format shall be URI based on RFC
    * 2079.
    */
   public URI getSwapFileDataRoot() {
      return swapFileDataRoot;
   }

   /**
    * VirtualSystemIdentifier shall reflect a unique name for the system as it is used within the
    * virtualization platform. Note that the VirtualSystemIdentifier is not the hostname assigned to
    * the operating system instance running within the virtual system, nor is it an IP address or
    * MAC address assigned to any of its network ports. On create requests VirtualSystemIdentifier
    * may contain implementation specific rules (like simple patterns or regular expression) that
    * may be interpreted by the implementation when assigning a VirtualSystemIdentifier.
    */
   public String getVirtualSystemIdentifier() {
      return virtualSystemIdentifier;
   }

   /**
    * VirtualSystemType shall reflect a particular type of virtual system.
    */
   public Set<String> getVirtualSystemTypes() {
      return virtualSystemTypes;
   }

   /**
    * End-user supplied notes that are related to the virtual system.
    */
   public String getNotes() {
      return notes;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((virtualSystemIdentifier == null) ? 0 : virtualSystemIdentifier.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      VirtualSystemSettingData other = (VirtualSystemSettingData) obj;
      if (virtualSystemIdentifier == null) {
         if (other.virtualSystemIdentifier != null)
            return false;
      } else if (!virtualSystemIdentifier.equals(other.virtualSystemIdentifier))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String
               .format(
                        "[elementName=%s, instanceID=%s, caption=%s, description=%s, automaticRecoveryAction=%s, automaticShutdownAction=%s, automaticStartupAction=%s, automaticStartupActionDelay=%s, automaticStartupActionSequenceNumber=%s, configurationDataRoot=%s, configurationFile=%s, configurationID=%s, creationTime=%s, logDataRoot=%s, notes=%s, recoveryFile=%s, snapshotDataRoot=%s, suspendDataRoot=%s, swapFileDataRoot=%s, virtualSystemIdentifier=%s, virtualSystemTypes=%s]",
                        elementName, instanceID, caption, description, automaticRecoveryAction,
                        automaticShutdownAction, automaticStartupAction, automaticStartupActionDelay,
                        automaticStartupActionSequenceNumber, configurationDataRoot, configurationFile,
                        configurationID, creationTime, logDataRoot, notes, recoveryFile, snapshotDataRoot,
                        suspendDataRoot, swapFileDataRoot, virtualSystemIdentifier, virtualSystemTypes);
   }

}
