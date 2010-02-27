package org.jclouds.gogrid;

import org.jclouds.gogrid.domain.internal.ErrorResponse;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;

import static java.lang.String.format;

/**
 * @author Oleksiy Yarmula
 */
public class GoGridResponseException extends HttpResponseException {

    private static final long serialVersionUID = 1924589L;

    private ErrorResponse error;

    public GoGridResponseException(HttpCommand command, HttpResponse response, ErrorResponse error) {
        super(format("command %s failed with code %s, error [%s]: %s",
                    command.toString(), response.getStatusCode(), error.getErrorCode(),
                    error.getMessage()),
              command, response);
        this.setError(error);
    }


    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }
}
