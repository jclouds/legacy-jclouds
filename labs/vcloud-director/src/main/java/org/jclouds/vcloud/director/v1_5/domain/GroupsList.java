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

package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;


/**
 * 
 *                 Container for ReferenceType elements that reference groups.
 *             
 * 
 * <p>Java class for GroupsList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GroupsList">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="GroupReference" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "GroupsList")
@XmlType(propOrder = {
    "groups"
})
public class GroupsList {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromGroupsList(this);
   }

   public static class Builder {
      private List<Reference> groups;

      /**
       * @see GroupsList#getGroupReference()
       */
      public Builder groups(List<Reference> groups) {
         this.groups = ImmutableList.copyOf(groups);
         return this;
      }
      
      /**
       * @see GroupsList#getGroupReference()
       */
      public Builder group(Reference group) {
         groups.add(checkNotNull(group, "group"));
         return this;
      }
      
      public GroupsList build() {
         return new GroupsList(groups);
      }

      public Builder fromGroupsList(GroupsList in) {
         return groups(in.getGroups());
      }
   }

   private GroupsList() {
      // For JAXB
   }
   
   private GroupsList(List<Reference> groups) {
      this.groups = groups;
   }

    @XmlElement(name = "GroupReference")
    protected List<Reference> groups;

    /**
     * Gets the value of the groupReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferenceType }
     * 
     * 
     */
    public List<Reference> getGroups() {
        if (groups == null) {
           groups = new ArrayList<Reference>();
        }
        return this.groups;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      GroupsList that = GroupsList.class.cast(o);
      return equal(groups, that.groups);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(groups);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("groups", groups).toString();
   }

}
