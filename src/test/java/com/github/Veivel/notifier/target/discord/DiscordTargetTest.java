package com.github.Veivel.notifier.target.discord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.logger.ModLogger;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiscordTargetTest {

    private static final String TARGET_NAME = "discord";
    private static final String VALID_WEBHOOK_URL = "https://example.com/hook";
    // A space in the authority is illegal per RFC 2396; new URI(...) throws.
    private static final String INVALID_WEBHOOK_URL =
        "https://exa mple.com/hook";

    @Mock
    private Logger logger;

    @Mock
    private HttpClient httpClient;

    private DiscordConfig validConfig;

    @BeforeEach
    void setUp() {
        validConfig = new DiscordConfig(TARGET_NAME, true, VALID_WEBHOOK_URL);
    }

    // The final `logger` field in DiscordTarget is initialized at construction
    // time via ModLogger.get(). Wrap the constructor in a MockedStatic so the
    // instance ends up holding the test's logger mock for the rest of its life.
    private DiscordTarget newTarget(DiscordConfig config, HttpClient client) {
        try (MockedStatic<ModLogger> mocked = mockStatic(ModLogger.class)) {
            mocked.when(ModLogger::get).thenReturn(logger);
            return new DiscordTarget(config, client);
        }
    }

    private DiscordTarget newTarget(DiscordConfig config) {
        return newTarget(config, httpClient);
    }

    @SuppressWarnings("unchecked")
    private HttpResponse<String> responseWith(int statusCode, String body) {
        HttpResponse<String> response = mock(HttpResponse.class);
        // handleAsync schedules the response handler on ForkJoinPool; success
        // tests assert synchronously and may exit before that callback fires,
        // leaving these stubs technically unused. Mark them lenient so the
        // strict-stubbing check does not flag them.
        lenient().when(response.statusCode()).thenReturn(statusCode);
        lenient().when(response.body()).thenReturn(body);
        return response;
    }

    @Test
    void constructor_storesConfigOnTheInstance() {
        DiscordTarget target = newTarget(validConfig);

        assertThat(target.getConfig()).isSameAs(validConfig);
    }

    @Test
    void healthCheck_returnsTrue_whenSendPayloadSucceeds() {
        // doReturn(...) sidesteps generic inference on sendAsync(<String>).
        doReturn(
            CompletableFuture.completedFuture(responseWith(204, ""))
        ).when(httpClient).sendAsync(any(), any());
        DiscordTarget target = newTarget(validConfig);

        assertThat(target.healthCheck()).isTrue();
    }

    @Test
    void healthCheck_returnsFalse_whenWebhookUrlIsInvalid() {
        // URISyntaxException in the constructor leaves webhookUri null, which
        // forces healthCheck() to short-circuit before any payload is built.
        DiscordConfig invalid = new DiscordConfig(
            TARGET_NAME,
            true,
            INVALID_WEBHOOK_URL
        );

        DiscordTarget target = newTarget(invalid);

        assertThat(target.healthCheck()).isFalse();
    }

    @Test
    void sendReadout_postsThePayloadToTheConfiguredWebhook() {
        doReturn(
            CompletableFuture.completedFuture(responseWith(204, ""))
        ).when(httpClient).sendAsync(any(), any());
        DiscordTarget target = newTarget(validConfig);

        target.sendReadout(
            new ReadoutEvent("Steve", 3, 1, 2, 3, "minecraft:overworld")
        );

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(
            HttpRequest.class
        );
        verify(httpClient).sendAsync(requestCaptor.capture(), any());

        HttpRequest sent = requestCaptor.getValue();
        assertThat(sent.uri().toString()).isEqualTo(VALID_WEBHOOK_URL);
        assertThat(sent.method()).isEqualTo("POST");
    }

    @Test
    void sendReadout_logsError_whenResponseStatusIs4xxOrHigher() {
        doReturn(
            CompletableFuture.completedFuture(
                responseWith(500, "server error")
            )
        ).when(httpClient).sendAsync(any(), any());
        DiscordTarget target = newTarget(validConfig);

        target.sendReadout(
            new ReadoutEvent("Steve", 3, 1, 2, 3, "minecraft:overworld")
        );

        // handleAsync schedules the response handler on ForkJoinPool, so wait
        // up to two seconds for the error log to land rather than racing the
        // assertion against the async callback.
        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(
            Object.class
        );
        verify(logger, timeout(2000)).error(
            eq("Failed to send Discord webhook, response: {}"),
            bodyCaptor.capture()
        );
        assertThat(bodyCaptor.getValue()).isEqualTo("server error");
    }

    @Test
    void getConfig_returnsTheConfigPassedAtConstruction() {
        DiscordTarget target = newTarget(validConfig);

        assertThat(target.getConfig()).isSameAs(validConfig);
    }
}
