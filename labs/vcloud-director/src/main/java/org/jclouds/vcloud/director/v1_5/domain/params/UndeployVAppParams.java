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
package org.jclouds.vcloud.director.v1_5.domain.params;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

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
   
   public static final String MEDIA_TYPE = VCloudDirectorMediaType.UNDEPLOY_VAPP_PARAMS;
   
   @XmlType
   @XmlEnum(String.class)
   public static enum PowerAction {
      @XmlEnumValue("powerOff") POWER_OFF("powerOff"),
      @XmlEnumValue("suspend") SUSPEND("suspend"),
      @XmlEnumValue("shutdown") SHUTDOWN("shutdown"),
      @XmlEnumValue("force") FORCE("force"),
      @XmlEnumValue("") UNRECOGNIZED("unrecognized");
      
      public static final List<PowerAction> ALL = ImmutableList.of( POWER_OFF, SUSPEND, SHUTDOWN, FORCE );

      protected final String stringValue;

      PowerAction(String stringValue) {
         this.stringValue = stringValue;
      }

      public String value() {
         return stringValue;
      }

      protected static final Map<String, PowerAction> POWER_ACTION_BY_ID = Maps.uniqueIndex(
            ImmutableSet.copyOf(PowerAction.values()), new Function<PowerAction, String>() {
               @Override
               public String apply(PowerAction input) {
                  return input.stringValue;
               }
            });

      public static PowerAction fromValue(String value) {
         PowerAction action = POWER_ACTION_BY_ID.get(checkNotNull(value, "stringValue"));
         return action == null ? UNRECOGNIZED : action;
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromUndeployVAppParams(this);
   }

   public static class Builder {

      private PowerAction undeployPowerAction;

      /**
       * @see UndeployVAppParams#getUndeployPowerAction()
       */
      public Builder undeployPowerAction(PowerAction undeployPowerAction) {
         this.undeployPowerAction = undeployPowerAction;
         return this;
      }

      /**
       * @see UndeployVAppParams#getUndeployPowerAction()
       */
      public Builder undeployPowerAction(String undeployPowerAction) {
         this.undeployPowerAction = PowerAction.valueOf(undeployPowerAction);
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
   protected PowerAction undeployPowerAction;

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
   public PowerAction getUndeployPowerAction() {
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
