package kin.backupandrestore.restore.presenter;


import kin.backupandrestore.base.BasePresenter;
import kin.backupandrestore.base.BaseView;

interface BaseChildPresenter<T extends BaseView> extends BasePresenter<T> {

    void onAttach(T view, RestorePresenter restorePresenter);
}
