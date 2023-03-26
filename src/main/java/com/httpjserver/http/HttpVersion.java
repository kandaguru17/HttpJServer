package com.httpjserver.http;

import com.httpjserver.core.parser.HttpJParsingException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Http Version of the incoming request.
 */
public enum HttpVersion {

    HTTP_1_1("HTTP/1.1", 1, 1);

    private final String versionLiteral;
    private final int minor;
    private final int major;

    HttpVersion(String versionLiteral, int minor, int major) {
        this.versionLiteral = versionLiteral;
        this.minor = minor;
        this.major = major;
    }


    private static final Pattern versionPattern = Pattern.compile("^HTTP/(?<major>\\d+).(?<minor>\\d+)");

    /**
     * Helper method to identify the closest match http-version when the minor version is different
     *
     * @param versionLiteral the http version literal
     * @return the compatible {@link HttpVersion}
     * @throws HttpJParsingException thrown when an unimplemented {@link HttpVersion} is encountered
     */
    public static HttpVersion getCompatibleHttpVersion(String versionLiteral) throws HttpJParsingException {

        Matcher matcher = versionPattern.matcher(versionLiteral);

        if (!matcher.matches()) {
            throw new HttpJParsingException(HttpStatusCode.BAD_REQUEST);
        }

        int minor = Integer.parseInt(matcher.group("minor"));
        int major = Integer.parseInt(matcher.group("major"));

        HttpVersion compatibleVersion = null;

        for (var version : HttpVersion.values()) {

            if (version.versionLiteral.equals(versionLiteral)) {
                return version;
            }

            if (version.major == major) {
                if (version.minor < minor) {
                    compatibleVersion = version;
                }
            }
        }
        return compatibleVersion;
    }

}
