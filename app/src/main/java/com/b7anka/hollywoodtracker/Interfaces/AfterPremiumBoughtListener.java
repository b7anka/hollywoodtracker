package com.b7anka.hollywoodtracker.Interfaces;

import com.b7anka.hollywoodtracker.Model.APIResponse;

import retrofit2.Response;

public interface AfterPremiumBoughtListener
{
    void afterPremiumBoughtSuccessfully(Response<APIResponse> response, String detailss);
    void afterPremiumBoughtFailed(Throwable t, String details);
}
