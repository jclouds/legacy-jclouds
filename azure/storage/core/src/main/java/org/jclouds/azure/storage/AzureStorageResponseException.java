package org.jclouds.azure.storage;

import org.jclouds.azure.storage.domain.AzureStorageError;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;

/**
 * Encapsulates an Error from Azure Storage Services.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/UsingRESTError.html" />
 * @see AzureStorageError
 * @see org.jclouds.aws.handlers.ParseAzureStorageErrorFromXmlContent
 * @author Adrian Cole
 * 
 */
public class AzureStorageResponseException extends HttpResponseException {

   private static final long serialVersionUID = 1L;

   private AzureStorageError error = new AzureStorageError();

   public AzureStorageResponseException(HttpCommand command, HttpResponse response, AzureStorageError error) {
      super(String.format("command %s failed with code %s, error: %s", command.toString(), response
               .getStatusCode(), error.toString()), command, response);
      this.setError(error);

   }

   public AzureStorageResponseException(HttpCommand command, HttpResponse response, AzureStorageError error,
            Throwable cause) {
      super(String.format("command %1$s failed with error: %2$s", command.toString(), error
               .toString()), command, response, cause);
      this.setError(error);

   }

   public AzureStorageResponseException(String message, HttpCommand command, HttpResponse response,
            AzureStorageError error) {
      super(message, command, response);
      this.setError(error);

   }

   public AzureStorageResponseException(String message, HttpCommand command, HttpResponse response,
            AzureStorageError error, Throwable cause) {
      super(message, command, response, cause);
      this.setError(error);

   }

   public void setError(AzureStorageError error) {
      this.error = error;
   }

   public AzureStorageError getError() {
      return error;
   }

}
