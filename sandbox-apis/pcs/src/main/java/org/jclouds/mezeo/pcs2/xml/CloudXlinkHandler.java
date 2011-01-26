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

package org.jclouds.mezeo.pcs2.xml;

import java.net.URI;
import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.mezeo.pcs2.PCSCloudAsyncClient;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * Parses the discovery response from xlink refs.
 * 
 * @author Adrian Cole
 */
public class CloudXlinkHandler extends ParseSax.HandlerWithResult<PCSCloudAsyncClient.Response> {

   private Map<String, URI> map = Maps.newHashMap();

   public static class PCSCloudResponseImpl implements PCSCloudAsyncClient.Response {
      private final Map<String, URI> map;

      public PCSCloudResponseImpl(Map<String, URI> map) {
         this.map = map;
      }

      public URI getContactsUrl() {
         return map.get("contacts");
      }

      public URI getMetacontainersUrl() {
         return map.get("metacontainers");
      }

      public URI getProjectsUrl() {
         return map.get("projects");
      }

      public URI getRecyclebinUrl() {
         return map.get("recyclebin");
      }

      public URI getRootContainerUrl() {
         return map.get("rootContainer");
      }

      public URI getSharesUrl() {
         return map.get("shares");
      }

      public URI getTagsUrl() {
         return map.get("tags");
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((map == null) ? 0 : map.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         PCSCloudResponseImpl other = (PCSCloudResponseImpl) obj;
         if (map == null) {
            if (other.map != null)
               return false;
         } else if (!map.equals(other.map))
            return false;
         return true;
      }

      @Override
      public String toString() {
         return "PCSDiscoveryResponseImpl [map=" + map + "]";
      }

   }

   public PCSCloudAsyncClient.Response getResult() {
      return new PCSCloudResponseImpl(map);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      int index = attributes.getIndex("xlink:href");
      if (index != -1) {
         map.put(qName, URI.create(attributes.getValue(index)));
      }
   }

}
