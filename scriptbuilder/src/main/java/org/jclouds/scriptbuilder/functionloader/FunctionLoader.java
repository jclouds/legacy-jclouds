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
package org.jclouds.scriptbuilder.functionloader;

import org.jclouds.scriptbuilder.domain.OsFamily;

/**
 * A function loader interface.
 */
public interface FunctionLoader {

  /**
   * Loads a function and returns it as {@link String}.
   * @param function The function name to load.
   * @param family   This operating system family of the function.
   * @return         The function as {@link String}.
   * @throws FunctionNotFoundException
   */
  public String loadFunction(String function, OsFamily family) throws FunctionNotFoundException;

}
