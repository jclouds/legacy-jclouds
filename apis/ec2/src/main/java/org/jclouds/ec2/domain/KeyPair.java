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
package org.jclouds.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.crypto.SshKeys;
import org.jclouds.javax.annotation.Nullable;

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateKeyPair.html"
 *      />
 * @author Adrian Cole
 */
public class KeyPair implements Comparable<KeyPair> {
   @Override
   public String toString() {
      return "[region=" + region + ", keyName=" + keyName + ", fingerprint=" + fingerprint + ", sha1OfPrivateKey="
               + sha1OfPrivateKey + ", keyMaterial?=" + (keyMaterial != null) + "]";
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String region;
      private String keyName;
      private String fingerprint;
      private String sha1OfPrivateKey;
      private String keyMaterial;

      public Builder region(String region) {
         this.region = region;
         return this;
      }

      public Builder keyName(String keyName) {
         this.keyName = keyName;
         return this;
      }

      public Builder sha1OfPrivateKey(String sha1OfPrivateKey) {
         this.sha1OfPrivateKey = sha1OfPrivateKey;
         return this;
      }

      public Builder keyMaterial(String keyMaterial) {
         this.keyMaterial = keyMaterial;
         return this;
      }

      public Builder fingerprint(String fingerprint) {
         this.fingerprint = fingerprint;
         return this;
      }

      public KeyPair build() {
         if (fingerprint == null && keyMaterial != null)
            fingerprint(SshKeys.fingerprintPrivateKey(keyMaterial));
         return new KeyPair(region, keyName, sha1OfPrivateKey, keyMaterial, fingerprint);
      }

      public static Builder fromKeyPair(KeyPair in) {
         return new Builder().region(in.getRegion()).keyName(in.getKeyName()).sha1OfPrivateKey(in.getSha1OfPrivateKey())
                  .keyMaterial(in.getKeyMaterial());
      }
   }

   private final String region;
   private final String keyName;
   private final String sha1OfPrivateKey;
   @Nullable
   private final String keyMaterial;
   @Nullable
   private final String fingerprint;

   public KeyPair(String region, String keyName, String sha1OfPrivateKey, @Nullable String keyMaterial,
            @Nullable String fingerprint) {
      this.region = checkNotNull(region, "region");
      this.keyName = checkNotNull(keyName, "keyName");
      this.sha1OfPrivateKey = checkNotNull(sha1OfPrivateKey, "sha1OfPrivateKey");
      this.keyMaterial = keyMaterial;// nullable on list
      this.fingerprint = fingerprint;// nullable on list
   }

   /**
    * Key pairs (to connect to instances) are Region-specific.
    */
   public String getRegion() {
      return region;
   }

   /**
    * {@inheritDoc}
    */
   public int compareTo(KeyPair o) {
      return (this == o) ? 0 : getKeyName().compareTo(o.getKeyName());
   }

   /**
    * A SHA-1 digest of the DER encoded private key.
    * 
    * @see SshKeys#sha1
    */
   public String getSha1OfPrivateKey() {
      return sha1OfPrivateKey;
   }

   /**
    * fingerprint per the following <a
    * href="http://tools.ietf.org/html/draft-friedl-secsh-fingerprint-00" >spec</a>
    * 
    * @see SshKeys#fingerprint
    */
   public String getFingerprint() {
      return fingerprint;
   }

   /**
    * An unencrypted PEM encoded RSA private key.
    */
   public String getKeyMaterial() {
      return keyMaterial;
   }

   /**
    * The key pair name provided in the original request.
    */
   public String getKeyName() {
      return keyName;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((fingerprint == null) ? 0 : fingerprint.hashCode());
      result = prime * result + ((keyMaterial == null) ? 0 : keyMaterial.hashCode());
      result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + ((sha1OfPrivateKey == null) ? 0 : sha1OfPrivateKey.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      KeyPair other = (KeyPair) obj;
      if (fingerprint == null) {
         if (other.fingerprint != null)
            return false;
      } else if (!fingerprint.equals(other.fingerprint))
         return false;
      if (keyMaterial == null) {
         if (other.keyMaterial != null)
            return false;
      } else if (!keyMaterial.equals(other.keyMaterial))
         return false;
      if (keyName == null) {
         if (other.keyName != null)
            return false;
      } else if (!keyName.equals(other.keyName))
         return false;
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
         return false;
      if (sha1OfPrivateKey == null) {
         if (other.sha1OfPrivateKey != null)
            return false;
      } else if (!sha1OfPrivateKey.equals(other.sha1OfPrivateKey))
         return false;
      return true;
   }

   public Builder toBuilder() {
      return Builder.fromKeyPair(this);
   }
}
