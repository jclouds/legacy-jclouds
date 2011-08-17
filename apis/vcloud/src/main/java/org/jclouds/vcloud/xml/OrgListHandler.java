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

import static org.jclouds.vcloud.util.Utils.putReferenceType;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.jclouds.vcloud.domain.ReferenceType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class OrgListHandler extends ParseSax.HandlerWithResult<Map<String, ReferenceType>> {

   private Map<String, ReferenceType> org = Maps.newHashMap();

   public Map<String, ReferenceType> getResult() {
      return org;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (qName.endsWith("Org")) {
         String type = attributes.get("type");
         if (type != null) {
            if (type.indexOf("org+xml") != -1) {
               putReferenceType(org, attributes);
            }
         }
      }
   }
}
