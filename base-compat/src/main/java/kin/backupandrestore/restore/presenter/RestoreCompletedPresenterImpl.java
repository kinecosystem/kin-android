package kin.backupandrestore.restore.presenter;

import kin.backupandrestore.restore.view.RestoreCompletedView;

public class RestoreCompletedPresenterImpl extends BaseChildPresenterImpl<RestoreCompletedView> implements
        RestoreCompletedPresenter {

    public RestoreCompletedPresenterImpl() {
    }

    @Override
    public void onBackClicked() {
        getParentPresenter().previousStep();
    }

    @Override
    public void close() {
        getParentPresenter().closeFlow();
    }

}
