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
public class EditAccountOptions extends CreateAccountOptions {

   public static class Builder {
      /**
       * @see org.jclouds.glesys.options.EditAccountOptions#antispamLevel
       */
      public static EditAccountOptions antispamLevel(int antispamLevel) {
         return EditAccountOptions.class.cast(new EditAccountOptions().antispamLevel(antispamLevel));
      }

      /**
       * @see org.jclouds.glesys.options.EditAccountOptions#antiVirus
       */
      public static EditAccountOptions antiVirus(boolean antiVirus) {
         return EditAccountOptions.class.cast(new EditAccountOptions().antiVirus(antiVirus));
      }

      /**
       * @see org.jclouds.glesys.options.EditAccountOptions#autorespond
       */
      public static EditAccountOptions autorespond(boolean autorespond) {
         return EditAccountOptions.class.cast(new EditAccountOptions().autorespond(autorespond));
      }

      /**
       * @see org.jclouds.glesys.options.EditAccountOptions#autorespondSaveEmail
       */
      public static EditAccountOptions autorespondSaveEmail(boolean autorespondSaveEmail) {
         return EditAccountOptions.class.cast(new EditAccountOptions().autorespondSaveEmail(autorespondSaveEmail));
      }

      /**
       * @see org.jclouds.glesys.options.EditAccountOptions#autorespondMessage
       */
      public static EditAccountOptions autorespondMessage(String autorespondMessage) {
         return EditAccountOptions.class.cast(new EditAccountOptions().autorespondMessage(autorespondMessage));
      }

      /**
       * @see org.jclouds.glesys.options.EditAccountOptions#password
       */
      public static EditAccountOptions password(String password) {
         return new EditAccountOptions().password(password);
      }
   }

   /** Reset the password for this account */
   public EditAccountOptions password(String password) {
      formParameters.put("password", password);
      return this;
   }
}
