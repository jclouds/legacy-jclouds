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
package org.jclouds.scriptbuilder.statements.ssh;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.jclouds.scriptbuilder.domain.Statements.newStatementList;

import java.util.Map;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

/**
 * Control sshd_config
 * 
 * @author Adrian Cole
 */
public class SshdConfig implements Statement {
   private static final String sshdConfig = "/etc/ssh/sshd_config";

   private final Map<String, String> params;

   public SshdConfig(Map<String, String> params) {
      this.params = checkNotNull(params, "params");
   }

   public String render(OsFamily family) {
      String linesToPrepend = Joiner.on('\n').withKeyValueSeparator(" ").join(params);
      Statement prependSshdConfig = exec(String.format(
               "exec 3<> %1$s && awk -v TEXT=\"%2$s\n\" 'BEGIN {print TEXT}{print}' %1$s >&3", sshdConfig,
               linesToPrepend));
      Statement reloadSshdConfig = exec("hash service 2>&- && service ssh reload 2>&- || /etc/init.d/ssh* reload");
      return newStatementList(prependSshdConfig, reloadSshdConfig).render(family);
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of();
   }
}
