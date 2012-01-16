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
package org.jclouds.vcloud.loaders;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.VAppTemplate;

import com.google.common.cache.CacheLoader;

@Singleton
public class VAppTemplateLoader extends CacheLoader<URI, VAppTemplate> {
   @Resource
   protected Logger logger = Logger.NULL;

   private final VCloudClient client;

   @Inject
   VAppTemplateLoader(VCloudClient client) {
      this.client = client;
   }

   @Override
   public VAppTemplate load(URI template) {
      return client.getVAppTemplateClient().getVAppTemplate(template);
   }
}