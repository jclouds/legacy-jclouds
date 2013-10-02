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
package org.jclouds.scriptbuilder.functionloader.filters;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.CharStreams.newReaderSupplier;
import static com.google.common.io.CharStreams.readLines;

import java.io.IOException;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.scriptbuilder.functionloader.FunctionLoader;
import org.jclouds.scriptbuilder.functionloader.FunctionNotFoundException;

import com.google.common.io.LineProcessor;

/**
 * FunctionLoader decorator to filters out license header comments from a file.
 * 
 * @author Ignasi Barrera
 */
public class LicenseHeaderFilter implements FunctionLoader {

   /** The target function loader. */
   private final FunctionLoader delegate;

   public LicenseHeaderFilter(FunctionLoader delegate) {
      this.delegate = checkNotNull(delegate, "delegate");
   }

   @Override
   public String loadFunction(String function, OsFamily family) throws FunctionNotFoundException {
      return filter(delegate.loadFunction(function, family), family);
   }

   /**
    * Filters out license header comments from a file.
    * @param lines The lines to filter.
    * @param family The {@link OsFamily} of the file.
    * @return The filtered file.
    */
   public String filter(String lines, OsFamily family) {
      try {
         return readLines(newReaderSupplier(checkNotNull(lines, "lines")),
               new LicenseHeaderProcessor(checkNotNull(family, "family")));
      } catch (IOException e) {
         // Don't fail; just return the original text with the comments
         return lines;
      }
   }

   /**
    * Line by line processor.
    *
    * @author Ignasi Barrera
    */
   private static class LicenseHeaderProcessor implements LineProcessor<String> {

      /** Search token used to identify license header comments beginning. */
      private static final String LICENSE_TOKEN_START = "Licensed to the Apache Software Foundation";
      
      /** Search token used to identify license header comments end. */
      private static final String LICENSE_TOKEN_END = "limitations under the License";

      /** The resulting text, without the license header comments. */
      private final StringBuilder builder;

      /** The comment token for the given OsFamily */
      private final String commentToken;
      
      /** The line termination token for the given OsFamily */
      private final String lineTerminationToken;

      /** Flag to indicate that a license header comment is being processed. */
      private boolean isLicenseComment;

      public LicenseHeaderProcessor(OsFamily family) {
         builder = new StringBuilder();
         commentToken = ShellToken.REM.to(family);
         lineTerminationToken = ShellToken.LF.to(family);
         isLicenseComment = false;
      }

      @Override
      public boolean processLine(String line) throws IOException {
         String trimmed = line.trim();
         boolean isComment = isCommentLine(trimmed);

         if (isComment && line.contains(LICENSE_TOKEN_START)) {
            // License found, start skipping following comment lines
            isLicenseComment = true;
         }

         // Remove empty comment lines (licenses are prefixed and suffixed with
         // empty comments)
         if (!isLicenseComment && !isEmptyCommentLine(trimmed)) {
            builder.append(line);
            builder.append(lineTerminationToken);
         }
         
         if (isLicenseComment && line.contains(LICENSE_TOKEN_END)) {
            // Reset the license flag to avoid filtering other comments
            isLicenseComment = false;
         }

         return true; // Continue processing lines
      }

      @Override
      public String getResult() {
         return builder.toString();
      }

      /**
       * Checks if the given line is an empty comment line.
       */
      private boolean isEmptyCommentLine(String line) {
         // ShellToken.REM.to(WINDOWS) has a '@' prefix but not all comments do
         return line.equals(commentToken) || line.equals("REM");
      }

      /**
       * Checks if the given line is a comment line.
       */
      private boolean isCommentLine(String line) {
         // ShellToken.REM.to(WINDOWS) has a '@' prefix but not all comments do
         return line.startsWith(commentToken) || line.startsWith("REM");
      }
   }

}
