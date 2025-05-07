package org.alliancegenome.mati.configuration;


import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.jbosslog.JBossLog;

import java.util.UUID;

/**
 * Allows to send the exceptions we are not capturing with ConstraintViolationExceptionMapper to
 * the standard error log
 */
@Provider
@JBossLog
public class ThrowableMapper implements ExceptionMapper<Throwable> {

	@Override
	public Response toResponse(Throwable e) {
		String errorId = UUID.randomUUID().toString();
		log.error("errorId[{}]", errorId, e);
		String defaultErrorMessage = "System.error";
		ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage(defaultErrorMessage);
		ErrorResponse errorResponse = new ErrorResponse(errorId, errorMessage);
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
	}
}
