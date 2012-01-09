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
