package org.jclouds.date;

import org.jclouds.date.internal.SimpleDateCodecFactory;

import com.google.inject.ImplementedBy;


/**
 * Codecs for converting from Date->String and vice versa.
 * 
 * @author aled
 */
@ImplementedBy(SimpleDateCodecFactory.class)
public interface DateCodecFactory {

   public DateCodec rfc1123();
}
