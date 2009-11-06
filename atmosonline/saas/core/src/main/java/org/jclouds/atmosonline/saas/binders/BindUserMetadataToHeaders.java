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
package org.jclouds.atmosonline.saas.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jclouds.atmosonline.saas.domain.UserMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

public class BindUserMetadataToHeaders implements Binder {

   public void bindToRequest(HttpRequest request, Object entity) {
      UserMetadata md = (UserMetadata) checkNotNull(entity, "entity");
      if (md.getMetadata().size() > 0) {
         String header = join(md.getMetadata());
         request.getHeaders().put("x-emc-meta", header);
      }
      if (md.getListableMetadata().size() > 0) {
         String header = join(md.getListableMetadata());
         request.getHeaders().put("x-emc-listable-meta", header);
      }
      if (md.getTags().size() > 0) {
         String header = join(md.getTags());
         request.getHeaders().put("x-emc-tags", header);
      }
      if (md.getListableTags().size() > 0) {
         String header = join(md.getListableTags());
         request.getHeaders().put("x-emc-listable-tags", header);
      }
   }

   private String join(Set<String> set) {
      StringBuffer header = new StringBuffer();
      for (String entry : set) {
         header.append(entry).append(",");
      }
      header.deleteCharAt(header.length() - 1);
      return header.toString();
   }

   private String join(Map<String, String> map) {
      StringBuffer header = new StringBuffer();
      for (Entry<String, String> entry : map.entrySet()) {
         header.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
      }
      header.deleteCharAt(header.length() - 1);
      return header.toString();
   }
}
