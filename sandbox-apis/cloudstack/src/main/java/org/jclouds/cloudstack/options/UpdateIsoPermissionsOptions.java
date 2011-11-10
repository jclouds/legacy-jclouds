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
package org.jclouds.cloudstack.options;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import org.jclouds.cloudstack.domain.PermissionOperation;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options for the Iso updateIsoPermissions method.
 *
 * @see org.jclouds.cloudstack.features.IsoClient#updateIsoPermissions
 * @see org.jclouds.cloudstack.features.IsoAsyncClient#updateIsoPermissions
 * @author Richard Downer
 */
public class UpdateIsoPermissionsOptions extends BaseHttpRequestOptions {

   public static final UpdateIsoPermissionsOptions NONE = new UpdateIsoPermissionsOptions(); 

   /**
    * @param accounts a comma delimited list of accounts. If specified, "op" parameter has to be passed in.
    */
   public UpdateIsoPermissionsOptions accounts(Iterable<String> accounts) {
      this.queryParameters.replaceValues("accounts", ImmutableSet.of(Joiner.on(',').join(accounts)));
      return this;
   }

   /**
    * @param isExtractable true if the template/iso is extractable, false other wise. Can be set only by root admin
    */
   public UpdateIsoPermissionsOptions isExtractable(boolean isExtractable) {
      this.queryParameters.replaceValues("isextractable", ImmutableSet.of(isExtractable + ""));
      return this;
   }

   /**
    * @param isFeatured true for featured template/iso, false otherwise
    */
   public UpdateIsoPermissionsOptions isFeatured(boolean isFeatured) {
      this.queryParameters.replaceValues("isfeatured", ImmutableSet.of(isFeatured + ""));
      return this;
   }

   /**
    * @param isPublic true for public template/iso, false for private templates/isos
    */
   public UpdateIsoPermissionsOptions isPublic(boolean isPublic) {
      this.queryParameters.replaceValues("ispublic", ImmutableSet.of(isPublic + ""));
      return this;
   }

   /**
    * @param operation permission operator (add, remove, reset)
    */
   public UpdateIsoPermissionsOptions operation(PermissionOperation operation) {
      this.queryParameters.replaceValues("op", ImmutableSet.of(operation + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param accounts a comma delimited list of accounts. If specified, "op" parameter has to be passed in.
       */
      public static UpdateIsoPermissionsOptions accounts(Iterable<String> accounts) {
         return new UpdateIsoPermissionsOptions().accounts(accounts);
      }

      /**
       * @param isExtractable true if the template/iso is extractable, false other wise. Can be set only by root admin
       */
      public static UpdateIsoPermissionsOptions isExtractable(boolean isExtractable) {
         return new UpdateIsoPermissionsOptions().isExtractable(isExtractable);
      }

      /**
       * @param isFeatured true for featured template/iso, false otherwise
       */
      public static UpdateIsoPermissionsOptions isFeatured(boolean isFeatured) {
         return new UpdateIsoPermissionsOptions().isFeatured(isFeatured);
      }

      /**
       * @param isPublic true for public template/iso, false for private templates/isos
       */
      public static UpdateIsoPermissionsOptions isPublic(boolean isPublic) {
         return new UpdateIsoPermissionsOptions().isPublic(isPublic);
      }

      /**
       * @param operation permission operator (add, remove, reset)
       */
      public static UpdateIsoPermissionsOptions operation(PermissionOperation operation) {
         return new UpdateIsoPermissionsOptions().operation(operation);
      }
   }

}
