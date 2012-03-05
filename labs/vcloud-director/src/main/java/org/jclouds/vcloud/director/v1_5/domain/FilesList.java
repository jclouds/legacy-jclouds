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

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;


/**
 * Represents a list of files to be transferred (uploaded
 * or downloaded).
 * <p/>
 * <p/>
 * <p>Java class for FilesList complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
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
 */
@XmlType(name = "FilesList", propOrder = {
      "files"
})
public class FilesList {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromFilesList(this);
   }

   public static class Builder {

      private List<File> files = Lists.newLinkedList();
      
      /**
       * @see FilesList#getFiles()
       */
      public Builder files(List<File> files) {
         this.files = Lists.newLinkedList(checkNotNull(files, "files"));
         return this;
      }

      /**
       * @see FilesList#getFiles()
       */
      public Builder file(File file) {
         files.add(checkNotNull(file, "file"));
         return this;
      }
      
      public FilesList build() {
         FilesList filesList = new FilesList(files);
         return filesList;
      }

      public Builder fromFilesList(FilesList in) {
         return files(in.getFiles());
      }
   }

   private FilesList() {
      // For JAXB and builder use
   }

   private FilesList(List<File> files) {
      this.files = ImmutableList.copyOf(files);
   }


   @XmlElement(name = "File", required = true)
   protected List<File> files = Lists.newLinkedList();

   /**
    * Gets the value of the file property.
    */
   public List<File> getFiles() {
      return Collections.unmodifiableList(this.files);
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
