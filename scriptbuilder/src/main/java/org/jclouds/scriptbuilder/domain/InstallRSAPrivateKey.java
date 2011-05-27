/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

/**
 * @see org.jclouds.scriptbuilder.statements.ssh.InstallRSAPrivateKey
 * @author Adrian Cole
 */
@Deprecated
public class InstallRSAPrivateKey extends org.jclouds.scriptbuilder.statements.ssh.InstallRSAPrivateKey {

   public InstallRSAPrivateKey(String privateKey) {
      super(privateKey);
   }

}