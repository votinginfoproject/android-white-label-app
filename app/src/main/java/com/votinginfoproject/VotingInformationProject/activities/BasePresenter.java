package com.votinginfoproject.VotingInformationProject.activities;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

/**
 * Created by marcvandehey on 4/4/16.
 */
public abstract class BasePresenter<InteractiveView> {
    private InteractiveView view;

    public abstract void onCreate(Bundle savedState);

    @CallSuper
    public void onAttachView(InteractiveView view) {
        this.view = view;
    }

    @CallSuper
    public void onDetachView() {
        this.view = null;
    }

    public abstract void onSaveState(@NonNull Bundle state);

    public abstract void onDestroy();

    public InteractiveView getView() {
        return view;
    }

    public void setView(InteractiveView view) {
        this.view = view;
    }
}
