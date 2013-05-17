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
package org.jclouds.glesys.options;


/**
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_editaccount" />
 */
public class UpdateAccountOptions extends CreateAccountOptions {

   public static class Builder {
      /**
       * @see org.jclouds.glesys.options.UpdateAccountOptions#antispamLevel
       */
      public static UpdateAccountOptions antispamLevel(int antispamLevel) {
         return UpdateAccountOptions.class.cast(new UpdateAccountOptions().antispamLevel(antispamLevel));
      }

      /**
       * @see org.jclouds.glesys.options.UpdateAccountOptions#antiVirus
       */
      public static UpdateAccountOptions antiVirus(boolean antiVirus) {
         return UpdateAccountOptions.class.cast(new UpdateAccountOptions().antiVirus(antiVirus));
      }

      /**
       * @see org.jclouds.glesys.options.UpdateAccountOptions#autorespond
       */
      public static UpdateAccountOptions autorespond(boolean autorespond) {
         return UpdateAccountOptions.class.cast(new UpdateAccountOptions().autorespond(autorespond));
      }

      /**
       * @see org.jclouds.glesys.options.UpdateAccountOptions#autorespondSaveEmail
       */
      public static UpdateAccountOptions autorespondSaveEmail(boolean autorespondSaveEmail) {
         return UpdateAccountOptions.class.cast(new UpdateAccountOptions().autorespondSaveEmail(autorespondSaveEmail));
      }

      /**
       * @see org.jclouds.glesys.options.UpdateAccountOptions#autorespondMessage
       */
      public static UpdateAccountOptions autorespondMessage(String autorespondMessage) {
         return UpdateAccountOptions.class.cast(new UpdateAccountOptions().autorespondMessage(autorespondMessage));
      }

      /**
       * @see org.jclouds.glesys.options.UpdateAccountOptions#password
       */
      public static UpdateAccountOptions password(String password) {
         return new UpdateAccountOptions().password(password);
      }
   }

   /** Reset the password for this account */
   public UpdateAccountOptions password(String password) {
      formParameters.put("password", password);
      return this;
   }
}
