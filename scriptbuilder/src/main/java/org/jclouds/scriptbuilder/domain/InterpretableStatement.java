/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.scriptbuilder.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.scriptbuilder.util.Utils;

import com.google.common.collect.ImmutableList;

/**
 * Statement used in a shell script
 * 
 * @author Adrian Cole
 */
public class InterpretableStatement implements Statement {

   private String statement;

   public InterpretableStatement(String statement) {
      this.statement = checkNotNull(statement, "statement");
   }

   public String render(OsFamily family) {
      return Utils.replaceTokens(statement, ShellToken.tokenValueMap(family));
   }

   @Override
   public Iterable<String> functionDependecies() {
      return ImmutableList.of();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((statement == null) ? 0 : statement.hashCode());
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
      InterpretableStatement other = (InterpretableStatement) obj;
      if (statement == null) {
         if (other.statement != null)
            return false;
      } else if (!statement.equals(other.statement))
         return false;
      return true;
   }
}