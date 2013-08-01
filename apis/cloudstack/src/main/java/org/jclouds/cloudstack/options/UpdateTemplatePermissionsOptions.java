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

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

/**
 * Options used to control how a template should be updated.
 *
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.8/api/user/updateTemplate.html"
 *      />
 * @author Richard Downer
 */
public class UpdateTemplatePermissionsOptions extends BaseHttpRequestOptions {

   /**
    * a list of accounts. If specified, "op" parameter has to be passed in.
    */
   public UpdateTemplatePermissionsOptions accounts(Iterable<String> accounts) {
      this.queryParameters.replaceValues("accounts", ImmutableSet.of(Joiner.on(',').join(accounts)));
      return this;
   }

   /**
    * a list of projects. If specified, "op" parameter has to be passed in.
    */
   public UpdateTemplatePermissionsOptions projectIds(Iterable<String> projectIds) {
      this.queryParameters.replaceValues("projectids", ImmutableSet.of(Joiner.on(',').join(projectIds)));
      return this;
   }

   /**
    * true if the template/iso is extractable, false other wise. Can be set only by root admin
    */
   public UpdateTemplatePermissionsOptions isExtractable(boolean isExtractable) {
      this.queryParameters.replaceValues("isextractable", ImmutableSet.of(isExtractable + ""));
      return this;
   }

   /**
    * true for featured template/iso, false otherwise
    */
   public UpdateTemplatePermissionsOptions isFeatured(boolean isFeatured) {
      this.queryParameters.replaceValues("isfeatured", ImmutableSet.of(isFeatured + ""));
      return this;
   }

   /**
    * true for public template/iso, false for private templates/isos
    */
   public UpdateTemplatePermissionsOptions isPublic(boolean isPublic) {
      this.queryParameters.replaceValues("ispublic", ImmutableSet.of(isPublic + ""));
      return this;
   }

   /**
    * permission operator (add, remove, reset)
    */
   public UpdateTemplatePermissionsOptions op(Operation op) {
      this.queryParameters.replaceValues("op", ImmutableSet.of(op + ""));
      return this;
   }

   public enum Operation {
      add, remove, reset
   }

   public static class Builder {

      public static UpdateTemplatePermissionsOptions accounts(Iterable<String> accounts) {
         UpdateTemplatePermissionsOptions options = new UpdateTemplatePermissionsOptions();
         return options.accounts(accounts);
      }

      public static UpdateTemplatePermissionsOptions projectIds(Iterable<String> projectIds) {
         UpdateTemplatePermissionsOptions options = new UpdateTemplatePermissionsOptions();
         return options.projectIds(projectIds);
      }

      public static UpdateTemplatePermissionsOptions isExtractable(boolean isExtractable) {
         UpdateTemplatePermissionsOptions options = new UpdateTemplatePermissionsOptions();
         return options.isExtractable(isExtractable);
      }

      public static UpdateTemplatePermissionsOptions isFeatured(boolean isFeatured) {
         UpdateTemplatePermissionsOptions options = new UpdateTemplatePermissionsOptions();
         return options.isFeatured(isFeatured);
      }

      public static UpdateTemplatePermissionsOptions isPublic(boolean isPublic) {
         UpdateTemplatePermissionsOptions options = new UpdateTemplatePermissionsOptions();
         return options.isPublic(isPublic);
      }

      public static UpdateTemplatePermissionsOptions op(Operation op) {
         UpdateTemplatePermissionsOptions options = new UpdateTemplatePermissionsOptions();
         return options.op(op);
      }
   }
   
}
