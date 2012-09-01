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
package org.jclouds.azure.management.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * The Add OS Image operation adds an OS image that is currently stored in a storage account in your
 * subscription to the image repository.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157191" >api</a>
 * @author Adrian Cole
 */
public class OSImageParams {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromHostedService(this);
   }

   public static class Builder {

      protected String label;
      protected URI mediaLink;
      protected OSType os;
      protected String name;

      /**
       * @see OSImageParams#getLabel()
       */
      public Builder label(String label) {
         this.label = label;
         return this;
      }

      /**
       * @see OSImageParams#getMediaLink()
       */
      public Builder mediaLink(URI mediaLink) {
         this.mediaLink = mediaLink;
         return this;
      }

      /**
       * @see OSImageParams#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see OSImageParams#getOS()
       */
      public Builder os(OSType os) {
         this.os = os;
         return this;
      }

      public OSImageParams build() {
         return new OSImageParams(label, mediaLink, name, os);
      }

      public Builder fromHostedService(OSImageParams in) {
         return this.label(in.getLabel()).mediaLink(in.getMediaLink()).name(in.getName()).os(in.getOS());
      }
   }

   protected final String label;
   protected final URI mediaLink;
   protected final String name;
   protected final OSType os;

   protected OSImageParams(String label, URI mediaLink, String name, OSType os) {
      this.label = checkNotNull(label, "label");
      this.name = checkNotNull(name, "name for %s", label);
      this.mediaLink = checkNotNull(mediaLink, "mediaLink for %s", label);
      this.os = checkNotNull(os, "os for %s", label);
   }

   /**
    * The operating system type of the OS image.
    */
   public OSType getOS() {
      return os;
   }

   /**
    * The name of the hosted service. This name is the DNS prefix name and can be used to access the
    * hosted service.
    * 
    * For example, if the service name is MyService you could access the access the service by
    * calling: http://MyService.cloudapp.net
    */
   public String getName() {
      return name;
   }

   /**
    * The location of the blob in the blob store in which the media for the image is located. The
    * blob location belongs to a storage account in the subscription specified by the
    * <subscription-id> value in the operation call.
    * 
    * Example:
    * 
    * http://example.blob.core.windows.net/disks/myimage.vhd
    */
   public URI getMediaLink() {
      return mediaLink;
   }

   /**
    * The description of the image.
    */
   public String getLabel() {
      return label;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      OSImageParams other = (OSImageParams) obj;
      return Objects.equal(this.name, other.name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).add("label", label).add("mediaLink", mediaLink).add("name", name)
               .add("os", os);
   }

}
