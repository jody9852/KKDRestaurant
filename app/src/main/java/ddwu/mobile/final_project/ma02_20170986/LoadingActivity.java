package ddwu.mobile.final_project.ma02_20170986;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoadingActivity extends Activity {
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
