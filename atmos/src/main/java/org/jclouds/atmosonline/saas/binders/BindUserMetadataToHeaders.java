/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.atmosonline.saas.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.UserMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.base.Joiner;

@Singleton
public class BindUserMetadataToHeaders implements Binder {

   public void bindToRequest(HttpRequest request, Object payload) {
      UserMetadata md = (UserMetadata) checkNotNull(payload, "payload");
      if (md.getMetadata().size() > 0) {
         String header = Joiner.on(',').withKeyValueSeparator("=").join(md.getMetadata());
         request.getHeaders().put("x-emc-meta", header);
      }
      if (md.getListableMetadata().size() > 0) {
         String header = Joiner.on(',').withKeyValueSeparator("=").join(md.getListableMetadata());
         request.getHeaders().put("x-emc-listable-meta", header);
      }
      if (md.getTags().size() > 0) {
         String header = Joiner.on(',').join(md.getTags());
         request.getHeaders().put("x-emc-tags", header);
      }
      if (md.getListableTags().size() > 0) {
         String header = Joiner.on(',').join(md.getListableTags());
         request.getHeaders().put("x-emc-listable-tags", header);
      }
   }
}
