package kin.backupandrestore.base;

import androidx.annotation.CallSuper;

public abstract class BasePresenterImpl<T extends BaseView> implements BasePresenter<T> {

    protected T view;

    @CallSuper
    @Override
    public void onAttach(T view) {
        this.view = view;

    }

    @CallSuper
    @Override
    public void onDetach() {
        view = null;
    }

    @Override
    public T getView() {
        return view;
    }

}
