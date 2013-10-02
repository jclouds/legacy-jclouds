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
package org.jclouds.scriptbuilder;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.scriptbuilder.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Creates an environment file
 * 
 * @author Adrian Cole
 */
public class EnvBuilder {

   @VisibleForTesting
   Map<String, String> variables = Maps.newHashMap();

   /**
    * Exports a variable inside the script
    * 
    * @param name name of the variable in UPPER_UNDERSCORE case format
    */
   public EnvBuilder export(String name, String value) {
      variables.put(checkNotNull(name, "name"), checkNotNull(value, "value"));
      return this;
   }

   /**
    * builds the environment file, by adding the following
    * <ol>
    * <li>example usage</li>
    * <li>variable exports</li>
    * <li>return statement</li>
    * </ol>
    * 
    * @param osFamily
    *           whether to write a cmd or bash script.
    */
   public String build(final OsFamily osFamily) {
      StringBuilder builder = new StringBuilder();
      builder.append(Utils.writeComment(" Env file: please do not confuse people by making this executable", osFamily));
      builder.append(Utils.writeComment("", osFamily));
      builder.append(Utils.writeComment(" Example usage to set a variable", osFamily));
      builder.append(Utils.writeComment("", osFamily));
      builder.append(Utils.writeComment(" "
               + Utils.writeVariableExporters(ImmutableMap.of("MAVEN_OPTS", "-Xms64m -Xmx128m"),
                        osFamily), osFamily));
      builder.append(Utils.writeVariableExporters(variables, osFamily));
      builder.append(ShellToken.LF.to(osFamily));
      builder.append(Utils.writeComment(
               " Please retain this statement so that the script can be validated", osFamily));
      builder.append(ShellToken.RETURN.to(osFamily)).append(" 0")
               .append(ShellToken.LF.to(osFamily));
      return builder.toString();
   }
}
