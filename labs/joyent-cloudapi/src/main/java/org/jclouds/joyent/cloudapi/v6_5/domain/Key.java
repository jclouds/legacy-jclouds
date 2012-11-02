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
package org.jclouds.joyent.cloudapi.v6_5.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * Keys are the means by which you operate on your SSH/signing keys. Currently
 * CloudAPI supports uploads of public keys in the OpenSSH format.
 * 
 * @author Adrian Cole
 * @see <a href="http://apidocs.joyent.com/sdcapidoc/cloudapi/index.html#ListKeys" >docs</a>
 */
public class Key implements Comparable<Key> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromKey(this);
   }
   
   public static class Builder {
      private String name;
      private String key;
      private Date created = new Date();

      /**
       * @see Key#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see Key#get()
       */
      public Builder key(String key) {
         this.key = key;
         return this;
      }

      /**
       * @see Key#getCreated()
       */
      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      public Key build() {
         return new Key(name, key, created);
      }

      public Builder fromKey(Key in) {
         return name(in.getName()).key(in.get()).created(in.getCreated());
      }
   }

   protected final String name;
   protected final String key;
   // don't include created in the http request
   protected final transient Date created;
   
   @ConstructorProperties({ "name", "key", "created" })
   public Key(String name, String key, Date created) {
      this.name = checkNotNull(name, "name");
      this.key = checkNotNull(key, "key: OpenSSH formatted public key of key(%s)", name);
      this.created = checkNotNull(created, "created date of key(%s)", name);
   }

   /**
    * Name for this key
    */
   public String getName() {
      return name;
   }

   /**
    * OpenSSH formatted public key
    */
   public String get() {
      return key;
   }
   
   /**
    * Date the key was created
    */
   public Date getCreated() {
      return created;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Key) {
         Key that = Key.class.cast(object);
         return Objects.equal(name, that.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").omitNullValues()
                    .add("name", name)
                    .add("key", key)
                    .add("created", created).toString();
   }
   
   @Override
   public int compareTo(Key that) {
      return ComparisonChain.start()
                            .compare(this.name, that.name)
                            .compare(this.created, that.created).result();
   }
}
