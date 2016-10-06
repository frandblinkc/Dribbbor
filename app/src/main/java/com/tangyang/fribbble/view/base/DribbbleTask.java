package com.tangyang.fribbble.view.base;

import android.os.AsyncTask;

import com.tangyang.fribbble.dribbble.DribbbleException;

/**
 * Created by YangTang on 10/6/2016.
 */
public abstract class DribbbleTask<Params, Progress, Result>
        extends AsyncTask<Params, Progress, Result> {

    private DribbbleException exception;

    protected abstract Result doJob(Params... params) throws DribbbleException;

    protected abstract void onSuccess(Result result);

    protected abstract void onFailure(DribbbleException e);

    @Override
    protected Result doInBackground(Params... params) {
        try {
            return doJob(params);
        } catch (DribbbleException e) {
            e.printStackTrace();
            exception = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        if (exception != null) {
            onFailure(exception);
        } else {
            onSuccess(result);
        }
    }
}
