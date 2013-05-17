/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.options;

import org.jclouds.cloudstack.domain.PermissionOperation;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

/**
 * Options for the ISO updateISOPermissions method.
 *
 * @see org.jclouds.cloudstack.features.ISOClient#updateISOPermissions
 * @see org.jclouds.cloudstack.features.ISOAsyncClient#updateISOPermissions
 * @author Richard Downer
 */
public class UpdateISOPermissionsOptions extends BaseHttpRequestOptions {

   public static final UpdateISOPermissionsOptions NONE = new UpdateISOPermissionsOptions();

   /**
    * @param accounts a comma delimited list of accounts. If specified, "op" parameter has to be passed in.
    */
   public UpdateISOPermissionsOptions accounts(Iterable<String> accounts) {
      this.queryParameters.replaceValues("accounts", ImmutableSet.of(Joiner.on(',').join(accounts)));
      return this;
   }

   /**
    * @param isExtractable true if the template/iso is extractable, false other wise. Can be set only by root admin
    */
   public UpdateISOPermissionsOptions isExtractable(boolean isExtractable) {
      this.queryParameters.replaceValues("isextractable", ImmutableSet.of(isExtractable + ""));
      return this;
   }

   /**
    * @param isFeatured true for featured template/iso, false otherwise
    */
   public UpdateISOPermissionsOptions isFeatured(boolean isFeatured) {
      this.queryParameters.replaceValues("isfeatured", ImmutableSet.of(isFeatured + ""));
      return this;
   }

   /**
    * @param isPublic true for public template/iso, false for private templates/isos
    */
   public UpdateISOPermissionsOptions isPublic(boolean isPublic) {
      this.queryParameters.replaceValues("ispublic", ImmutableSet.of(isPublic + ""));
      return this;
   }

   /**
    * @param operation permission operator (add, remove, reset)
    */
   public UpdateISOPermissionsOptions operation(PermissionOperation operation) {
      this.queryParameters.replaceValues("op", ImmutableSet.of(operation + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param accounts a comma delimited list of accounts. If specified, "op" parameter has to be passed in.
       */
      public static UpdateISOPermissionsOptions accounts(Iterable<String> accounts) {
         return new UpdateISOPermissionsOptions().accounts(accounts);
      }

      /**
       * @param isExtractable true if the template/iso is extractable, false other wise. Can be set only by root admin
       */
      public static UpdateISOPermissionsOptions isExtractable(boolean isExtractable) {
         return new UpdateISOPermissionsOptions().isExtractable(isExtractable);
      }

      /**
       * @param isFeatured true for featured template/iso, false otherwise
       */
      public static UpdateISOPermissionsOptions isFeatured(boolean isFeatured) {
         return new UpdateISOPermissionsOptions().isFeatured(isFeatured);
      }

      /**
       * @param isPublic true for public template/iso, false for private templates/isos
       */
      public static UpdateISOPermissionsOptions isPublic(boolean isPublic) {
         return new UpdateISOPermissionsOptions().isPublic(isPublic);
      }

      /**
       * @param operation permission operator (add, remove, reset)
       */
      public static UpdateISOPermissionsOptions operation(PermissionOperation operation) {
         return new UpdateISOPermissionsOptions().operation(operation);
      }
   }

}
