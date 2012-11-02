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
package org.jclouds.openstack.quantum.v1_0.domain;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Details of a Quantum Port
 * 
 * @author Adam Lowe
 * @see <a href="http://docs.openstack.org/api/openstack-network/1.0/content/Ports.html">api doc</a>
*/
public class PortDetails extends Port {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromPortDetails(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Port.Builder<T>  {
      protected Attachment attachment;
   
      /** 
       * @see PortDetails#getAttachment()
       */
      public T attachment(Attachment attachment) {
         this.attachment = attachment;
         return self();
      }

      public PortDetails build() {
         return new PortDetails(id, state, attachment);
      }
      
      public T fromPortDetails(PortDetails in) {
         return super.fromPort(in)
                  .attachment(in.getAttachment());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Attachment attachment;

   @ConstructorProperties({
      "id", "state", "attachment"
   })
   protected PortDetails(String id, Port.State state, @Nullable Attachment attachment) {
      super(id, state);
      this.attachment = attachment;
   }

   @Nullable
   public Attachment getAttachment() {
      return this.attachment;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(attachment);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      PortDetails that = PortDetails.class.cast(obj);
      return super.equals(that) && Objects.equal(this.attachment, that.attachment);
   }
   
   protected ToStringHelper string() {
      return super.string()
            .add("attachment", attachment);
   }
   
}
