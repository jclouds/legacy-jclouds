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
package org.jclouds.glesys.options;


/**
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_editaccount" />
 */
public class EmailEditOptions extends EmailCreateOptions {

   public static class Builder {
      /**
       * @see org.jclouds.glesys.options.EmailEditOptions#antispamLevel
       */
      public static EmailEditOptions antispamLevel(int antispamLevel) {
         return EmailEditOptions.class.cast(new EmailEditOptions().antispamLevel(antispamLevel));
      }

      /**
       * @see org.jclouds.glesys.options.EmailEditOptions#antiVirus
       */
      public static EmailEditOptions antiVirus(boolean antiVirus) {
         return EmailEditOptions.class.cast(new EmailEditOptions().antiVirus(antiVirus));
      }

      /**
       * @see org.jclouds.glesys.options.EmailEditOptions#autorespond
       */
      public static EmailEditOptions autorespond(boolean autorespond) {
         return EmailEditOptions.class.cast(new EmailEditOptions().autorespond(autorespond));
      }

      /**
       * @see org.jclouds.glesys.options.EmailEditOptions#autorespondSaveEmail
       */
      public static EmailEditOptions autorespondSaveEmail(boolean autorespondSaveEmail) {
         return EmailEditOptions.class.cast(new EmailEditOptions().autorespondSaveEmail(autorespondSaveEmail));
      }

      /**
       * @see org.jclouds.glesys.options.EmailEditOptions#autorespondMessage
       */
      public static EmailEditOptions autorespondMessage(String autorespondMessage) {
         return EmailEditOptions.class.cast(new EmailEditOptions().autorespondMessage(autorespondMessage));
      }

      /**
       * @see org.jclouds.glesys.options.EmailEditOptions#password
       */
      public static EmailEditOptions password(String password) {
         return new EmailEditOptions().password(password);
      }
   }

   /** Reset the password for this account */
   public EmailEditOptions password(String password) {
      formParameters.put("password", password);
      return this;
   }
}
