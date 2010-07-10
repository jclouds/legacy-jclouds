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
package org.jclouds.http.payloads;

import java.io.InputStream;

import org.jclouds.util.Utils;

/**
 * @author Adrian Cole
 */
public class StringPayload extends BasePayload<String> {

   public StringPayload(String content) {
      super(content, null, new Long(content.length()), null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public InputStream getInput() {
      return Utils.toInputStream(content);
   }

}