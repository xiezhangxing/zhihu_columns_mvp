package jason.xie.columns.defaultcolumns;

import java.util.ArrayList;
import java.util.List;

import jason.xie.columns.ColumnsApplication;
import jason.xie.columns.model.APIService;
import jason.xie.columns.model.Column;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Jason Xie on 2016/6/4.
 */

public class DefaultColumnsPresenter implements DefaultColumnsContract.Presenter {

    private DefaultColumnsContract.View mDefaultColumnsView;
    private String[] mDefaultIds;
    private Subscription mSubscription;
    private ColumnsApplication mApplication;
    private APIService mAPIService;
    private List<Column> mColumns = new ArrayList<>();

    public DefaultColumnsPresenter(DefaultColumnsContract.View defaultColumnsView, String[] defaultIds){
        mDefaultColumnsView = defaultColumnsView;
        mDefaultIds = defaultIds;
        mApplication = ColumnsApplication.get();
        mAPIService = mApplication.getAPIService();
    }

    @Override
    public void loadColumns() {
        mColumns.clear();
        mDefaultColumnsView.showLoading();
        if(mSubscription != null){
            mSubscription.unsubscribe();
        }
        for(String id : mDefaultIds){
            mSubscription = mAPIService.getColumnById(id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(mApplication.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<Column>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Column column) {
                            mColumns.add(column);
                            mDefaultColumnsView.showColumns(mColumns);
                        }
                    });
        }
    }

    @Override
    public void start() {
        loadColumns();
    }

    @Override
    public void stop(){
        mDefaultColumnsView = null;
        if(mSubscription != null){
            mSubscription.unsubscribe();
        }
    }
}