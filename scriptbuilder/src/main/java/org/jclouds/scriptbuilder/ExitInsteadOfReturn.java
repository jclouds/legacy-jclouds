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

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.collect.ForwardingObject;

/**
 * you cannot return from a top-level script, so if you are using snippets that
 * issue {@code return} then you'll want to wrap them in this.
 * 
 * @author Adrian Cole
 * 
 */
public class ExitInsteadOfReturn extends ForwardingObject implements Statement {
   private final Statement delegate;

   public ExitInsteadOfReturn(Statement delegate) {
      this.delegate = delegate;
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return delegate().functionDependencies(family);
   }

   @Override
   public String render(OsFamily family) {
      return delegate().render(family).toString().replaceAll(ShellToken.RETURN.to(family), ShellToken.EXIT.to(family));
   }

   @Override
   protected Statement delegate() {
      return delegate;
   }
}
