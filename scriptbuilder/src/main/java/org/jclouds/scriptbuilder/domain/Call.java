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
package org.jclouds.scriptbuilder.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Map;

import org.jclouds.scriptbuilder.util.Utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Statement used in a shell script
 * 
 * @author Adrian Cole
 */
public class Call implements Statement {

   public static final Map<OsFamily, String> OS_TO_CALL = ImmutableMap.of(OsFamily.UNIX,
            "{function}{args} || return 1\n", OsFamily.WINDOWS,
            "call :{function}{args}\r\nif errorlevel 1 goto abort\r\n");

   private String function;
   private String[] args;

   public Call(String function, String... args) {
      this.function = checkNotNull(function, "function");
      this.args = checkNotNull(args, "args");
   }

   public String render(OsFamily family) {
      StringBuilder args = new StringBuilder();
      for (String arg : this.args) {
         args.append(" ").append(Utils.replaceTokens(arg, ShellToken.tokenValueMap(family)));
      }
      StringBuilder call = new StringBuilder();
      call.append(Utils.replaceTokens(OS_TO_CALL.get(family), ImmutableMap.of("function", function,
               "args", args.toString())));
      return call.toString();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(args);
      result = prime * result + ((function == null) ? 0 : function.hashCode());
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
      Call other = (Call) obj;
      if (!Arrays.equals(args, other.args))
         return false;
      if (function == null) {
         if (other.function != null)
            return false;
      } else if (!function.equals(other.function))
         return false;
      return true;
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of(function);
   }
}
