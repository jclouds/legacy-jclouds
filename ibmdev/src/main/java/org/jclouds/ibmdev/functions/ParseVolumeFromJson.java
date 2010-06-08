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
package org.jclouds.ibmdev.functions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.functions.ParseJson;
import org.jclouds.ibmdev.domain.Volume;

import com.google.gson.Gson;

/**
 * @author Adrian Cole
 */
@Singleton
public class ParseVolumeFromJson extends ParseJson<Volume> {
   @Inject
   public ParseVolumeFromJson(Gson gson) {
      super(gson);
   }

   @Override
   protected Volume apply(InputStream stream) {
      try {
         Volume returnVal = gson.fromJson(new InputStreamReader(stream, "UTF-8"), Volume.class);
         ParseUtils.CLEAN_VOLUME.apply(returnVal);
         return returnVal;
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}