package org.jclouds.aws.ec2.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.rest.internal.GeneratedHttpRequest;

/**
 * 
 * @author Adrian Cole
 */
public class EC2Utils {
   public static void indexFormValuesWithPrefix(GeneratedHttpRequest<?> request, String prefix,
            Object input) {
      checkArgument(checkNotNull(input, "input") instanceof String[],
               "this binder is only valid for String[] : " + input.getClass());
      String[] values = (String[]) input;
      for (int i = 0; i < values.length; i++) {
         request.addFormParam(prefix + "." + (i + 1), checkNotNull(values[i], prefix
                  .toLowerCase()
                  + "s[" + i + "]"));
      }
   }
}