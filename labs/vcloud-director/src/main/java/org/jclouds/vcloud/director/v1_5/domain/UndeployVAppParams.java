/*
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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.base.Objects;

/**
 * Represents vApp/VM undeployment parameters.
 * 
 * @author grkvlt@apache.org
 * @see <a href="http://www.vmware.com/support/vcd/doc/rest-api-doc-1.5-html/types/UndeployVAppParamsType.html">
 *    vCloud REST API - UndeployVAppParamsType</a>
 * @since 0.9
 */
@XmlRootElement(name = "UndeployVAppParams")
@XmlType(name = "UndeployVAppParamsType")
public class UndeployVAppParams {

   public static final String MEDIA_TYPe = VCloudDirectorMediaType.UNDEPLOY_VAPP_PARAMS;

   public static class PowerAction {
      /** Power off the VMs. This is the default action if this attribute is missing or empty) */
      public static final String POWER_OFF = "powerOff";
      /** Suspend the VMs. */
      public static final String SUSPEND = "suspend";
      /** Shut down the VMs. */
      public static final String SHUTDOWN = "shutdown";
      /** Attempt to power off the VMs. */
      public static final String FORCE = "force";

      public static final List<String> ALL = Arrays.asList(POWER_OFF, SUSPEND, SHUTDOWN, FORCE);
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromUndeployVAppParams(this);
   }

   public static class Builder {

      private String undeployPowerAction;

      /**
       * @see UndeployVAppParams#getUndeployPowerAction()
       */
      public Builder undeployPowerAction(String undeployPowerAction) {
         this.undeployPowerAction = undeployPowerAction;
         return this;
      }

      public UndeployVAppParams build() {
         UndeployVAppParams undeployVAppParams = new UndeployVAppParams();
         undeployVAppParams.undeployPowerAction = undeployPowerAction;
         return undeployVAppParams;
      }

      public Builder fromUndeployVAppParams(UndeployVAppParams in) {
         return undeployPowerAction(in.getUndeployPowerAction());
      }
   }

   private UndeployVAppParams() {
      // For JAXB and builder use
   }

   @XmlElement(name = "UndeployPowerAction")
   protected String undeployPowerAction;

   /**
    * The specified action is applied to all VMs in the vApp.
    *
    * All values other than {@code default} ignore actions, order, and delay specified in the StartupSection. One of:
    * <ul>
    *    <li>{@link PowerAction#POWER_OFF powerOff}
    *    <li>{@link PowerAction#SUSPEND suspend}
    *    <li>{@link PowerAction#SHUTDOWN shutdown}
    *    <li>{@link PowerAction#FORCE force}
    * </ul>
    * Failures in undeploying the VM or associated networks are ignored. All references to the vApp and its VMs are
    * removed from the database), default (Use the actions, order, and delay specified in the StartupSection).
    *
    * @since 1.5
    */
   public String getUndeployPowerAction() {
      return undeployPowerAction;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      UndeployVAppParams that = UndeployVAppParams.class.cast(o);
      return equal(undeployPowerAction, that.undeployPowerAction);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(undeployPowerAction);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("undeployPowerAction", undeployPowerAction).toString();
   }

}
