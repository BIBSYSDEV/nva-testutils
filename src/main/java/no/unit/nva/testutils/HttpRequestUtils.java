package no.unit.nva.testutils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.mockito.invocation.InvocationOnMock;

public class HttpRequestUtils {

    private final HttpRequest request;

    public HttpRequestUtils(HttpRequest request) {
        this.request = request;
    }

    public HttpResponse<String> responseEchoingRequestBody(InvocationOnMock invocation) {
        HttpRequest request = invocation.getArgument(0);
        String body = RequestBodyReader.requestBody(request);
        return echoRequest(body);
    }

    protected HttpResponse<String> echoRequest(String body) {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(response.body()).thenReturn(body);
        when(response.headers()).thenReturn(echoHeaders());
        return response;
    }

    protected HttpHeaders echoHeaders() {
        return request.headers();
    }
}
