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
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.scriptbuilder.domain.Statements.interpret;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * Creates a run script
 * 
 * @author Adrian Cole
 */
public class AppendFile implements Statement {
   public static final String DELIMITER = "END_OF_JCLOUDS_FILE";

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected String path;
      protected Iterable<String> lines = ImmutableSet.of();
      protected String delimiter = DELIMITER;
      protected boolean expandVariables;

      /**
       * @see AppendFile#getPath()
       */
      public Builder path(String path) {
         this.path = path;
         return this;
      }

      /**
       * @see AppendFile#getLines()
       */
      public Builder lines(Iterable<String> lines) {
         this.lines = ImmutableList.copyOf(lines);
         return this;
      }

      /**
       * @see AppendFile#getDelimiter()
       */
      public Builder delimiter(String delimiter) {
         this.delimiter = delimiter;
         return this;
      }

      /**
       * @see AppendFile#shouldExpandVariables()
       */
      public Builder expandVariables(boolean expandVariables) {
         this.expandVariables = expandVariables;
         return this;
      }

      public AppendFile build() {
         return new AppendFile(path, lines, delimiter, expandVariables);
      }
   }

   protected final String path;
   protected final Iterable<String> lines;
   protected final String delimiter;
   protected final boolean expandVariables;

   protected AppendFile(String path, Iterable<String> lines, String delimiter, boolean expandVariables) {
      this.path = checkNotNull(path, "PATH");
      this.lines = checkNotNull(lines, "lines");
      this.delimiter = checkNotNull(delimiter, "delimiter");
      checkState(Iterables.size(lines) > 0, "you must pass something to execute");
      this.expandVariables = expandVariables;
   }

   public static String escapeVarTokens(String toEscape, OsFamily family) {
      Map<String, String> inputToEscape = Maps.newHashMap();
      for (ShellToken token : ImmutableList.of(ShellToken.VARL, ShellToken.VARR)) {
         if (!token.to(family).equals("")) {
            String tokenS = "{" + token.toString().toLowerCase() + "}";
            inputToEscape.put(tokenS, "{escvar}" + tokenS);
         }
      }
      for (Entry<String, String> entry : inputToEscape.entrySet()) {
         toEscape = toEscape.replace(entry.getKey(), entry.getValue());
      }
      return toEscape;
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of();
   }

   @Override
   public String render(OsFamily family) {
      if (family == OsFamily.UNIX) {
         return interpret(hereFile()).render(family);
      } else {
         return interpret(appendToWindowsFile()).render(family);
      }
   }

   protected String appendToWindowsFile() {
      StringBuilder builder = new StringBuilder();
      for (String line : lines) {
         builder.append(appendLineToWindowsFile(line, path));
      }
      return builder.toString();
   }

   protected String hereFile() {
      StringBuilder hereFile = startHereFile();
      for (String line : lines) {
         hereFile.append('\t').append(line).append("\n");
      }
      hereFile.append(delimiter).append("\n");
      return hereFile.toString();
   }

   public StringBuilder startHereFile() {
      StringBuilder hereFile = new StringBuilder().append("cat >> ").append(path);
      if (expandVariables)
         return hereFile.append(" <<-").append(delimiter).append("\n");
      return hereFile.append(" <<-'").append(delimiter).append("'\n");
   }

   protected String appendLineToWindowsFile(String line, String path) {
      String quote = "";
      if (!ShellToken.VQ.to(OsFamily.WINDOWS).equals("")) {
         quote = "'";
      } else {
         line = escapeVarTokens(line, OsFamily.WINDOWS);
      }
      return String.format("echo %s%s%s >>%s{lf}", quote, line, quote, path);
   }

}
