/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.io.payloads;
import static com.google.common.net.MediaType.FORM_DATA;
import static org.jclouds.http.utils.Queries.encodeQueryLine;
import static org.jclouds.util.Strings2.toInputStream;

import java.io.InputStream;

import com.google.common.collect.Multimap;

/**
 * @author Adrian Cole
 */
public class UrlEncodedFormPayload extends BasePayload<String> {
   public UrlEncodedFormPayload(Multimap<String, String> formParams) {
      super(encodeQueryLine(formParams));
      getContentMetadata().setContentLength((long) content.length());
      getContentMetadata().setContentType(FORM_DATA.toString());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public InputStream getInput() {
      return toInputStream(content);
   }

}
