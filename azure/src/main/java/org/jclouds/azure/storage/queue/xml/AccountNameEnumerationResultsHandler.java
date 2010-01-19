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
package org.jclouds.azure.storage.queue.xml;

import java.net.URI;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.azure.storage.domain.BoundedSet;
import org.jclouds.azure.storage.domain.internal.BoundedHashSet;
import org.jclouds.azure.storage.queue.domain.QueueMetadata;
import org.jclouds.http.functions.ParseSax;

import com.google.common.collect.Sets;

/**
 * Parses the following XML document:
 * <p/>
 * EnumerationResults AccountName="http://myaccount.queue.core.windows.net"
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179352.aspx" />
 * @author Adrian Cole
 */
public class AccountNameEnumerationResultsHandler extends
         ParseSax.HandlerWithResult<BoundedSet<QueueMetadata>> {

   private Set<QueueMetadata> metadata = Sets.newLinkedHashSet();
   private URI currentUrl;
   private String prefix;
   private String marker;
   private int maxResults;
   private String nextMarker;
   private String currentName;

   private StringBuilder currentText = new StringBuilder();

   @Inject
   public AccountNameEnumerationResultsHandler() {
   }

   public BoundedSet<QueueMetadata> getResult() {
      return new BoundedHashSet<QueueMetadata>(metadata, currentUrl, prefix, marker, maxResults,
               nextMarker);
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("MaxResults")) {
         maxResults = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equals("Marker")) {
         marker = currentText.toString().trim();
         marker = (marker.equals("")) ? null : marker;
      } else if (qName.equals("Prefix")) {
         prefix = currentText.toString().trim();
         prefix = (prefix.equals("")) ? null : prefix;
      } else if (qName.equals("NextMarker")) {
         nextMarker = currentText.toString().trim();
         nextMarker = (nextMarker.equals("")) ? null : nextMarker;
      } else if (qName.equals("Queue")) {
         metadata.add(new QueueMetadata(currentName, currentUrl));
         currentUrl = null;
         currentName = null;
      } else if (qName.equals("Url")) {
         currentUrl = URI.create(currentText.toString().trim());
      } else if (qName.equals("QueueName")) {
         currentName = currentText.toString().trim();
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
