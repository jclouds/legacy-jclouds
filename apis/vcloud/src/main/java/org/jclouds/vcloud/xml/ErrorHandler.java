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
package org.jclouds.vcloud.xml;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.jclouds.vcloud.domain.VCloudError;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class ErrorHandler extends ParseSax.HandlerWithResult<VCloudError> {
   private VCloudError error;

   public VCloudError getResult() {
      return error;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (SaxUtils.equalsOrSuffix(qName, "Error")) {
         error = Utils.newError(attributes);
      }
   }
}
