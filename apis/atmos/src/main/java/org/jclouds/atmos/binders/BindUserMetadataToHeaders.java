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
package org.jclouds.atmos.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.atmos.domain.UserMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.rest.Binder;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Multimaps;

/**
 * @author Adrian Cole
 */
@Singleton
public class BindUserMetadataToHeaders implements Binder, Function<UserMetadata, Map<String, String>> {
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof UserMetadata,
            "this binder is only valid for UserMetadatas!");
      checkNotNull(request, "request");

      return ModifyRequest.putHeaders(request, Multimaps.forMap(apply(UserMetadata.class.cast(input))));
   }

   @Override
   public Map<String, String> apply(UserMetadata md) {
      Builder<String, String> headers = ImmutableMap.<String, String> builder();
      if (md.getMetadata().size() > 0) {
         String header = Joiner.on(',').withKeyValueSeparator("=").join(md.getMetadata());
         headers.put("x-emc-meta", header);
      }
      if (md.getListableMetadata().size() > 0) {
         String header = Joiner.on(',').withKeyValueSeparator("=").join(md.getListableMetadata());
         headers.put("x-emc-listable-meta", header);
      }
      if (md.getTags().size() > 0) {
         String header = Joiner.on(',').join(md.getTags());
         headers.put("x-emc-tags", header);
      }
      if (md.getListableTags().size() > 0) {
         String header = Joiner.on(',').join(md.getListableTags());
         headers.put("x-emc-listable-tags", header);
      }
      return headers.build();
   }
}
