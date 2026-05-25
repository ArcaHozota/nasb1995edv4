package app.preach.gospel.listener;

import org.jooq.exception.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import app.preach.gospel.common.ProjectConstants;
import lombok.extern.log4j.Log4j2;

/**
 * SpringMVC例外処理インターセプト
 *
 * @author ArkamaHozota
 * @since 2.00
 */
@Log4j2
@RestControllerAdvice
public class ProjectExceptionHandler {

//	@ExceptionHandler(AccessDeniedException.class)
//	public Object handleAccessDeniedException(final AccessDeniedException exception) {
//		log.error("Access denied: {}", exception.getMessage());
//		return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON)
//				.body(ProjectConstants.MESSAGE_SPRINGSECURITY_REQUIRED_AUTH);
//	}

	@ExceptionHandler(DataAccessException.class)
	public Object handleDataAccessException(final DataAccessException exception) {
		final var errorMessage = exception.getMessage() != null ? exception.getMessage()
				: ProjectConstants.MESSAGE_STRING_FATAL_ERROR;
		log.error("Database error: {}", errorMessage);
		return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(errorMessage);
	}

	@ExceptionHandler(Exception.class)
	public Object handleException(final Exception exception) {
		final var errorMessage = exception.getMessage() != null ? exception.getMessage()
				: ProjectConstants.MESSAGE_STRING_FATAL_ERROR;
		log.error("処理中にエラーが発生しました：", exception);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
				.body(errorMessage);
	}

}