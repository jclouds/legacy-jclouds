/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ssh.jsch.config;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

import org.jclouds.ssh.ConfiguresSshClient;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.JschSshClient;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresSshClient
public class JschSshClientModule extends AbstractModule {

   protected void configure() {
      bind(SshClient.Factory.class).to(Factory.class).in(Scopes.SINGLETON);
   }

   private static class Factory implements SshClient.Factory {

      public SshClient create(InetSocketAddress socket, String username, String password) {
         return new JschSshClient(socket, username, password);
      }

      public SshClient create(InetSocketAddress socket, String username, byte[] privateKey) {
         return new JschSshClient(socket, username, privateKey);
      }

      @Override
      public Map<String, String> generateRSAKeyPair(String comment, String passphrase) {
         KeyPair pair = null;
         try {
            pair = KeyPair.genKeyPair(new JSch(), KeyPair.RSA);
         } catch (JSchException e) {
            Throwables.propagate(e);
         }
         if (passphrase != null)
            pair.setPassphrase(passphrase);
         ByteArrayOutputStream privateKey = new ByteArrayOutputStream();
         pair.writePrivateKey(privateKey);
         ByteArrayOutputStream publicKey = new ByteArrayOutputStream();
         pair.writePublicKey(publicKey, comment);
         return ImmutableMap.of("comment", comment, "passphrase", passphrase, "private",
                  new String(privateKey.toByteArray()), "public", new String(publicKey
                           .toByteArray()));
      }
   }
}