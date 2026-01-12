package com.example.notecook.Utils;

import retrofit2.Response;

public class Result<T> {

    public enum Status { LOADING, SUCCESS, ERROR }

    public final Status status;
    public final T data;
    public final Throwable error;
    public final Response<?> response;

    private Result(Status status, T data, Throwable error, Response<?> response) {
        this.status = status;
        this.data = data;
        this.error = error;
        this.response = response;
    }

    public static <T> Result<T> loading() {
        return new Result<>(Status.LOADING, null, null, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(Status.SUCCESS, data, null, null);
    }

    public static <T> Result<T> error(Throwable error, Response<?> response) {
        return new Result<>(Status.ERROR, null, error, response);
    }
}

