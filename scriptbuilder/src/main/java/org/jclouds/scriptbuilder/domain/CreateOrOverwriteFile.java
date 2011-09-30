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
package org.jclouds.scriptbuilder.domain;

import static org.jclouds.scriptbuilder.domain.Statements.interpret;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Creates a run script
 * 
 * @author Adrian Cole
 */
public class CreateOrOverwriteFile extends AppendFile {

   public CreateOrOverwriteFile(String path, Iterable<String> lines) {
       super(path, lines);
   }

   public CreateOrOverwriteFile(String path, Iterable<String> lines, String marker) {
       super(path, lines, marker);
   }

   @Override
   public String render(OsFamily family) {
      List<Statement> statements = Lists.newArrayList();
      if (family == OsFamily.UNIX) {
         StringBuilder builder = new StringBuilder();
         hereFile(path, builder);
         statements.add(interpret(builder.toString()));
      } else {
         // Windows:
         statements.add(interpret(String.format("copy /y CON %s{lf}", path))); // This clears the file
         for (String line : lines) {
            statements.add(appendToFile(line, path, family));
         }
      }
      return new StatementList(statements).render(family);
   }

   protected void hereFile(String path, StringBuilder builder) {
      builder.append("cat > ").append(path).append(" <<'").append(marker).append("'\n");
      for (String line : lines) {
         builder.append(line).append("\n");
      }
      builder.append(marker).append("\n");
   }

}