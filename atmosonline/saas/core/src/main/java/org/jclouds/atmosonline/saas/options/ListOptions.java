package org.jclouds.atmosonline.saas.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options used to control paginated results (aka list commands).
 * 
 * @author Adrian Cole
 */
public class ListOptions extends BaseHttpRequestOptions {
   public static final ListOptions NONE = new ListOptions();

   /**
    * specifies the position to resume listing
    * <p/>
    * note this is an opaque value and should not be interpreted.
    */
   public ListOptions token(String token) {
      this.headers.put("x-emc-token", checkNotNull(token, "x-emc-token"));
      return this;
   }

   public String getToken() {
      return getFirstHeaderOrNull("x-emc-token");
   }

   /**
    * theÊmaximumÊnumberÊofÊitemsÊ thatÊshouldÊbeÊreturned. IfÊthisÊisÊÊnotÊspecified,ÊthereÊisÊno
    * limit.
    */
   public ListOptions limit(int maxresults) {
      checkState(maxresults >= 0, "maxresults must be >= 0");
      checkState(maxresults <= 10000, "maxresults must be <= 5000");
      headers.put("x-emc-limit", Integer.toString(maxresults));
      return this;
   }

   public Integer getLimit() {
      String maxresults = getFirstHeaderOrNull("x-emc-limit");
      return (maxresults != null) ? new Integer(maxresults) : null;
   }

   public static class Builder {

      /**
       * @see ListOptions#token(String)
       */
      public static ListOptions token(String token) {
         ListOptions options = new ListOptions();
         return options.token(token);
      }

      /**
       * @see ListOptions#limit(int)
       */
      public static ListOptions limit(int maxKeys) {
         ListOptions options = new ListOptions();
         return options.limit(maxKeys);
      }

   }
}
