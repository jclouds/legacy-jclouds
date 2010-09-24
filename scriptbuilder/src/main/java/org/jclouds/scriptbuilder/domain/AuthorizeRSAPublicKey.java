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

package org.jclouds.scriptbuilder.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.scriptbuilder.domain.Statements.appendFile;
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import java.util.Collections;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Adrian Cole
 */
public class AuthorizeRSAPublicKey implements Statement {

   private final String publicKey;

   public AuthorizeRSAPublicKey(String publicKey) {
      this.publicKey = checkNotNull(publicKey, "publicKey");
   }

   @Override
   public Iterable<String> functionDependecies(OsFamily family) {
      return Collections.emptyList();
   }

   @Override
   public String render(OsFamily family) {
      checkNotNull(family, "family");
      if (family == OsFamily.WINDOWS)
         throw new UnsupportedOperationException("windows not yet implemented");
      return new StatementList(ImmutableList.of(exec("{md} .ssh"), appendFile(".ssh/authorized_keys", Splitter.on('\n')
               .split(publicKey)), exec("chmod 600 .ssh/authorized_keys"))).render(family);
   }
}