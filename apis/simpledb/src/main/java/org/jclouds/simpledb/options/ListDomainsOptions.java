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

package org.jclouds.simpledb.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Contains options supported in the Form API for the ListDomains operation. <h2>
 * Usage</h2> The recommended way to instantiate a ListDomainsOptions object is to statically import
 * ListDomainsOptions.Builder.* and invoke a static creation method followed by an instance mutator
 * (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.simpledb.options.ListDomainsOptions.Builder.*
 * <p/>
 * SimpleDBClient connection = // get connection
 * Set<String> domains = connection.listDomainsInRegion(maxNumberOfDomains(1));
 * <code>
 * 
 * @author Adrian Cole
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AmazonSimpleDB/2009-04-15/DeveloperGuide/SDB_API_ListDomains.html"
 *      />
 */
public class ListDomainsOptions extends BaseHttpRequestOptions {

   /**
    * The maximum number of domain names you want returned.
    * 
    * @param maxNumberOfDomains
    *           Maximum 100
    */
   public ListDomainsOptions maxNumberOfDomains(int maxNumberOfDomains) {
      checkArgument(maxNumberOfDomains > 0 && maxNumberOfDomains <= 100, "must be between 1-100");
      formParameters.put("MaxNumberOfDomains", maxNumberOfDomains + "");
      return this;
   }

   public String getMaxNumberOfDomains() {
      return getFirstFormOrNull("MaxNumberOfDomains");
   }

   /**
    * 
    * @param nextToken
    *           tells Amazon SimpleDB where to start the next list of domain names.
    */
   public ListDomainsOptions nextToken(String nextToken) {
      checkNotNull(nextToken, "nextToken");
      formParameters.put("NextToken", nextToken);
      return this;
   }

   /**
    * 
    * @return String that tells Amazon SimpleDB where to start the next list of domain names.
    */
   public String getNextToken() {
      return getFirstFormOrNull("NextToken");
   }

   public static class Builder {

      /**
       * @see ListDomainsOptions#nextToken
       */
      public static ListDomainsOptions nextToken(String nextToken) {
         ListDomainsOptions options = new ListDomainsOptions();
         return options.nextToken(nextToken);
      }

      /**
       * @see ListDomainsOptions#maxNumberOfDomains
       */
      public static ListDomainsOptions maxNumberOfDomains(int maxNumberOfDomains) {
         ListDomainsOptions options = new ListDomainsOptions();
         return options.maxNumberOfDomains(maxNumberOfDomains);
      }

   }
}
