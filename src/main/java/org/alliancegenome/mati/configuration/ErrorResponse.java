package org.alliancegenome.mati.configuration;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/** Error response is a list of Error Messages */
@Getter
@EqualsAndHashCode
public class ErrorResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorId;
    private List<ErrorMessage> errors;

    /** Constructor
     * @param errorId useful to track errors on the server logs
     * @param errorMessage message detailing the error
     * */
    public ErrorResponse(String errorId, ErrorMessage errorMessage) {
        this.errorId = errorId;
        this.errors = List.of(errorMessage);
    }

    /** Constructor
     * @param errorMessage message detailing the error
     * */
    public ErrorResponse(ErrorMessage errorMessage) {
        this(null, errorMessage);
    }

    /** Constructor
     * @param errors list of error messages
     * */
    public ErrorResponse(List<ErrorMessage> errors) {
        this.errorId = null;
        this.errors = errors;
    }

    /** Error Message with API path and inferred cause */
    @Getter
    @EqualsAndHashCode
    public static class ErrorMessage {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String path;
        private String message;

        /** Constructor
         * @param path  API path (e.g. /api/identifier)
         * @param message message describing the error*/
        public ErrorMessage(String path, String message) {
            this.path = path;
            this.message = message;
        }

        /** Constructor
         * @param message message describing the error */
        public ErrorMessage(String message) {
            this.path = null;
            this.message = message;
        }
    }
}
