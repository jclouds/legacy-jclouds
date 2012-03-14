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
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


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
public class FilesList extends ForwardingSet<File> {
   
   // TODO Investigate using the same wrapper (e.g. see Tasks); can we eliminate this class?
   
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromFilesList(this);
   }

   public static class Builder {

      private Set<File> files = Sets.newLinkedHashSet();
      
      /**
       * @see FilesList#getFiles()
       */
      public Builder files(Iterable<File> files) {
         this.files = Sets.newLinkedHashSet(checkNotNull(files, "files"));
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

   @XmlElement(name = "File", required = true)
   private Set<File> files = Sets.newLinkedHashSet();

   private FilesList() {
      // for JAXB
   }

   private FilesList(Iterable<File> files) {
      this.files = ImmutableSet.copyOf(files);
   }

   /**
    * Gets the value of the file property.
    */
   public Set<File> getFiles() {
      return Collections.unmodifiableSet(this.files);
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

   @Override
   protected Set<File> delegate() {
      return getFiles();
   }
}
