package org.jclouds.gogrid.handlers;

import com.google.common.io.Closeables;
import com.google.inject.Inject;
import org.jclouds.gogrid.GoGridResponseException;
import org.jclouds.gogrid.domain.internal.ErrorResponse;
import org.jclouds.gogrid.functions.ParseErrorFromJsonResponse;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;

import java.io.InputStream;

/**
 * @author Oleksiy Yarmula
 */
public class GoGridErrorHandler implements HttpErrorHandler {

    private final ParseErrorFromJsonResponse errorParser;

    @Inject
    public GoGridErrorHandler(ParseErrorFromJsonResponse errorParser) {
        this.errorParser = errorParser;
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Override
    public void handleError(HttpCommand command, HttpResponse response) {
        Exception exception;
        ErrorResponse error = parseErrorFromContentOrNull(response.getContent());
        switch (response.getStatusCode()) {
            case 403:
                exception = new AuthorizationException(command.getRequest(),
                        error.getMessage());
                break;
            default:
                exception = error != null ?
                        new GoGridResponseException(command, response, error) :
                        new HttpResponseException(command, response);
        }
        command.setException(exception);
        Closeables.closeQuietly(response.getContent());
    }

    ErrorResponse parseErrorFromContentOrNull(InputStream content) {
        if (content != null) {
            return errorParser.apply(content);
        }
        return null;
    }
}
