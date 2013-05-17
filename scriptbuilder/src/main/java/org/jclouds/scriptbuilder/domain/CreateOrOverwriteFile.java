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


/**
 * Creates a run script
 * 
 * @author Adrian Cole
 */
public class CreateOrOverwriteFile extends AppendFile {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder extends AppendFile.Builder {

      @Override
      public Builder path(String path) {
         return Builder.class.cast(super.path(path));
      }

      @Override
      public Builder lines(Iterable<String> lines) {
         return Builder.class.cast(super.lines(lines));
      }

      @Override
      public Builder delimiter(String delimiter) {
         return Builder.class.cast(super.delimiter(delimiter));
      }

      @Override
      public Builder expandVariables(boolean expandVariables) {
         return Builder.class.cast(super.expandVariables(expandVariables));
      }

      @Override
      public CreateOrOverwriteFile build() {
         return new CreateOrOverwriteFile(path, lines, delimiter, expandVariables);
      }

   }

   protected CreateOrOverwriteFile(String path, Iterable<String> lines, String delimiter, boolean expandVariables) {
      super(path, lines, delimiter, expandVariables);
   }

   @Override
   protected String appendToWindowsFile() {
      return String.format("copy /y CON %s{lf}", path) + super.appendToWindowsFile();
   }

   @Override
   public StringBuilder startHereFile() {
      StringBuilder hereFile = new StringBuilder().append("cat > ").append(path);
      if (expandVariables)
         return hereFile.append(" <<-").append(delimiter).append("\n");
      return hereFile.append(" <<-'").append(delimiter).append("'\n");
   }

}
