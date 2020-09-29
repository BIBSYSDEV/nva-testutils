package no.unit.nva.mocks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.regions.Region;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithSAMLRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithSAMLResult;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.DecodeAuthorizationMessageRequest;
import com.amazonaws.services.securitytoken.model.DecodeAuthorizationMessageResult;
import com.amazonaws.services.securitytoken.model.GetAccessKeyInfoRequest;
import com.amazonaws.services.securitytoken.model.GetAccessKeyInfoResult;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityResult;
import com.amazonaws.services.securitytoken.model.GetFederationTokenRequest;
import com.amazonaws.services.securitytoken.model.GetFederationTokenResult;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class MockStsClient implements AWSSecurityTokenService {

    public static final String SAMPLE_ACCESS_KEY_ID = "accessKeyId";
    public static final String SAMPLE_ACCESS_KEY = "accessKey";
    public static final String SAMPLE_SESSION_TOKEN = "sessionToken";
    private final AWSSecurityTokenService sts;

    public MockStsClient() {
        sts = mockStsService();
    }

    public MockStsClient(AWSSecurityTokenService sts) {
        this.sts = sts;
    }

    @Override
    public AssumeRoleResult assumeRole(AssumeRoleRequest assumeRoleRequest) {
        return sts.assumeRole(assumeRoleRequest);
    }

    @Override
    public void setEndpoint(String endpoint) {
        throw new UnsupportedOperationException("Not implemented in MockStsClient");
    }

    @Override
    public void setRegion(Region region) {
        throw new UnsupportedOperationException("Not implemented in MockStsClient");
    }

    @Override
    public AssumeRoleWithSAMLResult assumeRoleWithSAML(AssumeRoleWithSAMLRequest assumeRoleWithSAMLRequest) {
        throw new UnsupportedOperationException("Not implemented in MockStsClient");
    }

    @Override
    public AssumeRoleWithWebIdentityResult assumeRoleWithWebIdentity(
        AssumeRoleWithWebIdentityRequest assumeRoleWithWebIdentityRequest) {
        throw new UnsupportedOperationException("Not implemented in MockStsClient");
    }

    @Override
    public DecodeAuthorizationMessageResult decodeAuthorizationMessage(
        DecodeAuthorizationMessageRequest decodeAuthorizationMessageRequest) {
        throw new UnsupportedOperationException("Not implemented in MockStsClient");
    }

    @Override
    public GetAccessKeyInfoResult getAccessKeyInfo(GetAccessKeyInfoRequest getAccessKeyInfoRequest) {
        throw new UnsupportedOperationException("Not implemented in MockStsClient");
    }

    @Override
    public GetCallerIdentityResult getCallerIdentity(GetCallerIdentityRequest getCallerIdentityRequest) {
        throw new UnsupportedOperationException("Not implemented in MockStsClient");
    }

    @Override
    public GetFederationTokenResult getFederationToken(GetFederationTokenRequest getFederationTokenRequest) {
        throw new UnsupportedOperationException("Not implemented in MockStsClient");
    }

    @Override
    public GetSessionTokenResult getSessionToken(GetSessionTokenRequest getSessionTokenRequest) {
        throw new UnsupportedOperationException("Not implemented in MockStsClient");
    }

    @Override
    public GetSessionTokenResult getSessionToken() {
        throw new UnsupportedOperationException("Not implemented in MockStsClient");
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("Not implemented in MockStsClient");
    }

    @Override
    public ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest request) {
        return null;
    }

    private AWSSecurityTokenService mockStsService() {
        AWSSecurityTokenService sts = mock(AWSSecurityTokenService.class);
        when(sts.assumeRole(any(AssumeRoleRequest.class))).thenReturn(mockAssumeRole());
        return sts;
    }

    private AssumeRoleResult mockAssumeRole() {
        return new AssumeRoleResult().withCredentials(mockCredentials());
    }

    private Credentials mockCredentials() {
        return new Credentials(SAMPLE_ACCESS_KEY_ID, SAMPLE_ACCESS_KEY, SAMPLE_SESSION_TOKEN,
            Date.from(Instant.now().plus(Duration.ofDays(10))));
    }
}
