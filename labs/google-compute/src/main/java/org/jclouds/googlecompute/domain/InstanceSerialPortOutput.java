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

package org.jclouds.googlecompute.domain;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import java.beans.ConstructorProperties;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.googlecompute.domain.Resource.nullCollectionOnNullOrEmpty;

/**
 * The output of an instance's serial port;
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/instances/serialPort"/>
 */
public class InstanceSerialPortOutput {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromInstanceSerialPortOutput(this);
   }

   public static class Builder {

      private String selfLink;
      private String contents;

      /**
       * @see InstanceSerialPortOutput#getSelfLink()
       */
      public Builder selfLink(String selfLink) {
         this.selfLink = checkNotNull(selfLink);
         return this;
      }

      /**
       * @see InstanceSerialPortOutput#getContents()
       */
      public Builder contents(String contents) {
         this.contents = contents;
         return this;
      }

      public InstanceSerialPortOutput build() {
         return new InstanceSerialPortOutput(selfLink, contents);
      }

      public Builder fromInstanceSerialPortOutput(InstanceSerialPortOutput in) {
         return this.selfLink(in.getSelfLink()).contents(in.getContents());
      }
   }

   private final String selfLink;
   private final String contents;

   @ConstructorProperties({
           "selfLink", "contents"
   })
   public InstanceSerialPortOutput(String selfLink, String contents) {
      this.selfLink = selfLink;
      this.contents = contents;
   }

   /**
    * @return unique identifier for the resource; defined by the server (output only).
    */
   public String getSelfLink() {
      return selfLink;
   }

   /**
    * @return the contents of the console output.
    */
   public String getContents() {
      return contents;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(selfLink, contents);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      InstanceSerialPortOutput that = InstanceSerialPortOutput.class.cast(obj);
      return equal(this.selfLink, that.selfLink)
              && equal(this.contents, that.contents);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this).add("selfLink", selfLink).add("contents", contents);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
