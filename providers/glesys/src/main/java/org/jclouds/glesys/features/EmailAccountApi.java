/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.glesys.features;

import org.jclouds.glesys.domain.EmailAccount;
import org.jclouds.glesys.domain.EmailAlias;
import org.jclouds.glesys.domain.EmailOverview;
import org.jclouds.glesys.options.CreateAccountOptions;
import org.jclouds.glesys.options.UpdateAccountOptions;

import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to E-Mail requests.
 * <p/>
 *
 * @author Adam Lowe
 * @see org.jclouds.glesys.features.EmailAccountAsyncApi
 * @see <a href="https://github.com/GleSYS/API/wiki/API-Documentation" />
 */
public interface EmailAccountApi {

   /**
    * Get a summary of e-mail accounts associated with this Glesys account
    *
    * @return the relevant summary data
    */
   EmailOverview getOverview();

   /**
    * Get the set of detailed information about e-mail accounts
    *
    * @return the relevant set of details
    */
   FluentIterable<EmailAccount> listDomain(String domain);

   /**
    * Get the set of details about e-mail aliases
    *
    * @return the relevant set of details
    */
   FluentIterable<EmailAlias> listAliasesInDomain(String domain);

   /**
    * Create a new e-mail account
    *
    * @param accountAddress the e-mail address to use (the domain should already exist)
    * @param password       the password to use for the mailbox
    * @param options        optional parameters
    * @see DomainApi#create
    */
   EmailAccount createWithPassword(String accountAddress, String password, CreateAccountOptions... options);

   /**
    * Create an e-mail alias for an e-mail account
    *
    * @param aliasAddress   the address to use for the alias  (the domain should already exist)
    * @param toEmailAddress the existing e-mail account address the alias should forward to
    * @see DomainApi#create
    */
   EmailAlias createAlias(String aliasAddress, String toEmailAddress);

   /**
    * Adjust an e-mail account's settings
    *
    * @param accountAddress the existing e-mail account address
    * @param options        optional parameters
    */
   EmailAccount update(String accountAddress, UpdateAccountOptions... options);

   /**
    * Adjust (re-target) an e-mail alias
    *
    * @param aliasAddress   the existing alias e-mail address
    * @param toEmailAddress the existing e-mail account address the alias should forward to
    */
   EmailAlias updateAlias(String aliasAddress, String toEmailAddress);

   /**
    * Delete an e-mail account or alias
    *
    * @param accountAddress the existing alias e-mail account or alias address
    */
   boolean delete(String accountAddress);

}
