package com.github.j00walling.idm.util;

import org.springframework.stereotype.Component;

@Component
public final class Validate
{
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
