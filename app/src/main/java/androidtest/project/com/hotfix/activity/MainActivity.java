package androidtest.project.com.hotfix.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import androidtest.project.com.hotfix.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void AndFix(View view) {
        startActivity(new Intent(MainActivity.this, AndFixActivity.class));
    }

    public void Tinker(View view) {
        startActivity(new Intent(MainActivity.this, TinkerActivity.class));
    }

}
