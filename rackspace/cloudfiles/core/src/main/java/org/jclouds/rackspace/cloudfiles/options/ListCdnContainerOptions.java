/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.rackspace.cloudfiles.options;


/**
 * Contains options supported in the REST API for the GET container operation. <h2>
 */
public class ListCdnContainerOptions extends ListContainerOptions {
   public static final ListCdnContainerOptions NONE = new ListCdnContainerOptions();

   public ListCdnContainerOptions isCdnEnabled(boolean enabledOnly) {
      queryParameters.put("enabled_only", (enabledOnly ? "true" : "false"));
      return this;
   }

   public static class Builder extends ListContainerOptions.Builder {
      public static ListCdnContainerOptions isCdnEnabled(boolean enabledOnly) {
         ListCdnContainerOptions options = new ListCdnContainerOptions();
         return options.isCdnEnabled(enabledOnly);
      }
   }
   
}
