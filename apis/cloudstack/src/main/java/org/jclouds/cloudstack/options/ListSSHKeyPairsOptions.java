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

import com.google.common.collect.ImmutableSet;

/**
 * @author Vijay Kiran
 */
public class ListSSHKeyPairsOptions extends BaseHttpRequestOptions {

   public static final ListSSHKeyPairsOptions NONE = new ListSSHKeyPairsOptions();

   /**
    * @param name
    *           the SSHKeyPair name
    */
   public ListSSHKeyPairsOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   /**
    * @param projectId
    *           the project to list in
    */
   public ListSSHKeyPairsOptions projectId(String projectId) {
      this.queryParameters.replaceValues("projectId", ImmutableSet.of(projectId + ""));
      return this;
   }

   public static class Builder {
      /**
       * @see ListSSHKeyPairsOptions#name
       */
      public static ListSSHKeyPairsOptions name(String name) {
         ListSSHKeyPairsOptions options = new ListSSHKeyPairsOptions();
         return options.name(name);
      }

      /**
       * @see ListSSHKeyPairsOptions#projectId(String)
       */
      public static ListSSHKeyPairsOptions projectId(String projectId) {
         ListSSHKeyPairsOptions options = new ListSSHKeyPairsOptions();
         return options.projectId(projectId);
      }

   }
}
