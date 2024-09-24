package lofitsky.komptube.komp;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import lofitsky.komptube.common.Mode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class WebServer extends NanoHTTPD {
    // default value
    private String phoneUrl = "http://192.168.1.3:8765";

    public WebServer(int port) {
        super(port);

        String url = System.getenv("urls.phone");
        if(url != null && !url.isBlank()) {
            phoneUrl = url;
        }
    }

    public static void main(String[] args) throws IOException {
        new WebServer(8765).start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public static final Logger log = LoggerFactory.getLogger(WebServer.class.getName());

    @Override
    public Response serve(IHTTPSession session) {
        log.info("Got request: {}", session.getQueryParameterString());

        if(session.getMethod() != Method.GET) {
            log.warn("Bad request method: {}", session.getMethod());
            return newFixedLengthResponse(Status.METHOD_NOT_ALLOWED, NanoHTTPD.MIME_PLAINTEXT, "Use GET for this request");
        }

        String modeStr = getParam(session, "mode");
        try {
            Mode mode = Mode.valueOfOrNull(modeStr);
            switch(mode) {
                case SHARE_TO_KOMP -> openInBrowser(session);
                case SHARE_TO_PHONE -> shareToPhone(session);
                case HEALTH_CHECK -> { return selfHealthCheck(session); }
                case PHONE_WEBSERVER_HEALTH_CHECK -> { return checkPhoneWebserver(); }
                default -> throw new IllegalArgumentException("Unrecognized mode: " + modeStr);
            }
        } catch(Exception e) {
            log.error("Failed to process mode {}: {}", modeStr, e);
            return newFixedLengthResponse(Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "Invalid mode " + modeStr);
        }

        return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, "OK");
    }

    private Response selfHealthCheck(IHTTPSession session) {
        var now = new Date();
        log.info("Self health check at " + now);
        return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, "alive at " + now);
    }

    private String getParam(IHTTPSession session, String name) {
        return Optional.ofNullable(session.getParameters())
            .map(it -> it.get(name))
            .filter(it -> !it.isEmpty())
            .map(it -> it.get(0))
            .orElseThrow(() -> new IllegalArgumentException("Missing parameter: " + name));
    }

    private void openInBrowser(IHTTPSession session) throws IOException {
        var url = getParam(session, "url");
        log.info("Opening in browser {}", url);
        Runtime.getRuntime().exec("xdg-open " + url);
    }

    private void shareToPhone(IHTTPSession session) throws IOException {
        var url = getParam(session, "url");
        var urlEncoded = URLEncoder.encode(url, StandardCharsets.UTF_8);

        Request request = new Request.Builder()
                .get()
                .url(phoneUrl + "/?url=" + urlEncoded)
            .build();

        log.info("Sharing to phone {}", request.url());
        new Thread(() -> {
            try {
                new OkHttpClient().newCall(request).execute();
            } catch(IOException e) {
                log.error("{}", e.getMessage());
                log.error("{}", e);
                throw new RuntimeException(e);
            }
        }).start();
    }

    private Response checkPhoneWebserver() {
        Request request = new Request.Builder()
                .get()
                .url(phoneUrl + "/?mode=" + Mode.HEALTH_CHECK)
            .build();

        log.info("Health-check phone's webserver");
        try(var response = new OkHttpClient.Builder()
                .callTimeout(10, TimeUnit.SECONDS)
            .build()
            .newCall(request).execute()) {

                if(!response.isSuccessful()) {
                    return newFixedLengthResponse(Status.SERVICE_UNAVAILABLE, NanoHTTPD.MIME_PLAINTEXT, "Phone webserver unavailable");
                } else {
                    return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, "Phone webserver is alive: " + new String(response.body().bytes()));
                }
        } catch(Exception e) {
            log.error("{}", e.getMessage());
            log.error("{}", e);
            throw new RuntimeException(e);
        }
    }
}
