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


/**
 * 
 * ManagedElement is an abstract class that provides a common superclass (or top of the inheritance
 * tree) for the non-association classes in the CIM Schema.
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://dmtf.org/sites/default/files/cim/cim_schema_v2280/cim_schema_2.28.0Final-Doc.zip"
 *      />
 * 
 */
public abstract class ManagedElement extends SettingData {
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return builder().fromManagedElement(this);
   }

   public static class Builder extends SettingData.Builder {
      protected String caption;
      protected String description;

      /**
       * @see ManagedSettingData#getCaption
       */
      public Builder caption(String caption) {
         this.caption = caption;
         return this;
      }

      /**
       * @see ManagedSettingData#getDescription
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder fromManagedElement(ManagedElement in) {
         return caption(in.getCaption()).description(in.getDescription()).fromSettingData(in);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSettingData(SettingData in) {
         return Builder.class.cast(super.fromSettingData(in));
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

   protected final String caption;
   protected final String description;

   public ManagedElement(String elementName, String instanceID, String caption, String description) {
      super(elementName, instanceID);
      this.caption = caption;
      this.description = description;
   }

   /**
    * The Caption property is a short textual description (one- line string) of the object.
    */
   public String getCaption() {
      return caption;
   }

   /**
    * The Description property provides a textual description of the object.
    */
   public String getDescription() {
      return description;
   }

   @Override
   public String toString() {
      return String.format("[elementName=%s, instanceID=%s, caption=%s, description=%s]", elementName, instanceID,
               caption, description);
   }

}
