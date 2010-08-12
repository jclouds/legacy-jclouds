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

import org.jclouds.scriptbuilder.util.Utils;

import com.google.common.base.CaseFormat;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Creates a run script
 * 
 * @author Adrian Cole
 */
public class CreateRunScript implements Statement {
   public final static String MARKER = "END_OF_SCRIPT";
   final String instanceName;
   final Iterable<String> exports;
   final String pwd;
   final String[] execLines;

   public CreateRunScript(String instanceName, Iterable<String> exports, String pwd,
            String... execLines) {// TODO: convert so
      // that
      // createRunScript
      // can take from a
      // variable
      this.instanceName = checkNotNull(instanceName, "instanceName");
      this.exports = checkNotNull(exports, "exports");
      this.pwd = checkNotNull(pwd, "pwd").replaceAll("[/\\\\]", "{fs}");
      this.execLines = checkNotNull(execLines, "execLines");
      checkState(execLines.length > 0, "you must pass something to execute");
   }

   public static class AddTitleToFile implements Statement {
      final String title;
      final String file;

      public AddTitleToFile(String title, String file) {
         this.title = checkNotNull(title, "title");
         this.file = checkNotNull(file, "file");
      }

      public static final Map<OsFamily, String> OS_TO_TITLE_PATTERN = ImmutableMap.of(
               OsFamily.UNIX,
               "echo \"PROMPT_COMMAND='echo -ne \\\"\\033]0;{title}\\007\\\"'\">>{file}\n",
               OsFamily.WINDOWS, "echo title {title}>>{file}\r\n");

      @Override
      public Iterable<String> functionDependecies(OsFamily family) {
         return Collections.emptyList();
      }

      @Override
      public String render(OsFamily family) {
         return addSpaceToEnsureWeDontAccidentallyRedirectFd(Utils.replaceTokens(
                  OS_TO_TITLE_PATTERN.get(family), ImmutableMap.of("title", title, "file", file)));
      }
   }

   public static class AddExportToFile implements Statement {
      final String export;
      final String value;
      final String file;

      public AddExportToFile(String export, String value, String file) {
         this.export = checkNotNull(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, export),
                  "export");
         this.value = checkNotNull(value, "value");
         this.file = checkNotNull(file, "file");
      }

      public static final Map<OsFamily, String> OS_TO_EXPORT_PATTERN = ImmutableMap.of(
               OsFamily.UNIX, "echo \"export {export}='{value}'\">>{file}\n", OsFamily.WINDOWS,
               "echo set {export}={value}>>{file}\r\n");

      @Override
      public Iterable<String> functionDependecies(OsFamily family) {
         return Collections.emptyList();
      }

      @Override
      public String render(OsFamily family) {
         return addSpaceToEnsureWeDontAccidentallyRedirectFd(Utils.replaceTokens(
                  OS_TO_EXPORT_PATTERN.get(family), ImmutableMap.of("export", export, "value",
                           value, "file", file)));
      }
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

   public static final Map<OsFamily, String> OS_TO_CHMOD_PATTERN = ImmutableMap.of(OsFamily.UNIX,
            "chmod u+x {file}\n", OsFamily.WINDOWS, "");

