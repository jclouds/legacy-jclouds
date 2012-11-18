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
package org.jclouds.simpledb.xml;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jclouds.simpledb.domain.ListDomainsResponse;
import org.jclouds.http.functions.ParseSax;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Parses the following XML document:
 * <p/>
 * ListDomainsResponse
 * 
 * @author Adrian Cole
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AmazonSimpleDB/2009-04-15/DeveloperGuide/SDB_API_ListDomains.htm l"
 *      />
 */
public class ListDomainsResponseHandler extends ParseSax.HandlerWithResult<ListDomainsResponse> {
   private StringBuilder currentText = new StringBuilder();

   private Set<String> domains = Sets.newLinkedHashSet();
   private String nextToken;

   @Override
   public ListDomainsResponse getResult() {
      return new ListDomainsResponseImpl(domains, nextToken);
   }

   public static class ListDomainsResponseImpl extends LinkedHashSet<String> implements ListDomainsResponse {

      private final String nextToken;

      public ListDomainsResponseImpl(Iterable<String> domains, String nextToken) {
         Iterables.addAll(this, domains);
         this.nextToken = nextToken;
      }

      @Override
      public String getNextToken() {
         return nextToken;
      }

   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("DomainName")) {
         domains.add(currentText.toString().trim());
      } else if (qName.equals("NextToken")) {
         if (!currentText.toString().equals(""))
            this.nextToken = currentText.toString().trim();
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
