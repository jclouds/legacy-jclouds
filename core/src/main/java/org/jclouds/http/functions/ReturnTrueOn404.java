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
package org.jclouds.http.functions;

import static com.google.common.base.Predicates.equalTo;
import static org.jclouds.http.HttpUtils.returnValueOnCodeOrNull;
import static org.jclouds.util.Throwables2.propagateOrNull;

import javax.inject.Singleton;

import com.google.common.base.Function;

@Singleton
public class ReturnTrueOn404 implements Function<Exception, Boolean> {

   public Boolean apply(Exception from) {
      Boolean returnVal = returnValueOnCodeOrNull(from, true, equalTo(404));
      return returnVal != null ? returnVal : Boolean.class.cast(propagateOrNull(from));
   }

}