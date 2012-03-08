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

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * Represents vApp children.
 *
 * <pre>
 * &lt;complexType name="VAppChildren" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VAppChildren")
public class VAppChildren {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromVAppChildren(this);
   }

   public static class Builder {

      private List<VApp> vApps = Lists.newArrayList();
      private List<Vm> vms = Lists.newArrayList();

      /**
       * @see VAppChildren#getVApps()
       */
      public Builder vApps(List<VApp> vApps) {
         this.vApps = checkNotNull(vApps, "vApps");
         return this;
      }

      /**
       * @see VAppChildren#getVApps()
       */
      public Builder vApp(VApp vApp) {
         this.vApps.add(checkNotNull(vApp, "vApp"));
         return this;
      }

      /**
       * @see VAppChildren#getVms()
       */
      public Builder vms(List<Vm> vms) {
         this.vms = checkNotNull(vms, "vms");
         return this;
      }

      /**
       * @see VAppChildren#getVms()
       */
      public Builder vm(Vm vm) {
         this.vms.add(checkNotNull(vm, "vm"));
         return this;
      }

      public VAppChildren build() {
         VAppChildren vAppChildren = new VAppChildren(vApps, vms);
         return vAppChildren;
      }

      public Builder fromVAppChildren(VAppChildren in) {
         return vApps(in.getVApps()).vms(in.getVms());
      }
   }

   private VAppChildren() {
      // For JAXB and builder use
   }

   private VAppChildren(List<VApp> vApps, List<Vm> vms) {
      this.vApps = vApps;
      this.vms = vms;
   }

   @XmlElement(name = "VApp")
   protected List<VApp> vApps = Lists.newArrayList();
   @XmlElement(name = "Vm")
   protected List<Vm> vms = Lists.newArrayList();

   /**
    * Reserved.
    *
    * Unimplemented.
    */
   public List<VApp> getVApps() {
      return vApps;
   }

   /**
    * Child VMs.
    */
   public List<Vm> getVms() {
      return vms;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VAppChildren that = VAppChildren.class.cast(o);
      return equal(this.vApps, that.vApps) && equal(this.vms, that.vms);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(vApps, vms);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("vApps", vApps).add("vms", vms).toString();
   }
}
