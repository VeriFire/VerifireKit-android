package io.verifire.sample;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.verifire.sdk.Verifire;
import io.verifire.sdk.VerifireCallback;
import io.verifire.sdk.VerifireException;
import io.verifire.sdk.api.VerifireRequest;


public class MainActivity extends AppCompatActivity implements VerifireCallback {

    public static final String TAG = "VerifireSample";

    @BindView(R.id.enter_phone_dialog) View enterPhoneView;
    @BindView(R.id.enter_code_dialog) View enterCodeView;
    @BindView(R.id.phone_number_edit) TextView phoneEdit;
    @BindView(R.id.code_edit) TextView codeEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Initialize Verifire instance (key from Account on https://verifire.io)
        Verifire.initialize(this, getString(R.string.verifire_key));

    }

    @OnClick(R.id.verify_by_call_btn) public void onCallClick() {
        Verifire.getInstance().verifyNumber(phoneEdit.getText().toString(), Verifire.Method.call, this);
    }

    @OnClick(R.id.verify_by_sms_btn) public void onSMSClick() {
        Verifire.getInstance().verifyNumber(phoneEdit.getText().toString(), Verifire.Method.sms, this);
    }

    @OnClick(R.id.verify_by_voice_btn) public void onVoiceClick() {
        Verifire.getInstance().verifyNumber(phoneEdit.getText().toString(), Verifire.Method.voice, this);
    }

    @Override public void onCodeSent(VerifireRequest request) {
        Log.d(TAG, "Code sent, " + request);
        showEnterCodeView();
    }

    @OnClick(R.id.check_btn) public void onCheckClick() {
        Verifire.getInstance().check(codeEdit.getText().toString(), this);
    }

    @Override public void onVerificationCompleted(VerifireRequest request) {
        Log.d(TAG, "onVerificationCompleted: "+request.getId());

        // you should complete registration on your backend side
        // use API method POST /v1/verify/info
        // see https://verifire.io/api.html

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.verification_completed, request.getNumber()))
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, null)
                .setOnDismissListener(dialogInterface -> showEnterPhoneView()).create().show();
    }

    @Override public void onVerificationFailed(VerifireException e) {
        switch (e.getErrorCode()) {
            case VerifireException.INVALID_NUMBER:
                Toast.makeText(this, R.string.error_wrong_number, Toast.LENGTH_LONG).show();
                break;
            case VerifireException.INVALID_CODE:
                Toast.makeText(this, R.string.error_wrong_code, Toast.LENGTH_LONG).show();
                break;
            default:
                Log.e(TAG, "Verification failed with error: " + e);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override public void onConnectionError(IOException e) {
        Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
    }

    private void showEnterCodeView() {
        codeEdit.setText(null);
        enterPhoneView.setVisibility(View.GONE);
        enterCodeView.setVisibility(View.VISIBLE);
    }

    private void showEnterPhoneView() {
        enterPhoneView.setVisibility(View.VISIBLE);
        enterCodeView.setVisibility(View.GONE);
    }

    public void onBackPressed() {
        if (enterCodeView.getVisibility() == View.VISIBLE) {
            showEnterPhoneView();
        } else {
            super.onBackPressed();
        }
    }
}
