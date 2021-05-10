package no.unit.nva.stubs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import com.github.javafaker.Faker;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

class FakeS3ClientTest {

    public static final Faker FAKER = Faker.instance();

    @Test
    public void putObjectMakesContentAvailableForGetting() {
        FakeS3Client fakeS3Client = new FakeS3Client(new ConcurrentHashMap<>());
        URI s3Uri = URI.create("s3://bucket/some/path/file.txt");
        String expectedContent = randomString();

        putObject(fakeS3Client, s3Uri, expectedContent);
        ResponseBytes<GetObjectResponse> result = getObject(fakeS3Client, s3Uri);
        String actualContent = result.asUtf8String();
        assertThat(actualContent, is(equalTo(expectedContent)));
    }

    private ResponseBytes<GetObjectResponse> getObject(FakeS3Client fakeS3Client, URI s3Uri) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                                                .bucket(s3Uri.getHost())
                                                .key(s3Uri.getPath())
                                                .build();
        ResponseBytes<GetObjectResponse> result =
            fakeS3Client.getObject(getObjectRequest, ResponseTransformer.toBytes());
        return result;
    }

    private void putObject(FakeS3Client fakeS3Client, URI s3Uri, String expectedContent) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                .bucket(s3Uri.getHost())
                                                .key(s3Uri.getPath())
                                                .build();

        fakeS3Client.putObject(putObjectRequest,
                               RequestBody.fromBytes(expectedContent.getBytes(StandardCharsets.UTF_8)));
    }

    private String randomString() {
        return FAKER.lorem().sentence(10);
    }
}