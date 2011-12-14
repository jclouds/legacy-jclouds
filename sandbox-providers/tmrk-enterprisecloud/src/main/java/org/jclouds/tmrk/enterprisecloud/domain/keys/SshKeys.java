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

import com.google.common.collect.Sets;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * SshKeys is more than a simple wrapper as it extends Resource.
 * @author Jason King
 * 
 */
@XmlRootElement(name = "SshKeys")
public class SSHKeys extends Resource<SSHKeys> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromTemplates(this);
   }

   public static class Builder extends Resource.Builder<SSHKeys> {
      private Set<SSHKey> sshKeys = Sets.newLinkedHashSet();

      /**
       * @see SSHKeys#getSSHKeys
       */
      public Builder sshKeys(Set<SSHKey> sshKeys) {
         this.sshKeys =(checkNotNull(sshKeys,"sshKeys"));
         return this;
      }

      @Override
      public SSHKeys build() {
         return new SSHKeys(href, type, name, links, actions, sshKeys);
      }

      public Builder fromTemplates(SSHKeys in) {
         return fromResource(in).sshKeys(in.getSSHKeys());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<SSHKeys> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<SSHKeys> in) {
         return Builder.class.cast(super.fromResource(in));
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
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }

   }

   @XmlElement(name = "SshKey", required = false)
   private Set<SSHKey> sshKeys = Sets.newLinkedHashSet();

   private SSHKeys(URI href, String type, String name, Set<Link> links, Set<Action> actions, Set<SSHKey> sshKeys) {
      super(href, type, name, links, actions);
      this.sshKeys = checkNotNull(sshKeys,"sshKeys");
   }

   private SSHKeys() {
       //For JAXB
   }

   public Set<SSHKey> getSSHKeys() {
      return sshKeys;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      SSHKeys networks1 = (SSHKeys) o;

      if (!sshKeys.equals(networks1.sshKeys)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + sshKeys.hashCode();
      return result;
   }

   @Override
   public String string() {
      return super.string()+", sshKeys="+sshKeys;
   }
}