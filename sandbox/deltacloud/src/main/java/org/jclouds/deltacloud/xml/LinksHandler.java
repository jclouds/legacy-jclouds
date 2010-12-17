/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.links/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.deltacloud.xml;

import java.net.URI;
import java.util.Map;

import org.jclouds.deltacloud.reference.DeltacloudCollection;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class LinksHandler extends ParseSax.HandlerWithResult<Map<DeltacloudCollection, URI>> {

   private Map<DeltacloudCollection, URI> links = Maps.newLinkedHashMap();

   public Map<DeltacloudCollection, URI> getResult() {
      return links;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = Utils.cleanseAttributes(attrs);
      if (qName.equals("link")) {
         String rel = attributes.get("rel");
         if (rel != null) {
            DeltacloudCollection link = DeltacloudCollection.fromValue(rel);
            links.put(link, URI.create(attributes.get("href")));
         }
      }
   }
}
