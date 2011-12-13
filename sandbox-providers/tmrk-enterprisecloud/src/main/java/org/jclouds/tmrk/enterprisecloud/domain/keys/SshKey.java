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
package org.jclouds.tmrk.enterprisecloud.domain.keys;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.Task;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Entity;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * <xs:complexType name="SshKeyType">
 * @author Jason King
 */
@XmlRootElement(name = "SshKey")
public class SSHKey extends Entity<SSHKey> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromSshKey(this);
   }

   public static class Builder extends Entity.Builder<SSHKey> {
      private boolean defaultKey;
      private String fingerPrint;
      private String privateKey;
      
      /**
       * @see SSHKey#isDefaultKey
       */
      public Builder defaultKey(boolean defaultKey) {
         this.defaultKey = defaultKey;
         return this;
      }

      /**
       * @see SSHKey#getFingerPrint
       */
      public Builder fingerPrint(String fingerPrint) {
         this.fingerPrint = fingerPrint;
         return this;
      }

      /**
       * @see SSHKey#getPrivateKey
       */
      public Builder privateKey(String privateKey) {
         this.privateKey = privateKey;
         return this;
      }

      @Override
      public SSHKey build() {
         return new SSHKey(href, type, name, links, actions, tasks, defaultKey, fingerPrint, privateKey);
      }

      public Builder fromSshKey(SSHKey in) {
         return fromEntity(in).defaultKey(in.isDefaultKey()).fingerPrint(in.getFingerPrint()).privateKey(in.getPrivateKey());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<SSHKey> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<SSHKey> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromEntity(Entity<SSHKey> in) {
         return Builder.class.cast(super.fromEntity(in));
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder actions(Set<Action> actions) {
         return Builder.class.cast(super.actions(actions));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder tasks(Set<Task> tasks) {
         return Builder.class.cast(super.tasks(tasks));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }

   }

   @XmlElement(name = "Default", required = false)
   private boolean defaultKey;

   @XmlElement(name = "FingerPrint", required = false)
   private String fingerPrint;

   @XmlElement(name = "PrivateKey", required = false)
   private String privateKey;

   private SSHKey(URI href, String type, String name, Set<Link> links, Set<Action> actions, Set<Task> tasks,
                  boolean defaultKey, @Nullable String fingerPrint, @Nullable String privateKey) {
      super(href, type, name, links, actions, tasks);
      this.defaultKey = defaultKey;
      this.fingerPrint = fingerPrint;
      this.privateKey = privateKey;
   }

   private SSHKey() {
       //For JAXB
   }

   public boolean isDefaultKey() {
      return defaultKey;
   }

   public String getFingerPrint() {
      return fingerPrint;
   }

   public String getPrivateKey() {
      return privateKey;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      SSHKey sshKey = (SSHKey) o;

      if (defaultKey != sshKey.defaultKey) return false;
      if (fingerPrint != null ? !fingerPrint.equals(sshKey.fingerPrint) : sshKey.fingerPrint != null)
         return false;
      if (privateKey != null ? !privateKey.equals(sshKey.privateKey) : sshKey.privateKey != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (defaultKey ? 1 : 0);
      result = 31 * result + (fingerPrint != null ? fingerPrint.hashCode() : 0);
      result = 31 * result + (privateKey != null ? privateKey.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", defaultKey="+defaultKey+", fingerPrint="+fingerPrint+", privateKey="+privateKey;
   }

}