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
package org.jclouds.io.payloads;

import java.io.InputStream;
import java.util.Comparator;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.utils.Queries;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.util.Strings2;

import com.google.common.collect.Multimap;

/**
 * @author Adrian Cole
 */
public class UrlEncodedFormPayload extends BasePayload<String> {
   public UrlEncodedFormPayload(Multimap<String, String> formParams, char... skips) {
      this(formParams, null, skips);
   }

   public UrlEncodedFormPayload(Multimap<String, String> formParams,
            @Nullable Comparator<Map.Entry<String, String>> sorter, char... skips) {
      super(Queries.makeQueryLine(formParams, sorter, skips));
      getContentMetadata().setContentLength((long) content.length());
      getContentMetadata().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public InputStream getInput() {
      return Strings2.toInputStream(content);
   }

}