   @Override
   public String render(OsFamily family) {
      List<Statement> statements = Lists.newArrayList();
      Map<String, String> tokenMap = ShellToken.tokenValueMap(family);
      String runScript = Utils.replaceTokens(pwd + "{fs}" + instanceName + ".{sh}", tokenMap);
      statements.add(interpret(String.format("{md} %s{lf}", pwd)));
      if (family == OsFamily.UNIX) {
         StringBuilder builder = new StringBuilder();
         builder.append("\n");
         addUnixRunScriptHeader(family, runScript, builder);
         builder.append("\n");
         addUnixRunScript(runScript, builder);
         builder.append("\n");
         addUnixRunScriptFooter(family, runScript, builder);
         builder.append("\n");
         statements.add(interpret(builder.toString()));
      } else {
         statements.add(interpret(String.format("{rm} %s 2{closeFd}{lf}", runScript)));
         for (String line : Splitter.on(ShellToken.LF.to(family)).split(
                  ShellToken.BEGIN_SCRIPT.to(family))) {
            if (!line.equals(""))
               statements.add(appendToFile(line, runScript, family));
         }
         statements.add(new AddTitleToFile(instanceName, runScript));
         statements.add(appendToFile(Utils.writeZeroPath(family).replace(ShellToken.LF.to(family),
                  ""), runScript, family));
         statements.add(new AddExportToFile("instanceName", instanceName, runScript));
         for (String export : exports) {
            statements.add(new AddExportToFile(export, Utils.replaceTokens("{varl}"
                     + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, export) + "{varr}",
                     tokenMap), runScript));
         }
         statements.add(appendToFile("{cd} " + pwd, runScript, family));
         for (String execLine : execLines) {
            statements.add(appendToFile(execLine, runScript, family));
         }
         for (String line : Splitter.on(ShellToken.LF.to(family)).split(
                  ShellToken.END_SCRIPT.to(family))) {
            if (!line.equals(""))
               statements.add(appendToFile(line, runScript, family));
         }
      }
      statements.add(interpret(Utils.replaceTokens(OS_TO_CHMOD_PATTERN.get(family), ImmutableMap
               .of("file", runScript))));
      return new StatementList(statements).render(family);
   }

   private void addUnixRunScriptFooter(OsFamily family, String runScript, StringBuilder builder) {
      builder.append("# add runscript footer\n");
      builder.append("cat >> ").append(runScript).append(" <<'").append(MARKER).append("'\n");
      builder.append(ShellToken.END_SCRIPT.to(family));
      builder.append(MARKER).append("\n");
   }

   private void addUnixRunScript(String runScript, StringBuilder builder) {
      builder.append("# add desired commands from the user\n");
      builder.append("cat >> ").append(runScript).append(" <<'").append(MARKER).append("'\n");
      builder.append("cd ").append(pwd).append("\n");
      for (String execLine : execLines) {
         builder.append(execLine).append("\n");
      }
      builder.append(MARKER).append("\n");
   }

   private void addUnixRunScriptHeader(OsFamily family, String runScript, StringBuilder builder) {
      builder.append("# create runscript header\n");
      builder.append("cat > ").append(runScript).append(" <<").append(MARKER).append("\n");
      builder.append(ShellToken.BEGIN_SCRIPT.to(family));
      builder.append("PROMPT_COMMAND='echo -ne \"\\033]0;").append(instanceName).append(
               "\\007\"'\n");
      builder.append(Utils.writeZeroPath(family));
      builder.append("export INSTANCE_NAME='").append(instanceName).append("'\n");
      for (String export : exports) {
         String variableNameInUpper = CaseFormat.LOWER_CAMEL
                  .to(CaseFormat.UPPER_UNDERSCORE, export);
         builder.append("export ").append(variableNameInUpper).append("='$").append(
                  variableNameInUpper).append("'\n");
      }
      builder.append(MARKER).append("\n");
   }

   private Statement appendToFile(String line, String runScript, OsFamily family) {
      String quote = "";
      if (!ShellToken.VQ.to(family).equals("")) {
         quote = "'";
      } else {
         line = escapeVarTokens(line, family);
      }
      return interpret(addSpaceToEnsureWeDontAccidentallyRedirectFd(String.format(
               "echo %s%s%s>>%s{lf}", quote, line, quote, runScript)));
   }

   public static final Pattern REDIRECT_FD_PATTERN = Pattern.compile(".*[0-2]>>.*");

   static String addSpaceToEnsureWeDontAccidentallyRedirectFd(String line) {
      return REDIRECT_FD_PATTERN.matcher(line).matches() ? line.replace(">>", " >>") : line;
   }

}