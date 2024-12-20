package ewm.stats.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Обработчик ошибок.
 * Класс перехватывает исключения, выбрасываемые в контроллере.
 * Возвращает соответствующие HTTP-статусы и сообщения об ошибках.
 */
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(InvalidTimeRangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // Код ответа 400
    public ErrorResponse handleInvalidTimeRangeException(InvalidTimeRangeException exception) {
        log.warn("Неверный диапазон времени: {}", exception.getMessage());
        return new ErrorResponse("Ошибка.", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse missingHeaderException(final MissingRequestHeaderException exception) {
        String description = exception.getMessage();
        log.warn(description);
        return new ErrorResponse("Ошибка.", description);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Throwable e) {
        log.error("Возникло исключение", e);
        return new ErrorResponse("Возникло исключение.", e.getMessage());
    }
}