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

/**
 * 
 * The type of resource this allocation setting represents.
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://dmtf.org/sites/default/files/cim/cim_schema_v2280/cim_schema_2.28.0Final-Doc.zip"
 *      />
 * 
 */
public abstract class SettingData implements Comparable<SettingData> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromSettingData(this);
   }

   public static class Builder {
      protected String elementName;
      protected String instanceID;

      /**
       * @see SettingData#getElementName
       */
      public Builder elementName(String elementName) {
         this.elementName = checkNotNull(elementName, "elementName");
         return this;
      }

      /**
       * @see SettingData#getInstanceID
       */
      public Builder instanceID(String instanceID) {
         this.instanceID = checkNotNull(instanceID, "instanceID");
         return this;
      }

      public Builder fromSettingData(SettingData in) {
         return elementName(in.getElementName()).instanceID(in.getInstanceID());
      }
   }

   protected final String elementName;
   protected final String instanceID;

   public SettingData(String elementName, String instanceID) {
      this.elementName = checkNotNull(elementName, "elementName");
      this.instanceID = checkNotNull(instanceID, "instanceID");
   }

   /**
    * The user-friendly name for this instance of SettingData. In addition, the user-friendly name
    * can be used as an index property for a search or query. (Note: The name does not have to be
    * unique within a namespace.)
    */
   public String getElementName() {
      return elementName;
   }

   /**
    * Within the scope of the instantiating Namespace, InstanceID opaquely and uniquely identifies
    * an instance of this class.
    */
   public String getInstanceID() {
      return instanceID;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((elementName == null) ? 0 : elementName.hashCode());
      result = prime * result + ((instanceID == null) ? 0 : instanceID.hashCode());
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
      SettingData other = (SettingData) obj;
      if (elementName == null) {
         if (other.elementName != null)
            return false;
      } else if (!elementName.equals(other.elementName))
         return false;
      if (instanceID == null) {
         if (other.instanceID != null)
            return false;
      } else if (!instanceID.equals(other.instanceID))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[elementName=%s, instanceID=%s]", elementName, instanceID);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int compareTo(SettingData o) {
      if (instanceID == null)
         return -1;
      return (this == o) ? 0 : instanceID.compareTo(o.instanceID);
   }

}
