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
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.scriptbuilder.domain.Statements.interpret;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Creates a run script
 * 
 * @author Adrian Cole
 */
public class AppendFile implements Statement {
   public final static String MARKER = "END_OF_FILE";
   final String path;
   final Iterable<String> lines;

   public AppendFile(String path, Iterable<String> lines) {// TODO: convert so
      this.path = checkNotNull(path, "path");
      this.lines = checkNotNull(lines, "lines");
      checkState(Iterables.size(lines) > 0, "you must pass something to execute");
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
   public Iterable<String> functionDependecies(OsFamily family) {
      return Collections.emptyList();
   }

   @Override
   public String render(OsFamily family) {
      List<Statement> statements = Lists.newArrayList();
      if (family == OsFamily.UNIX) {
         StringBuilder builder = new StringBuilder();
         hereFile(path, builder);
         statements.add(interpret(builder.toString()));
      } else {
         for (String line : lines) {
            statements.add(appendToFile(line, path, family));
         }
      }
      return new StatementList(statements).render(family);
   }

   private void hereFile(String path, StringBuilder builder) {
      builder.append("cat >> ").append(path).append(" <<'").append(MARKER).append("'\n");
      for (String line : lines) {
         builder.append(line).append("\n");
      }
      builder.append(MARKER).append("\n");
   }

   private Statement appendToFile(String line, String path, OsFamily family) {
      String quote = "";
      if (!ShellToken.VQ.to(family).equals("")) {
         quote = "'";
      } else {
         line = escapeVarTokens(line, family);
      }
      return interpret(addSpaceToEnsureWeDontAccidentallyRedirectFd(String.format("echo %s%s%s>>%s{lf}", quote, line,
               quote, path)));
   }

   public static final Pattern REDIRECT_FD_PATTERN = Pattern.compile(".*[0-2]>>.*");

   static String addSpaceToEnsureWeDontAccidentallyRedirectFd(String line) {
      return REDIRECT_FD_PATTERN.matcher(line).matches() ? line.replace(">>", " >>") : line;
   }

}