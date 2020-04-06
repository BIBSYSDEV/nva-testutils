package no.unit.nva.testutils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.mockito.invocation.InvocationOnMock;

public class HttpRequestUtils {

    public HttpResponse<String> responseEchoingRequestBody(InvocationOnMock invocation) {
        HttpRequest request = invocation.getArgument(0);
        String body = RequestBodyReader.requestBody(request);
        HttpHeaders headers = mockHeaders(request);
        return mockRequest(body, headers);
    }

    protected HttpResponse<String> mockRequest(String body, HttpHeaders headers) {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(response.body()).thenReturn(body);
        when(response.headers()).thenReturn(headers);
        return response;
    }

    protected HttpHeaders mockHeaders(HttpRequest request) {
        return request.headers();
    }
}
