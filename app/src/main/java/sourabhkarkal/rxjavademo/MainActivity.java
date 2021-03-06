package sourabhkarkal.rxjavademo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import sourabhkarkal.rxjavademo.R;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private LinearLayout rootView;
    private Button startAsyncTaskButton;
    private Button startRxOperationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rootView = (LinearLayout) findViewById(R.id.root_view);

        // AsyncTask operation setup
        startAsyncTaskButton = (Button) findViewById(R.id.start_async_task_btn);
        startAsyncTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                new SampleAsyncTask().execute();
            }
        });

        // RxJava operation setup
        final Observable<String> operationObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(longRunningOperation());
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io()) // subscribeOn the I/O thread
                .observeOn(AndroidSchedulers.mainThread()); // observeOn the UI Thread

        final Observable<String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        sub.onNext("Hello, world!");
                        sub.onCompleted();
                    }
                }
        );




        startRxOperationButton = (Button) findViewById(R.id.start_rxjava_operation_btn);
        startRxOperationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                myObservable.subscribe(new Subscriber<String>() {
                    @Override
                    public void onNext(String s) { System.out.println(s); }

                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) { }
                });
            }
        });
    }

    public String longRunningOperation() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // error
        }
        return "Complete!";
    }

    /**
     * Sample Async Task that does a long running operation, and then posts something to the UI
     */
    private class SampleAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return longRunningOperation();
        }

        @Override
        protected void onPostExecute(String result) {
            Snackbar.make(rootView, result, Snackbar.LENGTH_LONG).show();
            startAsyncTaskButton.setEnabled(true);
        }
    }

}
