/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.deltacloud.xml;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.deltacloud.domain.DeltacloudCollection;
import org.jclouds.deltacloud.domain.Feature;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class DeltacloudCollectionHandler extends ParseSax.HandlerWithResult<DeltacloudCollection> {
   private URI href;
   private String rel;
   private Set<Feature> features = Sets.newLinkedHashSet();

   private DeltacloudCollection realm;

   public DeltacloudCollection getResult() {
      return realm;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (qName.equalsIgnoreCase("link")) {
         this.href = URI.create(attributes.get("href"));
         this.rel = attributes.get("rel");
      } else if (qName.equalsIgnoreCase("feature")) {
         features.add(new Feature(attributes.get("name")));
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equalsIgnoreCase("link")) {
         this.realm = new DeltacloudCollection(href, rel, features);
         this.href = null;
         this.rel = null;
         this.features = Sets.newLinkedHashSet();
      }
   }

}
