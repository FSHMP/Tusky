package com.keylesspalace.tusky.network;

import android.support.annotation.NonNull;

import com.keylesspalace.tusky.TuskyApplication;
import com.keylesspalace.tusky.db.AccountEntity;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by charlag on 31/10/17.
 */

public final class AuthInterceptor implements Interceptor {

    public AuthInterceptor() {
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        AccountEntity currentAccount = TuskyApplication.getAccountManager().getActiveAccount();

        Request originalRequest = chain.request();

        Request.Builder builder = originalRequest.newBuilder();
        // In the future we could add a phantom header parameter to some requests which would
        // signalise that we should override current account (could be useful for "boost as.."
        // actions and the like
        if (currentAccount != null) {
            // I'm not sure it's enough the hostname but should be good
            builder.url(originalRequest.url().newBuilder()
                    .host(currentAccount.getDomain())
                    .build())
                    .header("Authorization",
                            String.format("Bearer %s", currentAccount.getAccessToken()));
        }
        Request newRequest = builder.build();

        return chain.proceed(newRequest);
    }

}
