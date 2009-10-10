package org.jclouds.http.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.TeeInputStream;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.http.HttpConstants;
import org.jclouds.logging.Logger;

/**
 * Logs data to the wire LOG.
 * 
 * @author Adrian Cole
 * @see org.apache.http.impl.conn.Wire
 */
public class Wire {

   @Resource
   @Named(HttpConstants.HTTP_WIRE_LOGGER)
   protected Logger wireLog = Logger.NULL;
   @Resource
   protected Logger logger = Logger.NULL;

   private final ExecutorService exec;

   @Inject
   public Wire(ExecutorService exec) {
      this.exec = checkNotNull(exec, "executor");
   }

   private void wire(String header, InputStream instream) {
      StringBuilder buffer = new StringBuilder();
      int ch;
      try {
         while ((ch = instream.read()) != -1) {
            if (ch == 13) {
               buffer.append("[\\r]");
            } else if (ch == 10) {
               buffer.append("[\\n]\"");
               buffer.insert(0, "\"");
               buffer.insert(0, header);
               wireLog.debug(buffer.toString());
               buffer.setLength(0);
            } else if ((ch < 32) || (ch > 127)) {
               buffer.append("[0x");
               buffer.append(Integer.toHexString(ch));
               buffer.append("]");
            } else {
               buffer.append((char) ch);
            }
         }
         if (buffer.length() > 0) {
            buffer.append('\"');
            buffer.insert(0, '\"');
            buffer.insert(0, header);
            wireLog.debug(buffer.toString());
         }
      } catch (IOException e) {
         logger.error(e, "Error tapping line");
      }
   }

   public boolean enabled() {
      return wireLog.isDebugEnabled();
   }

   public InputStream copy(final String header, InputStream instream) {
      try {
         byte[] data = IOUtils.toByteArray(instream);
         wire(header, new ByteArrayInputStream(data));
         return new ByteArrayInputStream(data);
      } catch (IOException e) {
         throw new RuntimeException("Error tapping line", e);
      } finally {
         IOUtils.closeQuietly(instream);
      }
   }

   public InputStream tapAsynch(final String header, InputStream instream) {
      PipedOutputStream out = new PipedOutputStream();
      InputStream toReturn = new TeeInputStream(instream, out, true);
      final InputStream line;
      try {
         line = new PipedInputStream(out);
         exec.submit(new Runnable() {
            public void run() {
               try {
                  wire(header, line);
               } finally {
                  IOUtils.closeQuietly(line);
               }
            }
         });
      } catch (IOException e) {
         logger.error(e, "Error tapping line");
      }
      return toReturn;
   }

   public InputStream input(InputStream instream) {
      return copy("<< ", checkNotNull(instream, "input"));
   }

   @SuppressWarnings("unchecked")
   public <T> T output(T data) {
      checkNotNull(data, "data must be set before calling generateETag()");
      if (data instanceof InputStream) {
         if (exec.getClass().isAnnotationPresent(SingleThreaded.class))
            return (T) copy(">> ", (InputStream) data);
         else
            return (T) tapAsynch(">> ", (InputStream) data);
      } else if (data instanceof byte[]) {
         output((byte[]) data);
         return data;
      } else if (data instanceof String) {
         output((String) data);
         return data;
      } else if (data instanceof File) {
         output(((File) data));
         return data;
      } else {
         throw new UnsupportedOperationException("Content not supported " + data.getClass());
      }
   }

   private void output(final File out) {
      checkNotNull(out, "output");
      exec.submit(new Runnable() {
         public void run() {
            InputStream in = null;
            try {
               in = new FileInputStream(out);
               wire(">> ", in);
            } catch (FileNotFoundException e) {
               logger.error(e, "Error tapping file: %s", out);
            } finally {
               IOUtils.closeQuietly(in);
            }
         }
      });
   }

   private void output(byte[] b) {
      wire(">> ", new ByteArrayInputStream(checkNotNull(b, "output")));
   }

   private void output(final String s) {
      output(checkNotNull(s, "output").getBytes());
   }

}
