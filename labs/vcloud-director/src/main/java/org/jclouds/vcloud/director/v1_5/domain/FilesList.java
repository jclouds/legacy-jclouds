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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * 
 *                 Represents a list of files to be transferred (uploaded
 *                 or downloaded).
 *             
 * 
 * <p>Java class for FilesList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FilesList">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="File" type="{http://www.vmware.com/vcloud/v1.5}FileType" maxOccurs="unbounded"/>
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
@XmlType(name = "FilesList", propOrder = {
    "file"
})
public class FilesList {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromFilesList(this);
   }

   public static class Builder {
      
      private List<File> files;

      /**
       * @see FilesList#getFile()
       */
      public Builder file(List<File> file) {
         this.files = file;
         return this;
      }


      public FilesList build() {
         FilesList filesList = new FilesList(files);
         return filesList;
      }


      public Builder fromFilesList(FilesList in) {
         return file(in.getFile());
      }
   }

   private FilesList() {
      // For JAXB and builder use
   }

   private FilesList(List<File> files) {
      this.files = files;
   }


    @XmlElement(name = "File", required = true)
    protected List<File> files;

    /**
     * Gets the value of the file property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the file property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FileType }
     * 
     * 
     */
    public List<File> getFile() {
        if (files == null) {
            files = new ArrayList<File>();
        }
        return this.files;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      FilesList that = FilesList.class.cast(o);
      return equal(files, that.files);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(files);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("file", files).toString();
   }

}
