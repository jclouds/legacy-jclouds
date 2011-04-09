/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ibmdev.functions;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.ibmdev.domain.Volume;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class ParseVolumesFromJson implements
      Function<HttpResponse, Set<? extends Volume>> {

   private final ParseJson<VolumeListResponse> json;

   @Inject
   ParseVolumesFromJson(ParseJson<VolumeListResponse> json) {
      this.json = json;
   }

   private static class VolumeListResponse {
      Set<Volume> volumes = Sets.newLinkedHashSet();
   }

   @Override
   public Set<? extends Volume> apply(HttpResponse arg0) {
      return ParseUtils
            .clean(json.apply(arg0).volumes, ParseUtils.CLEAN_VOLUME);
   }
}