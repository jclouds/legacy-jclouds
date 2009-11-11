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
package org.jclouds.rackspace.cloudfiles;

import static org.jclouds.rackspace.cloudfiles.reference.CloudFilesConstants.PROPERTY_CLOUDFILES_METADATA_PREFIX;
import static org.jclouds.rackspace.cloudfiles.reference.CloudFilesConstants.PROPERTY_CLOUDFILES_RETRY;
import static org.jclouds.rackspace.cloudfiles.reference.CloudFilesConstants.PROPERTY_CLOUDFILES_TIMEOUT;

import java.util.Properties;

import org.jclouds.rackspace.RackspacePropertiesBuilder;

/**
 * Builds properties used in CloudFiles Connections
 * 
 * @author Adrian Cole
 */
public class CloudFilesPropertiesBuilder extends RackspacePropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_CLOUDFILES_METADATA_PREFIX, "X-Object-Meta-");
      return properties;
   }

   public CloudFilesPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public CloudFilesPropertiesBuilder(String id, String secret) {
      super(id, secret);
   }

   /**
    * longest time a single synchronous operation can take before throwing an exception.
    */
   public CloudFilesPropertiesBuilder withRequestTimeout(long milliseconds) {
      properties.setProperty(PROPERTY_CLOUDFILES_TIMEOUT, Long.toString(milliseconds));
      return this;
   }

   /**
    * longest time a single synchronous operation can take before throwing an exception.
    */
   public CloudFilesPropertiesBuilder withMaxRetries(int retries) {
      properties.setProperty(PROPERTY_CLOUDFILES_RETRY, Integer.toString(retries));
      return this;
   }

   protected CloudFilesPropertiesBuilder withMetaPrefix(String prefix) {
      properties.setProperty(PROPERTY_CLOUDFILES_METADATA_PREFIX, prefix);
      return this;
   }
}
