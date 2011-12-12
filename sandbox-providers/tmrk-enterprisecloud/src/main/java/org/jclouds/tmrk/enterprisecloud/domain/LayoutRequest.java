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
package org.jclouds.tmrk.enterprisecloud.domain;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.internal.AnonymousResource;

import javax.xml.bind.annotation.XmlElement;

/**
 * LayoutRequest has three variations. Only one of the three variations is permitted in a call:
 * 
 * 1.	Use an existing group.
 * Group is required and the href attribute on the element is required to identify 
 * the group to use.
 * 
 * 2.	Use a newly created group in an existing row.
 * Row is required and the href attribute on the element is required to identify 
 * the row in which the new group will be created.
 * NewGroup is required to provide the required name, 
 * which may not exceed fifty characters, 
 * and to create the group to use.
 * 
 * 3.	Move to a newly created group in a newly created row.
 * NewRow is required to provide the required name, 
 * which may not exceed fifty characters, 
 * and to create the row in which the new group will be created.
 * NewGroup is required to provide the required name, 
 * which may not exceed fifty characters,
 * and to create the group to use.
 * 
 * <xs:complexType name="LayoutRequest">
 * @author Jason King
 * 
 */
public class LayoutRequest {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromLayoutRequest(this);
   }

   public static class Builder {
      private AnonymousResource row;
      private AnonymousResource group;
      private String newRow;
      private String newGroup;

     /**
      * @see LayoutRequest#getRow
      */
      public Builder row(AnonymousResource row) {
         this.row = row;
         return this;
      }

      /**
       * @see LayoutRequest#getGroup
       */
      public Builder group(AnonymousResource group) {
         this.group = group;
         return this;
      }

      /**
       * @see LayoutRequest#getNewRow
       */
      public Builder newRow(String newRow) {
         this.newRow = newRow;
         return this;
      }

      /**
       * @see LayoutRequest#getNewGroup
       */
      public Builder newGroup(String newGroup) {
         this.newGroup = newGroup;
         return this;
      }

      public LayoutRequest build() {
         return new LayoutRequest(row, group, newRow, newGroup);
      }

      public Builder fromLayoutRequest(LayoutRequest in) {
         return row(in.getRow()).group(in.getGroup()).newRow(in.getNewRow()).newGroup(in.getNewGroup());
      }
   }

   @XmlElement(name = "Row", required = false)
   private AnonymousResource row;

   @XmlElement(name = "Group", required = false)
   private AnonymousResource group;

   @XmlElement(name = "NewRow", required = false)
   private String newRow;

   @XmlElement(name = "NewGroup", required = false)
   private String newGroup;


   private LayoutRequest(@Nullable AnonymousResource row, @Nullable AnonymousResource group, @Nullable String newRow, @Nullable String newGroup) {
      this.row = row;
      this.group = group;
      this.newRow = newRow;
      this.newGroup = newGroup;
    }

   private LayoutRequest() {
       //For JAXB
   }

   public AnonymousResource getRow() {
      return row;
   }

   public AnonymousResource getGroup() {
      return group;
   }

   public String getNewRow() {
      return newRow;
   }

   public String getNewGroup() {
      return newGroup;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      LayoutRequest that = (LayoutRequest) o;

      if (group != null ? !group.equals(that.group) : that.group != null)
         return false;
      if (newGroup != null ? !newGroup.equals(that.newGroup) : that.newGroup != null)
         return false;
      if (newRow != null ? !newRow.equals(that.newRow) : that.newRow != null)
         return false;
      if (row != null ? !row.equals(that.row) : that.row != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = row != null ? row.hashCode() : 0;
      result = 31 * result + (group != null ? group.hashCode() : 0);
      result = 31 * result + (newRow != null ? newRow.hashCode() : 0);
      result = 31 * result + (newGroup != null ? newGroup.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "row="+row+", group="+group+", newRow="+newRow+", newGroup="+newGroup;
   }
}