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
package org.jclouds.glesys.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.glesys.domain.EmailAccount;
import org.jclouds.glesys.domain.EmailAlias;
import org.jclouds.glesys.domain.EmailOverview;
import org.jclouds.glesys.options.CreateAccountOptions;
import org.jclouds.glesys.options.EditAccountOptions;

/**
 * Provides synchronous access to E-Mail requests.
 * <p/>
 *
 * @author Adam Lowe
 * @see org.jclouds.glesys.features.EmailAsyncClient
 * @see <a href="https://customer.glesys.com/api.php" />
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface EmailClient {

   /**
    * Get a summary of e-mail accounts associated with this Glesys account
    *
    * @return the relevant summary data
    */
   EmailOverview getEmailOverview();

   /**
    * Get the set of detailed information about e-mail accounts
    *
    * @return the relevant set of details
    */
   Set<EmailAccount> listAccounts(String domain);

   /**
    * Get the set of details about e-mail aliases
    *
    * @return the relevant set of details
    */
   Set<EmailAlias> listAliases(String domain);

   /**
    * Create a new e-mail account
    *
    * @param accountAddress the e-mail address to use (the domain should already exist)
    * @param password       the password to use for the mailbox
    * @param options        optional parameters
    * @see DomainClient#addDomain
    */
   EmailAccount createAccount(String accountAddress, String password, CreateAccountOptions... options);

   /**
    * Create an e-mail alias for an e-mail account
    *
    * @param aliasAddress   the address to use for the alias  (the domain should already exist)
    * @param toEmailAddress the existing e-mail account address the alias should forward to
    * @see DomainClient#addDomain
    */
   EmailAlias createAlias(String aliasAddress, String toEmailAddress);

   /**
    * Adjust an e-mail account's settings
    *
    * @param accountAddress the existing e-mail account address
    * @param options        optional parameters
    */
   EmailAccount editAccount(String accountAddress, EditAccountOptions... options);

   /**
    * Adjust (re-target) an e-mail alias
    *
    * @param aliasAddress   the existing alias e-mail address
    * @param toEmailAddress the existing e-mail account address the alias should forward to
    */
   EmailAlias editAlias(String aliasAddress, String toEmailAddress);

   /**
    * Delete an e-mail account or alias
    *
    * @param accountAddress the existing alias e-mail account or alias address
    */
   boolean delete(String accountAddress);

}