package net.stawrul.services.exceptions;

import org.springframework.http.converter.json.GsonBuilderUtils;

import java.sql.SQLOutput;
import java.util.logging.*;

/**
 * Wyjątek sygnalizujący niedostępność towaru.
 * <p>
 * Wystąpienie wyjątku z hierarchii RuntimeException w warstwie biznesowej
 * powoduje wycofanie transakcji (rollback).
 */
public class TooLargeOrderException extends RuntimeException {
    public TooLargeOrderException() {
        Logger logger = Logger.getLogger(getClass().getName());
        logger.log(Level.WARNING, "Handling " + this.getClass().getName());
    }
}
