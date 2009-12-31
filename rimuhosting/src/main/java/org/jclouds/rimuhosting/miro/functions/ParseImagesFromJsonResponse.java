/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rimuhosting.miro.functions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rimuhosting.miro.domain.Image;
import org.jclouds.rimuhosting.miro.domain.internal.RimuHostingResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.SortedSet;

/**
 * @author Ivan Meredith
 */

@Singleton
public class ParseImagesFromJsonResponse extends ParseJson<SortedSet<Image>> {

   @Inject
   public ParseImagesFromJsonResponse(Gson gson) {
      super(gson);
   }

   private static class DistroResponse extends RimuHostingResponse {
      private SortedSet<Image> distro_infos;

      public SortedSet<Image> getDistroInfos() {
         return distro_infos;
      }

      public void setDistroInfos(SortedSet<Image> distro_infos) {
         this.distro_infos = distro_infos;
      }
   }

   public SortedSet<Image> apply(InputStream stream) {
      Type setType = new TypeToken<Map<String, DistroResponse>>() {
      }.getType();
      try {
         Map<String, DistroResponse> t = gson.fromJson(new InputStreamReader(stream, "UTF-8"), setType);
         return t.values().iterator().next().getDistroInfos();
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}