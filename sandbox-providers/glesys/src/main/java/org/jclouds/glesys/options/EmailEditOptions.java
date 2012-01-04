package org.jclouds.glesys.options;


/**
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_editaccount" />
 */
public class EmailEditOptions extends EmailCreateOptions {

   public static class Builder {
      public static EmailEditOptions antispamLevel(int antispamLevel) {
         return EmailEditOptions.class.cast(new EmailEditOptions().antispamLevel(antispamLevel));
      }

      public static EmailEditOptions antiVirus(boolean antiVirus) {
         return EmailEditOptions.class.cast(new EmailEditOptions().antiVirus(antiVirus));
      }

      public static EmailEditOptions autorespond(boolean autorespond) {
         return EmailEditOptions.class.cast(new EmailEditOptions().autorespond(autorespond));
      }

      public static EmailEditOptions autorespondSaveEmail(boolean autorespondSaveEmail) {
         return EmailEditOptions.class.cast(new EmailEditOptions().autorespondSaveEmail(autorespondSaveEmail));
      }

      public static EmailEditOptions autorespondMessage(boolean autorespondMessage) {
         return EmailEditOptions.class.cast(new EmailEditOptions().autorespondMessage(autorespondMessage));
      }

      public static EmailEditOptions autorespondMessage(String password) {
         return new EmailEditOptions().password(password);
      }
   }

   public EmailEditOptions password(String password) {
      formParameters.put("password", password);
      return this;
   }
}
