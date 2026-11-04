package com.poli.client.socket;

import java.io.IOException;

import com.poli.common.dto.Response;

public interface IResponseListener {
    void onResponse(Response response);
    void onError(IOException e);
}
