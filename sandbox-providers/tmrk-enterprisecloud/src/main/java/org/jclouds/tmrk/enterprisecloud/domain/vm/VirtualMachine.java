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
package org.jclouds.tmrk.enterprisecloud.domain.vm;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.*;
import org.jclouds.tmrk.enterprisecloud.domain.hardware.HardwareConfiguration;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseNamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.software.OperatingSystem;
import org.jclouds.tmrk.enterprisecloud.domain.software.ToolsStatus;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <xs:complexType name="VirtualMachine">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "VirtualMachine")
public class VirtualMachine extends BaseNamedResource<VirtualMachine> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromVirtualMachine(this);
   }

   public static class Builder extends BaseNamedResource.Builder<VirtualMachine> {
      //TODO There are some more fields
      private Links links = new Links();
      private Actions actions = new Actions();
      private Tasks tasks = new Tasks();
      private String description;
      private VirtualMachineStatus status;

      private Layout layout;
      private boolean poweredOn;
      private ToolsStatus toolsStatus;
      private VirtualMachineMediaStatus mediaStatus;
      private boolean customizationPending;
      private OperatingSystem operatingSystem;
      private HardwareConfiguration hardwareConfiguration;
      private VirtualMachineIpAddresses ipAddresses = new VirtualMachineIpAddresses();

      /**
       * @see VirtualMachine#getLinks
       */
      public Builder links(Set<Link> links) {
         checkNotNull(links,"links");
         for(Link link:links) this.links.setLink(link);
         return this;
      }

       /**
        * @see VirtualMachine#getActions
        */
       public Builder actions(Set<Action> actions) {
          checkNotNull(actions,"actions");
          this.actions = Actions.builder().actions(actions).build();
          return this;
       }

       /**
        * @see VirtualMachine#getTasks
        */
       public Builder tasks(Set<Task> tasks) {
          checkNotNull(tasks,"tasks");
          for(Task task: tasks) this.tasks.setTask(task);
          return this;
       }


       /**
        * @see VirtualMachine#getDescription
        */
       public Builder description(String description) {
          this.description = description;
          return this;
       }

       /**
        * @see VirtualMachine#getLayout()
        */
       public Builder layout(Layout layout) {
          this.layout = layout;
          return this;
       }

       /**
        * @see VirtualMachine#getToolsStatus()
        */
       public Builder toolStatus(ToolsStatus toolsStatus) {
          this.toolsStatus = toolsStatus;
          return this;
       }

       /**
        * @see VirtualMachine#isCustomizationPending()
        */
       public Builder customizationPending(boolean customizationPending) {
          this.customizationPending = customizationPending;
          return this;
       }

       /**
        * @see VirtualMachine#getStatus()
        */
       public Builder status(VirtualMachineStatus status) {
          this.status = status;
          return this;
       }

       /**
        * @see VirtualMachine#getToolsStatus()
        */
       public Builder toolsStatus(ToolsStatus toolsStatus) {
          this.toolsStatus = toolsStatus;
          return this;
       }

       /**
        * @see VirtualMachine#getMediaStatus()
        */
       public Builder mediaStatus(VirtualMachineMediaStatus mediaStatus) {
          this.mediaStatus = mediaStatus;
          return this;
       }

       /**
        * @see VirtualMachine#isPoweredOn()
        */
       public Builder poweredOn(boolean poweredOn) {
          this.poweredOn = poweredOn;
          return this;
       }

       /**
        * @see VirtualMachine#getOperatingSystem()
        */
       public Builder operatingSystem(OperatingSystem operatingSystem) {
          this.operatingSystem = operatingSystem;
          return this;
       }

       /**
        * @see VirtualMachine#getHardwareConfiguration()
        */
       public Builder hardwareConfiguration(HardwareConfiguration hardwareConfiguration) {
          this.hardwareConfiguration = hardwareConfiguration;
          return this;
       }

       /**
        * @see VirtualMachine#getIpAddresses()
        */
       public Builder ipAddresses(VirtualMachineIpAddresses ipAddresses) {
          this.ipAddresses = checkNotNull(ipAddresses,"ipAddresses");
          return this;
       }

      @Override
      public VirtualMachine build() {
         return new VirtualMachine(href, type, name, tasks, actions, links, description, layout,
               status, poweredOn, toolsStatus, mediaStatus, customizationPending, operatingSystem,
               hardwareConfiguration, ipAddresses);
      }

      public Builder fromVirtualMachine(VirtualMachine in) {
        return fromNamedResource(in)
            .links(in.getLinks())
            .tasks(in.getTasks())
            .actions(in.getActions())
            .description(in.getDescription())
            .layout(in.getLayout())
            .status(in.getStatus())
            .poweredOn(in.isPoweredOn())
            .toolsStatus(in.getToolsStatus())
            .mediaStatus(in.getMediaStatus())
            .customizationPending(in.isCustomizationPending())
            .operatingSystem(in.getOperatingSystem())
            .hardwareConfiguration(in.getHardwareConfiguration())
            .ipAddresses(in.getIpAddresses());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(BaseResource<VirtualMachine> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromNamedResource(BaseNamedResource<VirtualMachine> in) {
         return Builder.class.cast(super.fromNamedResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
          // TODO Other fields?
      }

   }

   @XmlElement(name = "Links", required = true)
   private Links links = new Links();

   @XmlElement(name = "Tasks", required = true)
   private Tasks tasks = new Tasks();

   @XmlElement(name = "Actions", required = true)
   private Actions actions = new Actions();

   @XmlElement(name = "Description", required = true)
   private String description;

   @XmlElement(name = "Layout", required = false)
   private Layout layout;

   @XmlElement(name = "Status", required = false)
   private VirtualMachineStatus status;

   @XmlElement(name = "PoweredOn", required = false)
   private boolean poweredOn;

   @XmlElement(name = "ToolsStatus", required = false)
   private ToolsStatus toolsStatus;

   @XmlElement(name = "MediaStatus", required = false)
   private VirtualMachineMediaStatus mediaStatus;

   @XmlElement(name = "CustomizationPending", required = false)
   private boolean customizationPending;

   @XmlElement(name = "OperatingSystem", required = false)
   private OperatingSystem operatingSystem;

   @XmlElement(name = "HardwareConfiguration", required = false)
   private HardwareConfiguration hardwareConfiguation;

   @XmlElement(name = "IpAddresses", required = false)
   private VirtualMachineIpAddresses ipAddresses = new VirtualMachineIpAddresses();

    public VirtualMachine(URI href, String type, String name, Tasks tasks, Actions actions, Links links, String description, @Nullable Layout layout,
                         VirtualMachineStatus status, boolean poweredOn, @Nullable ToolsStatus toolsStatus, @Nullable VirtualMachineMediaStatus mediaStatus, boolean customizationPending,
                         @Nullable OperatingSystem operatingSystem, @Nullable HardwareConfiguration hardwareConfiguration, @Nullable VirtualMachineIpAddresses ipAddresses) {
      super(href, type, name);
      this.description = checkNotNull(description, "description");
      this.links = checkNotNull(links, "links");
      this.tasks = checkNotNull(tasks, "tasks");
      this.actions = checkNotNull(actions, "actions");
      this.status = checkNotNull(status, "status");

      this.layout = layout;
      this.poweredOn = poweredOn;
      this.toolsStatus = toolsStatus;
      this.mediaStatus = mediaStatus;
      this.customizationPending = customizationPending;
      this.operatingSystem = operatingSystem;
      this.hardwareConfiguation = hardwareConfiguration;
      this.ipAddresses = checkNotNull(ipAddresses, "ipAddresses");
   }

   protected VirtualMachine() {
        //For JAXB
   }


   public Set<Link> getLinks() {
       return Collections.unmodifiableSet(links.getLinks());
   }

    /**
     * refers to tasks regarding the virtual machine.
     * Only the most recent tasks, up to twenty, are returned.
     * Use the href to retrieve the complete list of tasks.
     * @return most recent tasks
     */
   public Set<Task> getTasks() {
       return Collections.unmodifiableSet(tasks.getTasks());
   }

   public Set<Action> getActions() {
       return Collections.unmodifiableSet(actions.getActions());
   }

   public String getDescription() {
       return description;
   }

   public VirtualMachineStatus getStatus() {
       return status;
   }

   public Layout getLayout() {
       return layout;
   }

   public boolean isPoweredOn() {
       return poweredOn;
   }

    /**
     * Is optional, so may return null
     */
   public ToolsStatus getToolsStatus() {
       return toolsStatus;
   }

   /**
    * Is optional, so may return null
    */
   public VirtualMachineMediaStatus getMediaStatus() {
       return mediaStatus;
   }

   /**
    * Is optional, so may return null
    */
   public HardwareConfiguration getHardwareConfiguration() {
       return hardwareConfiguation;
   }

   public boolean isCustomizationPending() {
       return customizationPending;
   }

   /**
    * Is optional, so may return null
    */
   public OperatingSystem getOperatingSystem() {
       return operatingSystem;
   }

   public VirtualMachineIpAddresses getIpAddresses() {
       return ipAddresses;
   }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        VirtualMachine that = (VirtualMachine) o;

        if (customizationPending != that.customizationPending) return false;
        if (poweredOn != that.poweredOn) return false;
        if (!actions.equals(that.actions)) return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (hardwareConfiguation != null ? !hardwareConfiguation.equals(that.hardwareConfiguation) : that.hardwareConfiguation != null)
            return false;
        if (!ipAddresses.equals(that.ipAddresses)) return false;
        if (layout != null ? !layout.equals(that.layout) : that.layout != null)
            return false;
        if (!links.equals(that.links)) return false;
        if (mediaStatus != that.mediaStatus) return false;
        if (operatingSystem != null ? !operatingSystem.equals(that.operatingSystem) : that.operatingSystem != null)
            return false;
        if (status != that.status) return false;
        if (!tasks.equals(that.tasks)) return false;
        if (toolsStatus != that.toolsStatus) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + links.hashCode();
        result = 31 * result + tasks.hashCode();
        result = 31 * result + actions.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (layout != null ? layout.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (poweredOn ? 1 : 0);
        result = 31 * result + (toolsStatus != null ? toolsStatus.hashCode() : 0);
        result = 31 * result + (mediaStatus != null ? mediaStatus.hashCode() : 0);
        result = 31 * result + (customizationPending ? 1 : 0);
        result = 31 * result + (operatingSystem != null ? operatingSystem.hashCode() : 0);
        result = 31 * result + (hardwareConfiguation != null ? hardwareConfiguation.hashCode() : 0);
        result = 31 * result + ipAddresses.hashCode();
        return result;
    }

    @Override
   public String string() {
      return super.string()+", links="+links+", tasks="+tasks+", actions="+actions+", description="+description+", layout="+layout+
                            ", status="+status+", poweredOn="+poweredOn+", toolsStatus="+toolsStatus+", mediaStatus="+mediaStatus+
                            ", customizationPending="+customizationPending+", operatingSystem="+operatingSystem+", hardwareConfiguration="+hardwareConfiguation+
                            ", ipAddresses="+ipAddresses;
   }

   @XmlEnum
   public enum VirtualMachineStatus {
       @XmlEnumValue("NotDeployed")
       NOT_DEPLOYED,
       @XmlEnumValue("Deployed")
       DEPLOYED,
       @XmlEnumValue("Orphaned")
       ORPHANED,
       @XmlEnumValue("TaskInProgress")
       TASK_IN_PROGRESS,
       @XmlEnumValue("CopyInProgress")
       COPY_IN_PROGRESS;

       public String value() {
           return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
       }

       @Override
       public String toString() {
           return value();
       }

   }

   @XmlEnum
   public enum VirtualMachineMediaStatus {
       @XmlEnumValue("Unmounted")
       UNMOUNTED,
       @XmlEnumValue("Mounted")
       MOUNTED;

       public String value() {
           return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
       }

       @Override
       public String toString() {
           return value();
       }

   }

}