package com.b7anka.hollywoodtracker.Helpers;

import android.util.Log;
import com.b7anka.hollywoodtracker.Interfaces.OfflineChangesListener;
import com.b7anka.hollywoodtracker.Model.APIResponse;
import com.b7anka.hollywoodtracker.Model.OfflineChanges;
import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.ViewModels.OfflineChangesViewModel;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import retrofit2.Response;

public class OfflineChangesManager implements RetrofitManager.UpdateDataListener,
        RetrofitManager.DeleteDataListener, RetrofitManager.SaveDataListener,
        RetrofitManager.ReportBugsListener, RetrofitManager.PremiumBoughtListener
{
    private AppCompatActivity activity;
    private RetrofitManager retrofitManager;
    private OfflineChangesViewModel offlineChangesViewModel;
    private SharedPreferencesManager preferencesManager;
    private List<OfflineChanges> offlineChanges;
    private OfflineChangesListener offlineChangesListener;
    private String premiumOffline;
    private int changesCounter;

    public OfflineChangesManager(AppCompatActivity activity)
    {
        this.activity = activity;
        this.preferencesManager = new SharedPreferencesManager(this.activity);
        this.retrofitManager = new RetrofitManager(this, this,
                this, this, this);
        this.offlineChangesViewModel = ViewModelProviders.of(this.activity).get(OfflineChangesViewModel.class);
    }

    public OfflineChangesManager(AppCompatActivity activity, OfflineChangesListener offlineChangesListener)
    {
        this(activity);
        this.offlineChangesListener = offlineChangesListener;
    }

    public void sendChangesToServer()
    {
        offlineChanges = offlineChangesViewModel.getAllOfflineChanges(preferencesManager.getUserId());
        premiumOffline = preferencesManager.getPremiumPurchasedDetails();
        changesCounter = premiumOffline.isEmpty() ? offlineChanges.size() : offlineChanges.size()+1;

        if(offlineChanges.size() > 0)
        {
            for(OfflineChanges of : offlineChanges)
            {
                if(of.getContent().equals(Constants.DELETE_CONTENT))
                {
                    retrofitManager.deleteUserData(of.getId(), preferencesManager.getUserId(), of.getType());
                }
                else
                {
                    if(of.getType().equals(Constants.BUG_REPORT))
                    {
                        String [] temp = of.getContent().split(Constants.SEMI_COLON);
                        retrofitManager.reportBugs(temp);
                    }
                    else
                    {
                        retrofitManager.updateUserData(of.getId(), preferencesManager.getUserId(), of.getType(), of.getContent());
                    }
                }
            }
        }

        if(!premiumOffline.isEmpty())
        {
            String[] temp = premiumOffline.split("ç");
            retrofitManager.buyPremium(Integer.parseInt(temp[0]), temp[1], temp[2]);
            changesCounter--;
        }

        if(offlineChangesListener != null)offlineChangesListener.onOfflineChangesCommunicatedSuccessfully();
    }

    private void deleteOfflineChanges()
    {
        if(changesCounter == 0)
        {
            offlineChangesViewModel.deleteAllOfflineChanges(preferencesManager.getUserId());
            offlineChanges.clear();
        }
    }

    private void checkChangesCounter(Response<APIResponse> response)
    {
        if(response.isSuccessful() && response.body().getSuccess())
        {
            changesCounter--;
        }
        deleteOfflineChanges();
    }

    private void showMessageOnRetrofitFailed(Throwable t)
    {
        if(t.toString().contains("SocketTimeoutException"))
        {
            Log.d(Constants.LOG_TAG, activity.getString(R.string.server_timeout));
        }
    }

    @Override
    public void afterBugReportSentSuccessfully(Response<APIResponse> response)
    {
       checkChangesCounter(response);
    }


    @Override
    public void afterBugReportSentFailure(Throwable t)
    {
        showMessageOnRetrofitFailed(t);
    }

    @Override
    public void afterUpdatedDataSuccess(Response<APIResponse> response)
    {
        checkChangesCounter(response);
    }

    @Override
    public void afterUpdateDataFailed(Throwable t)
    {
        showMessageOnRetrofitFailed(t);
    }

    @Override
    public void afterDataDeletionSuccessfully(Response<APIResponse> response)
    {
        checkChangesCounter(response);
    }

    @Override
    public void afterDataDeletionFailed(Throwable t)
    {
        showMessageOnRetrofitFailed(t);
    }

    @Override
    public void afterDataSavedSuccessfully(Response<APIResponse> response)
    {
        checkChangesCounter(response);
    }

    @Override
    public void afterDataSavedFailed(Throwable t)
    {
        showMessageOnRetrofitFailed(t);
    }

    @Override
    public void afterPremiumBoughtSuccessfully(Response<APIResponse> response, String details)
    {
        if(response.isSuccessful())
        {
            if(response.body().getSuccess())
            {
                preferencesManager.savePremiumPurchasedDetails(Constants.EMPTY);
            }
        }
        checkChangesCounter(response);
    }

    @Override
    public void afterPremiumBoughtFailed(Throwable t, String details)
    {
        showMessageOnRetrofitFailed(t);
    }
}
