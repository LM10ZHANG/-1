package com.salesms.common;

public final class TraceIdContext {
    private TraceIdContext() {
    }

    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    public static void set(String traceId) {
        TRACE_ID.set(traceId);
    }

    public static String get() {
        String v = TRACE_ID.get();
        return v == null ? "" : v;
    }

    public static void clear() {
        TRACE_ID.remove();
    }
}

