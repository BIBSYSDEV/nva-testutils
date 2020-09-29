package no.unit.nva.testutils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import java.util.UUID;

public class MockContext implements Context {

    private final Context context;
    private final String awsRequestId = UUID.randomUUID().toString();

    public MockContext() {
        this.context = mock(Context.class);
        when(context.getAwsRequestId()).thenReturn(awsRequestId);
    }

    public MockContext(Context context) {
        this.context = context;
    }

    @Override
    public String getAwsRequestId() {
        return context.getAwsRequestId();
    }

    @Override
    public String getLogGroupName() {
        return context.getLogGroupName();
    }

    @Override
    public String getLogStreamName() {
        return context.getLogStreamName();
    }

    @Override
    public String getFunctionName() {
        return context.getFunctionName();
    }

    @Override
    public String getFunctionVersion() {
        return context.getFunctionVersion();
    }

    @Override
    public String getInvokedFunctionArn() {
        return context.getInvokedFunctionArn();
    }

    @Override
    public CognitoIdentity getIdentity() {
        return context.getIdentity();
    }

    @Override
    public ClientContext getClientContext() {
        return context.getClientContext();
    }

    @Override
    public int getRemainingTimeInMillis() {
        return context.getRemainingTimeInMillis();
    }

    @Override
    public int getMemoryLimitInMB() {
        return context.getMemoryLimitInMB();
    }

    @Override
    public LambdaLogger getLogger() {
        return context.getLogger();
    }
}
