package com.pmoproject.employeeservice.configuration.rabbitmq;

import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.Map;

/**
 * Represents a custom retry policy that rabbit is configured to use when retrying failed messages
 */
public class CustomRetryPolicy extends SimpleRetryPolicy {

    private final BinaryExceptionClassifier retryableClassifier;
    private final int maxAttempts;

    @Override
    public boolean canRetry(RetryContext context) {
        Throwable t = context.getLastThrowable();
        return (t == null || retryForException(t.getCause())) && context.getRetryCount() < maxAttempts;
    }

    /**
     * Used to instantiate an object of this class
     * @param maxAttempts the maximum number of attempts the consumer attempt to the process the message before sending it to the DLQ
     * @param retryableExceptions a map used to indicate what exceptions are considered non-fatal (consumer will retry)
     */
    public CustomRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions) {
        this.maxAttempts = maxAttempts;
        this.retryableClassifier = new BinaryExceptionClassifier(retryableExceptions, false);
    }

    /**
     * method used to check whether a given exception is non-fatal
     * @param ex the exception to check
     * @return a boolean indicating whether an exception is considered non-fatal
     */
    private boolean retryForException(Throwable ex) {
        return this.retryableClassifier.classify(ex);
    }
}

