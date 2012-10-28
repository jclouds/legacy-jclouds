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
package org.jclouds.oauth.v2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The header for the OAuth token, contains the signer algorithm's name and the type of the token
 *
 * @author David Alves
 */
public class Header {

   private final String signerAlgorithm;
   private final String type;

   Header(String signerAlgorithm, String type) {
      this.signerAlgorithm = signerAlgorithm;
      this.type = type;
   }

   public String getSignerAlgorithm() {
      return signerAlgorithm;
   }

   public String getType() {
      return type;
   }

   public static class Builder {

      private String signerAlgorithm;
      private String type;

      public Builder signer(String signerAlgorithm) {
         this.signerAlgorithm = checkNotNull(signerAlgorithm);
         return this;
      }

      public Builder type(String type) {
         this.type = checkNotNull(type);
         return this;
      }

      public Header build() {
         return new Header(checkNotNull(signerAlgorithm), checkNotNull(type));
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Header header = (Header) o;

      if (!signerAlgorithm.equals(header.signerAlgorithm)) return false;
      if (!type.equals(header.type)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = signerAlgorithm.hashCode();
      result = 31 * result + type.hashCode();
      return result;
   }
}
