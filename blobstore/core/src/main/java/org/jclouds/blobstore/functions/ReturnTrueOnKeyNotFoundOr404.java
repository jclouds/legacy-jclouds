package org.jclouds.blobstore.functions;

import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.functions.ReturnTrueOn404;

import com.google.common.base.Function;

public class ReturnTrueOnKeyNotFoundOr404 implements Function<Exception, Boolean> {
   ReturnTrueOn404 rto404 = new ReturnTrueOn404();

   public Boolean apply(Exception from) {
      if (from instanceof KeyNotFoundException) {
         return true;
      } else {
         return rto404.apply(from);
      }

   }

}