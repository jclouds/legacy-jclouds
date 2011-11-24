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
package org.jclouds.tmrk.enterprisecloud.domain.hardware;

import javax.xml.bind.annotation.XmlElement;

/**
 * <xs:complexType name="DiskConfigurationOption">
 * @author Jason King
 */
public class DiskConfigurationOption {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromConfigurationOptionRange(this);
   }

   public static class Builder {

      private int minimum;
      private int maximum;
      private DiskConfigurationOptionRange systemDisk;
      private DiskConfigurationOptionRange dataDisk;

      /**
       * @see DiskConfigurationOption#getMinimum()
       */
      public Builder minimum(int minimum) {
         this.minimum = minimum;
         return this;
      }

      /**
       * @see DiskConfigurationOption#getMaximum()
       */
      public Builder maximum(int maximum) {
         this.maximum = maximum;
         return this;
      }

      /**
       * @see DiskConfigurationOption#getSystemDisk()
       */
      public Builder systemDisk(DiskConfigurationOptionRange systemDisk) {
         this.systemDisk = systemDisk;
         return this;
      }

      /**
       * @see DiskConfigurationOption#getDataDisk()
       */
      public Builder dataDisk(DiskConfigurationOptionRange dataDisk) {
         this.dataDisk = dataDisk;
         return this;
      }

      public DiskConfigurationOption build() {
         return new DiskConfigurationOption(minimum, maximum, systemDisk, dataDisk);
      }

      public Builder fromConfigurationOptionRange(DiskConfigurationOption in) {
        return minimum(in.getMinimum()).maximum(in.getMaximum()).systemDisk(in.getSystemDisk()).dataDisk(in.getDataDisk());
      }
   }

   @XmlElement(name = "Minimum")
   private int minimum;

   @XmlElement(name = "Maximum")
   private int maximum;

   @XmlElement(name = "SystemDisk")
   private DiskConfigurationOptionRange systemDisk;

   @XmlElement(name = "DataDisk")
   private DiskConfigurationOptionRange dataDisk;

   private DiskConfigurationOption(int minimum, int maximum, DiskConfigurationOptionRange systemDisk, DiskConfigurationOptionRange dataDisk) {
      this.minimum = minimum;
      this.maximum = maximum;
      this.systemDisk = systemDisk;
      this.dataDisk = dataDisk;
   }

   private DiskConfigurationOption() {
      //For JAXB
   }

   public int getMinimum() {
      return minimum;
   }

   public int getMaximum() {
      return maximum;
   }

   public DiskConfigurationOptionRange getSystemDisk() {
      return systemDisk;
   }

   public DiskConfigurationOptionRange getDataDisk() {
      return dataDisk;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      DiskConfigurationOption that = (DiskConfigurationOption) o;

      if (maximum != that.maximum) return false;
      if (minimum != that.minimum) return false;
      if (dataDisk != null ? !dataDisk.equals(that.dataDisk) : that.dataDisk != null)
         return false;
      if (systemDisk != null ? !systemDisk.equals(that.systemDisk) : that.systemDisk != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = minimum;
      result = 31 * result + maximum;
      result = 31 * result + (systemDisk != null ? systemDisk.hashCode() : 0);
      result = 31 * result + (dataDisk != null ? dataDisk.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[minimum="+minimum+", maximum="+ maximum +", systemDisk="+ systemDisk +", dataDisk="+dataDisk+"]";
   }

}
