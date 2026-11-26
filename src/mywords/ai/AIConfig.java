package mywords.ai;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import mywords.exception.AIServiceException;

public class AIConfig
{
    private static final String[] KEYS = {"AI_API_KEY", "AI_ENDPOINT", "AI_MODEL",
            "CONNECT_TIMEOUT_MS", "READ_TIMEOUT_MS", "MAX_RESPONSE_BYTES"};
    private static final int MAX_TIMEOUT_MS = 120_000;
    private static final int MAX_RESPONSE_LIMIT = 1_048_576;

    private final String apiKey;
    private final String endpoint;
    private final String model;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;
    private final int maxResponseBytes;

    private AIConfig(String[] values) throws AIServiceException
    {
        apiKey = values[0];
        endpoint = values[1];
        model = values[2];
        connectTimeoutMs = readLimit(values[3], KEYS[3], MAX_TIMEOUT_MS);
        readTimeoutMs = readLimit(values[4], KEYS[4], MAX_TIMEOUT_MS);
        maxResponseBytes = readLimit(values[5], KEYS[5], MAX_RESPONSE_LIMIT);

        if (!apiKey.matches("[A-Za-z0-9._~+/=-]+"))
        {
            throw new AIServiceException("Check the API key in config.txt.");
        }
        checkEndpoint(endpoint);
    }

    public static AIConfig load(String path) throws AIServiceException
    {
        // Each slot in values belongs to the key at the same index.
        String[] values = new String[KEYS.length];
        try (BufferedReader reader = Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8))
        {
            String line = reader.readLine();
            if (line != null && line.startsWith("\uFEFF"))
            {
                line = line.substring(1); // Some editors add a UTF-8 marker.
            }
            for (; line != null; line = reader.readLine())
            {
                line = line.strip();
                if (line.isEmpty() || line.startsWith("#"))
                {
                    continue;
                }
                int equals = line.indexOf('=');
                if (equals < 1)
                {
                    throw new AIServiceException("Use KEY=value lines in config.txt.");
                }
                String key = line.substring(0, equals).strip();
                if ("DATA_DIRECTORY".equals(key))
                {
                    continue; // An older template included this file-storage setting.
                }
                int index = keyIndex(key);
                if (index < 0)
                {
                    throw new AIServiceException("config.txt contains an unrecognized AI setting.");
                }
                if (values[index] != null)
                {
                    throw new AIServiceException("Remove the duplicate " + KEYS[index] + " in config.txt.");
                }
                values[index] = line.substring(equals + 1).strip();
            }
        }
        catch (IOException | IllegalArgumentException | SecurityException e)
        {
            throw new AIServiceException("Create or check your local config.txt to use online AI.");
        }

        for (int i = 0; i < values.length; i++)
        {
            if (values[i] == null || values[i].isEmpty() || values[i].startsWith("REPLACE_"))
            {
                throw new AIServiceException("Set " + KEYS[i] + " in your local config.txt.");
            }
        }
        return new AIConfig(values);
    }

    private static int keyIndex(String key)
    {
        for (int i = 0; i < KEYS.length; i++)
        {
            if (KEYS[i].equals(key))
            {
                return i;
            }
        }
        return -1;
    }

    private static int readLimit(String value, String key, int maximum) throws AIServiceException
    {
        try
        {
            int number = Integer.parseInt(value);
            if (number >= 1 && number <= maximum)
            {
                return number;
            }
        }
        catch (NumberFormatException e)
        {
            // Use the same safe message for invalid numbers and values out of range.
        }
        throw new AIServiceException(key + " in config.txt must be an integer from 1 to " + maximum + ".");
    }

    private static void checkEndpoint(String endpoint) throws AIServiceException
    {
        try
        {
            URI uri = new URI(endpoint);
            if ("https".equalsIgnoreCase(uri.getScheme()) && uri.getHost() != null
                    && uri.getUserInfo() == null && uri.getFragment() == null && uri.getQuery() == null
                    && (uri.getPort() == -1 || (uri.getPort() > 0 && uri.getPort() <= 65535)))
            {
                return;
            }
        }
        catch (URISyntaxException e)
        {
            // A malformed setting may contain sensitive text. Use the fixed message below.
        }
        throw new AIServiceException("Set AI_ENDPOINT to the approved HTTPS endpoint in config.txt.");
    }

    public String getApiKey()
    {
        return apiKey;
    }

    public String getEndpoint()
    {
        return endpoint;
    }

    public String getModel()
    {
        return model;
    }

    int getConnectTimeoutMs()
    {
        return connectTimeoutMs;
    }

    int getReadTimeoutMs()
    {
        return readTimeoutMs;
    }

    int getMaxResponseBytes()
    {
        return maxResponseBytes;
    }
}
