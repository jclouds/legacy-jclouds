package org.jclouds.aws.ec2.functions;

import java.lang.reflect.Constructor;

import javax.inject.Singleton;

import org.jclouds.aws.AWSResponseException;

import com.google.common.base.Function;

@Singleton
public class ReturnVoidOnGroupNotFound implements Function<Exception, Void> {

   static final Void v;
   static {
      Constructor<Void> cv;
      try {
         cv = Void.class.getDeclaredConstructor();
         cv.setAccessible(true);
         v = cv.newInstance();
      } catch (Exception e) {
         throw new Error("Error setting up class", e);
      }
   }

   public Void apply(Exception from) {
      if (from instanceof AWSResponseException) {
         if (((AWSResponseException) from).getError().getCode().equals("InvalidGroup.NotFound"))
            return v;
      }
      return null;
   }

}