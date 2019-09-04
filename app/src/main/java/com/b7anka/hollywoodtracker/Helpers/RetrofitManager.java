package com.b7anka.hollywoodtracker.Helpers;

import android.util.ArrayMap;
import com.b7anka.hollywoodtracker.Interfaces.InterfaceAPI;
import com.b7anka.hollywoodtracker.Model.APIResponse;
import org.json.JSONObject;
import java.util.Map;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager
{
    public interface AfterLoginListener
    {
        void afterLoginSuccess(Response<APIResponse> response, String password);
        void afterLoginFailure(Throwable t);
    }

    public interface AfterLoginWithGoogleListener
    {
        void afterLoginWithGoogleSuccess(Response<APIResponse> response);
        void afterLoginWithGoogleFailure(Throwable t);
    }

    public interface AfterChangedPasswordFromLogin
    {
        void afterPasswordChangedSuccessfully(Response<APIResponse> response);
        void afterPasswordChangedFailed(Throwable t);
    }

    public interface ForgotPasswordListener
    {
        void afterForgotPasswordSuccess(Response<APIResponse> response);
        void afterForgotPasswordFailed(Throwable t);
    }

    public interface RegisterListener
    {
        void afterRegisterSuccessfully(Response<APIResponse> response);
        void afterRegisterFailure(Throwable t);
    }

    public interface RegisterWithGoogleListener
    {
        void afterRegisterWithGoogleSuccessfully(Response<APIResponse> response);
        void afterRegisterWithGoogleFailure(Throwable t);
    }

    public interface ReportBugsListener
    {
        void afterBugReportSentSuccessfully(Response<APIResponse> response);
        void afterBugReportSentFailure(Throwable t);
    }

    public interface GetDataListener
    {
        void afterDataRetrievedSuccessfully(Response<APIResponse> response);
        void afterDataRetrievedFailed(Throwable t);
    }

    public interface UpdateDataListener
    {
        void afterUpdatedDataSuccess(Response<APIResponse> response);
        void afterUpdateDataFailed(Throwable t);
    }

    public interface DeleteDataListener
    {
        void afterDataDeletionSuccessfully(Response<APIResponse> response);
        void afterDataDeletionFailed(Throwable t);
    }

    public interface SaveDataListener
    {
        void afterDataSavedSuccessfully(Response<APIResponse> response);
        void afterDataSavedFailed(Throwable t);
    }

    public interface DeleteUserAccountListener
    {
        void afterUserAccountDeletionSuccessfully(Response<APIResponse> response);
        void afterUserAccountDeletionFailed(Throwable t);
    }

    public interface PremiumBoughtListener
    {
        void afterPremiumBoughtSuccessfully(Response<APIResponse> response, String details);
        void afterPremiumBoughtFailed(Throwable t, String details);
    }

    public interface CheckUserListener
    {
        void afterUserCheckedSuccessfully(Response<APIResponse> response);
        void afterUserCheckedFails(Throwable t);
    }

    public interface AfterThumbnailUploadListener
    {
        void afterThumbnailUploadedSuccessfully(Response<APIResponse> response);
        void afterThumbnailUploadedFailed(Throwable t);
    }

    public interface GetRewardedVideos
    {
        void afterRewardedVideosLoadedSuccessfully(Response<APIResponse> response, boolean isSaving);
        void afterRewardedVideosLoadFailed(Throwable t);
    }

    public interface AfterGoogleAccountUpdatedListener
    {
        void afterGoogleAccountUpdatedSuccessfully(Response<APIResponse> response);
        void afterGoogleAccountUpdatedFailed(Throwable t);
    }

    private AfterLoginListener afterLoginListener;
    private AfterLoginWithGoogleListener afterLoginWithGoogleListener;
    private AfterChangedPasswordFromLogin afterChangedPasswordFromLogin;
    private ForgotPasswordListener forgotPasswordListener;
    private RegisterListener registerListener;
    private RegisterWithGoogleListener registerWithGoogleListener;
    private ReportBugsListener reportBugsListener;
    private GetDataListener getDataListener;
    private UpdateDataListener updateDataListener;
    private DeleteDataListener deleteDataListener;
    private SaveDataListener saveDataListener;
    private DeleteUserAccountListener deleteUserAccountListener;
    private PremiumBoughtListener premiumBoughtListener;
    private CheckUserListener checkUserListener;
    private AfterThumbnailUploadListener afterThumbnailUploadListener;
    private GetRewardedVideos getRewardedVideos;
    private AfterGoogleAccountUpdatedListener afterGoogleAccountUpdatedListener;
    private Retrofit retrofit;
    private InterfaceAPI request;
    private RequestBody body;

    public RetrofitManager()
    {
        this.retrofit = getRetrofit();
        this.request = retrofit.create(InterfaceAPI.class);
    }

    public RetrofitManager(AfterLoginListener afterLoginListener)
    {
        this();
        this.afterLoginListener = afterLoginListener;
    }

    public RetrofitManager(AfterLoginWithGoogleListener afterLoginWithGoogleListener)
    {
        this();
        this.afterLoginWithGoogleListener = afterLoginWithGoogleListener;
    }

    public RetrofitManager(AfterChangedPasswordFromLogin afterChangedPasswordFromLogin)
    {
        this();
        this.afterChangedPasswordFromLogin = afterChangedPasswordFromLogin;
    }

    public RetrofitManager(ForgotPasswordListener forgotPasswordListener)
    {
        this();
        this.forgotPasswordListener = forgotPasswordListener;
    }

    public RetrofitManager(RegisterListener registerListener)
    {
        this();
        this.registerListener = registerListener;
    }

    public RetrofitManager(ReportBugsListener reportBugsListener)
    {
        this();
        this.reportBugsListener = reportBugsListener;
    }

    public RetrofitManager(GetDataListener getDataListener)
    {
        this();
        this.getDataListener = getDataListener;
    }

    public RetrofitManager(UpdateDataListener updateDataListener)
    {
        this();
        this.updateDataListener = updateDataListener;
    }

    public RetrofitManager(DeleteDataListener deleteDataListener)
    {
        this();
        this.deleteDataListener = deleteDataListener;
    }

    public RetrofitManager(SaveDataListener saveDataListener) {
        this();
        this.saveDataListener = saveDataListener;
    }

    public RetrofitManager(DeleteUserAccountListener deleteUserAccountListener) {
        this();
        this.deleteUserAccountListener = deleteUserAccountListener;
    }

    public RetrofitManager(PremiumBoughtListener premiumBoughtListener) {
        this();
        this.premiumBoughtListener = premiumBoughtListener;
    }

    public RetrofitManager(CheckUserListener checkUserListener)
    {
        this();
        this.checkUserListener = checkUserListener;
    }

    public RetrofitManager(RegisterWithGoogleListener registerWithGoogleListener)
    {
        this();
        this.registerWithGoogleListener = registerWithGoogleListener;
    }

    public RetrofitManager(AfterThumbnailUploadListener afterThumbnailUploadListener)
    {
        this();
        this.afterThumbnailUploadListener = afterThumbnailUploadListener;
    }

    public RetrofitManager(GetRewardedVideos getRewardedVideos)
    {
        this();
        this.getRewardedVideos = getRewardedVideos;
    }

    public RetrofitManager(AfterGoogleAccountUpdatedListener afterGoogleAccountUpdatedListener)
    {
        this();
        this.afterGoogleAccountUpdatedListener = afterGoogleAccountUpdatedListener;
    }

    public RetrofitManager(GetDataListener getDataListener, UpdateDataListener updateDataListener,
                           DeleteDataListener deleteDataListener, CheckUserListener checkUserListener)
    {
        this();
        this.getDataListener = getDataListener;
        this.updateDataListener = updateDataListener;
        this.deleteDataListener = deleteDataListener;
        this.checkUserListener = checkUserListener;
    }

    public RetrofitManager(GetDataListener getDataListener, UpdateDataListener updateDataListener, DeleteDataListener deleteDataListener)
    {
        this();
        this.getDataListener = getDataListener;
        this.updateDataListener = updateDataListener;
        this.deleteDataListener = deleteDataListener;
    }

    public RetrofitManager(GetDataListener getDataListener, UpdateDataListener updateDataListener, DeleteUserAccountListener deleteUserAccountListener, CheckUserListener checkUserListener) {
        this();
        this.getDataListener = getDataListener;
        this.updateDataListener = updateDataListener;
        this.deleteUserAccountListener = deleteUserAccountListener;
        this.checkUserListener = checkUserListener;
    }

    public RetrofitManager(GetDataListener getDataListener, UpdateDataListener updateDataListener, DeleteUserAccountListener deleteUserAccountListener, CheckUserListener checkUserListener, AfterGoogleAccountUpdatedListener afterGoogleAccountUpdatedListener)
    {
        this(getDataListener, updateDataListener, deleteUserAccountListener, checkUserListener);
        this.afterGoogleAccountUpdatedListener = afterGoogleAccountUpdatedListener;
    }

    public RetrofitManager(ReportBugsListener reportBugsListener, UpdateDataListener updateDataListener, DeleteDataListener deleteDataListener, SaveDataListener saveDataListener, PremiumBoughtListener premiumBoughtListener) {
        this();
        this.reportBugsListener = reportBugsListener;
        this.updateDataListener = updateDataListener;
        this.deleteDataListener = deleteDataListener;
        this.saveDataListener = saveDataListener;
        this.premiumBoughtListener = premiumBoughtListener;
    }

    public RetrofitManager(AfterLoginListener afterLoginListener, AfterLoginWithGoogleListener afterLoginWithGoogleListener) {
        this();
        this.afterLoginListener = afterLoginListener;
        this.afterLoginWithGoogleListener = afterLoginWithGoogleListener;
    }

    public RetrofitManager(AfterLoginListener afterLoginListener, AfterLoginWithGoogleListener afterLoginWithGoogleListener, RegisterWithGoogleListener registerWithGoogleListener, AfterGoogleAccountUpdatedListener afterGoogleAccountUpdatedListener)
    {
        this();
        this.afterLoginListener = afterLoginListener;
        this.afterLoginWithGoogleListener = afterLoginWithGoogleListener;
        this.registerWithGoogleListener = registerWithGoogleListener;
        this.afterGoogleAccountUpdatedListener = afterGoogleAccountUpdatedListener;
    }

    public void setDeleteDataListener(DeleteDataListener deleteDataListener) {
        this.deleteDataListener = deleteDataListener;
    }

    public void setSaveDataListener(SaveDataListener saveDataListener) {
        this.saveDataListener = saveDataListener;
    }

    public void login(String username, final String password)
    {
        if(this.afterLoginListener != null)
        {
            Map<String, String> jsonParams = new ArrayMap<>();
            jsonParams.put("username", username);
            jsonParams.put("pass", password);
            jsonParams.put("lang", OtherSharedMethods.getDefaultLocale());

            body = getRequestBody(jsonParams);

            Call<APIResponse> loginCall = request.postUserLogin(body);

            loginCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    afterLoginListener.afterLoginSuccess(response, password);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    afterLoginListener.afterLoginFailure(t);
                }
            });
        }
    }

    public void loginWithGoogle(String email)
    {
        if(this.afterLoginWithGoogleListener != null)
        {
            Map<String, String> jsonParams = new ArrayMap<>();
            jsonParams.put("email", email);
            jsonParams.put("lang", OtherSharedMethods.getDefaultLocale());

            body = getRequestBody(jsonParams);

            Call<APIResponse> loginCall = request.postUserLoginWithGoogle(body);

            loginCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    afterLoginWithGoogleListener.afterLoginWithGoogleSuccess(response);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    afterLoginWithGoogleListener.afterLoginWithGoogleFailure(t);
                }
            });
        }
    }

    public void updateGoogleAccountStatus(String email, boolean value)
    {
        if(this.afterGoogleAccountUpdatedListener != null)
        {
            Map<String, String> jsonParams = new ArrayMap<>();
            jsonParams.put("email", email);
            jsonParams.put("value", String.valueOf(value));
            jsonParams.put("lang", OtherSharedMethods.getDefaultLocale());

            body = getRequestBody(jsonParams);

            Call<APIResponse> loginCall = request.updateGoogleAccountState(body);

            loginCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    afterGoogleAccountUpdatedListener.afterGoogleAccountUpdatedSuccessfully(response);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    afterGoogleAccountUpdatedListener.afterGoogleAccountUpdatedFailed(t);
                }
            });
        }
    }

    public void changePassword(String password, int id)
    {
        if(this.afterChangedPasswordFromLogin != null)
        {
            Map<String, String> jsonParams = new ArrayMap<>();
            jsonParams.put("id", String.valueOf(id));
            jsonParams.put("type", Constants.PROFILE);
            jsonParams.put("content", String.format("%s%s",password,Constants.SEMI_COLON));
            jsonParams.put("lang", OtherSharedMethods.getDefaultLocale());
            jsonParams.put("time", String.valueOf(OtherSharedMethods.getTimeStamp()));

            body = getRequestBody(jsonParams);

            Call<APIResponse> updateDataCall = request.updateUserData(body);

            updateDataCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    afterChangedPasswordFromLogin.afterPasswordChangedSuccessfully(response);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    afterChangedPasswordFromLogin.afterPasswordChangedFailed(t);
                }
            });
        }
    }

    public void recoverPassword(String email)
    {
        if(this.forgotPasswordListener != null)
        {
            Map<String, String> jsonParams = new ArrayMap<>();
            jsonParams.put("email", email);
            jsonParams.put("lang", OtherSharedMethods.getDefaultLocale());

            body = getRequestBody(jsonParams);

            Call<APIResponse> recoverPassCall = request.recoverUserPassword(body);

            recoverPassCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    forgotPasswordListener.afterForgotPasswordSuccess(response);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    forgotPasswordListener.afterForgotPasswordFailed(t);
                }
            });
        }
    }

    public void updateUserData(int id, int idUser, String type, String content)
    {
        if(this.updateDataListener != null)
        {
            Map<String, String> jsonParams = new ArrayMap<>();
            jsonParams.put("id", String.valueOf(id));
            jsonParams.put("idUser", String.valueOf(idUser));
            jsonParams.put("type", type);
            jsonParams.put("content", content);
            jsonParams.put("lang", OtherSharedMethods.getDefaultLocale());
            jsonParams.put("time", String.valueOf(OtherSharedMethods.getTimeStamp()));

            body = getRequestBody(jsonParams);

            Call<APIResponse> updateDataCall = request.updateUserData(body);

            updateDataCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    updateDataListener.afterUpdatedDataSuccess(response);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    updateDataListener.afterUpdateDataFailed(t);
                }
            });
        }
    }

    public void register(String fullname, String username, String email, String password, String thumbnail)
    {
        if(this.registerListener != null)
        {
            Map<String, String> jsonParams = new ArrayMap<>();
            jsonParams.put("username", username);
            jsonParams.put("pass", password);
            jsonParams.put("lang", OtherSharedMethods.getDefaultLocale());
            jsonParams.put("fullname", fullname);
            jsonParams.put("email", email);
            jsonParams.put("thumbnail", thumbnail);

            body = getRequestBody(jsonParams);

            Call<APIResponse> registerCall = request.postUserRegister(body);

            registerCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    registerListener.afterRegisterSuccessfully(response);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    registerListener.afterRegisterFailure(t);
                }
            });
        }
    }

    public void registerWithGoogle(String fullname, String username, String email, String password, String thumbnail)
    {
        if(this.registerWithGoogleListener != null)
        {
            Map<String, String> jsonParams = new ArrayMap<>();
            jsonParams.put("username", username);
            jsonParams.put("pass", password);
            jsonParams.put("lang", OtherSharedMethods.getDefaultLocale());
            jsonParams.put("fullname", fullname);
            jsonParams.put("email", email);
            jsonParams.put("thumbnail", thumbnail);

            body = getRequestBody(jsonParams);

            Call<APIResponse> registerCall = request.postUserRegisterWithGoogle(body);

            registerCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    registerWithGoogleListener.afterRegisterWithGoogleSuccessfully(response);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    registerWithGoogleListener.afterRegisterWithGoogleFailure(t);
                }
            });
        }
    }

    public void reportBugs(String[] content)
    {
        if(reportBugsListener != null)
        {
            Map<String, String> jsonParams = new ArrayMap<>();
            jsonParams.put("title", content[0]);
            jsonParams.put("name", content[1]);
            jsonParams.put("email", content[2]);
            jsonParams.put("body", content[3]);
            jsonParams.put("lang", OtherSharedMethods.getDefaultLocale());

            body = getRequestBody(jsonParams);
            Call<APIResponse> sendBugReportCall = request.postBugReport(body);

            sendBugReportCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    reportBugsListener.afterBugReportSentSuccessfully(response);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    reportBugsListener.afterBugReportSentFailure(t);
                }
            });
        }
    }

    public void getData(int userId, String type)
    {
        if(getDataListener != null)
        {
            Call<APIResponse> getDataCall = request.getUserData(userId,type,OtherSharedMethods.getDefaultLocale(), OtherSharedMethods.getTimeStamp());

            getDataCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    getDataListener.afterDataRetrievedSuccessfully(response);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    getDataListener.afterDataRetrievedFailed(t);
                }
            });
        }
    }

    public void checkUserStatus(String username)
    {
        if(this.checkUserListener != null)
        {
            Call<APIResponse> getDataCall = request.checkUser(username, OtherSharedMethods.getDefaultLocale());

            getDataCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    checkUserListener.afterUserCheckedSuccessfully(response);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    checkUserListener.afterUserCheckedFails(t);
                }
            });
        }
    }

    public void saveUserData(int id, String type, String title, String watchedTime, int season, int episode, int completed)
    {
        if(this.saveDataListener != null)
        {
            Map<String, String> jsonParams = new ArrayMap<>();
            jsonParams.put("idUser", String.valueOf(id));
            jsonParams.put("type", type);
            jsonParams.put("title", title);
            jsonParams.put("watchedtime", watchedTime);
            jsonParams.put("season", String.valueOf(season));
            jsonParams.put("episode", String.valueOf(episode));
            jsonParams.put("completed", String.valueOf(completed));
            jsonParams.put("lang", OtherSharedMethods.getDefaultLocale());
            jsonParams.put("time", String.valueOf(OtherSharedMethods.getTimeStamp()));

            body = getRequestBody(jsonParams);

            Call<APIResponse> saveDataCall = request.saveUserData(body);

            saveDataCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    saveDataListener.afterDataSavedSuccessfully(response);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    saveDataListener.afterDataSavedFailed(t);
                }
            });
        }
    }

    public void deleteUserData(int id, int idUser, String type)
    {
        if(this.deleteDataListener != null)
        {
            Map<String, String> jsonParams = new ArrayMap<>();
            jsonParams.put("id", String.valueOf(id));
            jsonParams.put("idUser", String.valueOf(idUser));
            jsonParams.put("type", type);
            jsonParams.put("lang", OtherSharedMethods.getDefaultLocale());
            jsonParams.put("time", String.valueOf(OtherSharedMethods.getTimeStamp()));

            body = getRequestBody(jsonParams);

            Call<APIResponse> updateDataCall = request.deleteUserData(body);

            updateDataCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    deleteDataListener.afterDataDeletionSuccessfully(response);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    deleteDataListener.afterDataDeletionFailed(t);
                }
            });
        }
    }

    public void deleteUserAccount(int id)
    {

        if(this.deleteUserAccountListener != null)
        {
            Map<String, String> jsonParams = new ArrayMap<>();
            jsonParams.put("id", String.valueOf(id));
            jsonParams.put("lang", OtherSharedMethods.getDefaultLocale());

            body = getRequestBody(jsonParams);

            Call<APIResponse> deleteDataCall = request.deleteUserAccount(body);

            deleteDataCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    deleteUserAccountListener.afterUserAccountDeletionSuccessfully(response);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    deleteUserAccountListener.afterUserAccountDeletionFailed(t);
                }
            });
        }
    }

    public void buyPremium(int idUser, String originalData, String signature)
    {
        if(this.premiumBoughtListener != null)
        {
            Map<String, String> jsonParams = new ArrayMap<>();
            jsonParams.put("idUser", String.valueOf(idUser));
            jsonParams.put("originalData", originalData);
            jsonParams.put("signature", signature);
            jsonParams.put("device", Constants.DEVICE_ANDROID);
            jsonParams.put("lang", OtherSharedMethods.getDefaultLocale());
            jsonParams.put("time", String.valueOf(OtherSharedMethods.getTimeStamp()));

            body = getRequestBody(jsonParams);

            final String premiumDetails = String.valueOf(idUser)+"ç"+originalData+"ç"+signature;

            Call<APIResponse> savePremiumCall = request.postPremium(body);

            savePremiumCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    premiumBoughtListener.afterPremiumBoughtSuccessfully(response, premiumDetails);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    premiumBoughtListener.afterPremiumBoughtFailed(t, premiumDetails);
                }
            });
        }
    }

    public void uploadThumbnailToServer(String title, String thumbnail)
    {
        if(this.afterThumbnailUploadListener != null)
        {
            Map<String, String> jsonParams = new ArrayMap<>();
            jsonParams.put("title", title);
            jsonParams.put("thumb", thumbnail);
            jsonParams.put("lang", OtherSharedMethods.getDefaultLocale());

            body = getRequestBody(jsonParams);

            Call<APIResponse> uploadThumbCall = request.postThumbnail(body);

            uploadThumbCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    afterThumbnailUploadListener.afterThumbnailUploadedSuccessfully(response);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    afterThumbnailUploadListener.afterThumbnailUploadedFailed(t);
                }
            });
        }
    }

    public void getRewardedVideos(int userId, final boolean isSaving, int value)
    {
        if(this.getRewardedVideos != null)
        {
            Call<APIResponse> getDataCall = request.getUserRewardedVideos(userId, isSaving, value, OtherSharedMethods.getDefaultLocale());

            getDataCall.enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response)
                {
                    getRewardedVideos.afterRewardedVideosLoadedSuccessfully(response, isSaving);
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t)
                {
                    getRewardedVideos.afterRewardedVideosLoadFailed(t);
                }
            });
        }
    }

    private Retrofit getRetrofit()
    {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private RequestBody getRequestBody(Map<String, String> jsonParams)
    {
        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                (new JSONObject(jsonParams)).toString());
    }
}
