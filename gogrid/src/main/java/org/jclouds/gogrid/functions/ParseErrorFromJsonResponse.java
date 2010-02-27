package org.jclouds.gogrid.functions;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Singleton;
import org.jclouds.gogrid.domain.internal.ErrorResponse;
import org.jclouds.gogrid.domain.internal.GenericResponseContainer;
import org.jclouds.http.functions.ParseJson;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author Oleksiy Yarmula
 */
@Singleton
public class ParseErrorFromJsonResponse extends ParseJson<ErrorResponse> {

    @Inject
    public ParseErrorFromJsonResponse(Gson gson) {
        super(gson);
    }

    public ErrorResponse apply(InputStream stream) {
        Type setType = new TypeToken<GenericResponseContainer<ErrorResponse>>() {
        }.getType();
        GenericResponseContainer<ErrorResponse> response;
        try {
            response = gson.fromJson(new InputStreamReader(stream, "UTF-8"), setType);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("jclouds requires UTF-8 encoding", e);
        }
        checkState(response.getList() != null && response.getList().size() == 1,
                /*or throw*/ "Expected exactly 1 error object in response");
        return Iterables.getOnlyElement(response.getList());
    }
}
