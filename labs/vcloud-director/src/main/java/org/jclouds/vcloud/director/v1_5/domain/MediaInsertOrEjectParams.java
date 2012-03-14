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

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;

/**
 * Represents parameters for inserting/ejecting media to VM.
 *
 * <pre>
 * &lt;complexType name="MediaInsertOrEjectParams" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "MediaInsertOrEjectParams")
@XmlType(name = "MediaInsertOrEjectParamsType")
public class MediaInsertOrEjectParams {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromMediaInsertOrEjectParams(this);
   }

   public static class Builder {

      private Reference media;

      /**
       * @see MediaInsertOrEjectParams#getMedia()
       */
      public Builder media(Reference media) {
         this.media = media;
         return this;
      }

      public MediaInsertOrEjectParams build() {
         MediaInsertOrEjectParams mediaInsertOrEjectParams = new MediaInsertOrEjectParams(media);
         return mediaInsertOrEjectParams;
      }

      public Builder fromMediaInsertOrEjectParams(MediaInsertOrEjectParams in) {
         return media(in.getMedia());
      }
   }

   protected MediaInsertOrEjectParams() {
      // For JAXB and builder use
   }

   public MediaInsertOrEjectParams(Reference media) {
      this.media = media;
   }

   @XmlElement(name = "Media", required = true)
   protected Reference media;

   /**
    * Gets the value of the media property.
    */
   public Reference getMedia() {
      return media;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      MediaInsertOrEjectParams that = MediaInsertOrEjectParams.class.cast(o);
      return equal(media, that.media);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(media);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("media", media).toString();
   }
}
