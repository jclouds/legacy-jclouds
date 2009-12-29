/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.internal.Nullable;

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateKeyPair.html"
 *      />
 * @author Adrian Cole
 */
public class KeyPair implements Comparable<KeyPair> {
   private final Region region;
   private final String keyName;
   private final String keyFingerprint;
   @Nullable
   private final String keyMaterial;

   public KeyPair(Region region, String keyName, String keyFingerprint, @Nullable String keyMaterial) {
      this.region = checkNotNull(region, "region");
      this.keyName = checkNotNull(keyName, "keyName");
      this.keyFingerprint = checkNotNull(keyFingerprint, "keyFingerprint");
      this.keyMaterial = keyMaterial;// nullable on list
   }

   /**
    * Key pairs (to connect to instances) are Region-specific.
    */
   public Region getRegion() {
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
    */
   public String getKeyFingerprint() {
      return keyFingerprint;
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
      result = prime * result + ((keyFingerprint == null) ? 0 : keyFingerprint.hashCode());
      result = prime * result + ((keyMaterial == null) ? 0 : keyMaterial.hashCode());
      result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
      result = prime * result + ((region == null) ? 0 : region.hashCode());
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
      if (keyFingerprint == null) {
         if (other.keyFingerprint != null)
            return false;
      } else if (!keyFingerprint.equals(other.keyFingerprint))
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
      return true;
   }

}