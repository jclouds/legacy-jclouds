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

package org.jclouds.simpledb;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.jclouds.concurrent.Timeout;
import org.jclouds.simpledb.domain.Item;
import org.jclouds.simpledb.domain.ListDomainsResponse;
import org.jclouds.simpledb.options.ListDomainsOptions;

/**
 * Provides access to SimpleDB via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface SimpleDBClient {

   /**
    * The ListDomains operation lists all domains associated with the Access Key ID. It returns
    * domain names up to the limit set by MaxNumberOfDomains. A NextToken is returned if there are
    * more than MaxNumberOfDomains domains. Calling ListDomains successive times with the NextToken
    * returns up to MaxNumberOfDomains more domain names each time.
    * 
    * 
    * @param region
    *           Domains are Region-specific.
    * @param options
    *           specify result count or other options
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AmazonSimpleDB/2009-04-15/DeveloperGuide/SDB_API_ListDomains.html"
    *      />
    */
   ListDomainsResponse listDomainsInRegion(@Nullable String region, ListDomainsOptions... options);

   /**
    * The CreateDomain operation creates a new domain.
    * 
    * <p/>
    * The domain name must be unique among the domains associated with the Access Key ID provided in
    * the request. The CreateDomain operation might take 10 or more seconds to complete.
    * 
    * 
    * <h3>Note</h3>
    * CreateDomain is an idempotent operation; running it multiple times using the same domain name
    * will not result in an error response.
    * <p/>
    * You can create up to 100 domains per account.
    * <p/>
    * If you require additional domains, go to
    * http://amazon.com/contact-us/simpledb-limit-request/.
    * 
    * @param region
    *           Domains are Region-specific.
    * @param domainName
    *           The name of the domain to create. The name can range between 3 and 255 characters
    *           and can contain the following characters: a-z, A-Z, 0-9, '_', '-', and '.'.
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AmazonSimpleDB/2009-04-15/DeveloperGuide/SDB_API_ListDomains.html"
    *      />
    */
   void createDomainInRegion(@Nullable String region, String domainName);

   /**
    * The DeleteDomain operation deletes a domain. Any items (and their attributes) in the domain
    * are deleted as well. The DeleteDomain operation might take 10 or more seconds to complete. <h3>
    * Note</h3>
    * 
    * Running DeleteDomain on a domain that does not exist or running the function multiple times
    * using the same domain name will not result in an error response.
    * 
    * 
    * @param region
    *           Domains are Region-specific.
    * @param domainName
    *           The name of the domain to delete.
    * 
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AmazonSimpleDB/2009-04-15/DeveloperGuide/SDB_API_ListDomains.html"
    *      />
    */
   void deleteDomainInRegion(String region, String domainName);
   void putAttributes(String region, String domain, String itemName, Item attributes);
   Map<String, Item> select(String region, String selectionExpression);
}
