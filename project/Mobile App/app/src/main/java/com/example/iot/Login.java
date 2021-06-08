package com.example.iot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    private CountryCodePicker ccp;
    private EditText phonetext;
    private EditText codetext;
    private Button ctnandnextbtn;
    private String checker="",phoneNumber ="";
    private RelativeLayout relativeLayout;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        phonetext = findViewById(R.id.phoneText);
        codetext = findViewById(R.id.codeText);
        ctnandnextbtn = findViewById(R.id.continueNextButton);
        relativeLayout = findViewById(R.id.phoneAuth);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phonetext);

        ctnandnextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ctnandnextbtn.getText().equals("Đồng ý") || checker.equals("Mã đã gửi"))
                {
                    String verificationCode = codetext.getText().toString();
                    if (verificationCode.equals(""))
                    {
                        Toast.makeText(Login.this, "Hãy điền mã xác thực", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        loadingBar.setTitle("Xác thực mã");
                        loadingBar.setMessage("Hãy chờ chúng tôi xác thực mã của bạn");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }
                }
                else
                {
                    phoneNumber = ccp.getFullNumberWithPlus();
                    //nếu số điện thoại không bằng null
                    if (!phoneNumber.equals(""))
                    {
                        loadingBar.setTitle("Đang xác thực số điện thoại");
                        loadingBar.setMessage("Phiền bạn chờ một chút");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        PhoneAuthOptions options =
                                PhoneAuthOptions.newBuilder(mAuth)
                                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(Login.this)                 // Activity (for callback binding)
                                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                        .build();
                        PhoneAuthProvider.verifyPhoneNumber(options);
                    }
                    else
                    {
                        Toast.makeText(Login.this, "Hãy điền số điện thoại cần xác nhận", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                Toast.makeText(Login.this, "Số điện thoại không đúng...", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                relativeLayout.setVisibility(View.VISIBLE);

                ctnandnextbtn.setText("Tiếp tục");
                codetext.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerificationId = s;
                mResendToken = forceResendingToken;
                relativeLayout.setVisibility(View.GONE);
                checker = "Mã đã gửi";
                ctnandnextbtn.setText("Đồng ý");
                codetext.setVisibility(View.VISIBLE);

                loadingBar.dismiss();
                Toast.makeText(Login.this, "Mã đã gửi thành công, hãy kiểm tra", Toast.LENGTH_SHORT).show();
            }
        };
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new  OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            String phone = mAuth.getCurrentUser().getPhoneNumber();
                            Toast.makeText(Login.this, "Đăng nhập thành công với"+phone, Toast.LENGTH_SHORT).show();
                            Manhinhchinh();
                        } else
                            {
                                loadingBar.dismiss();
                                String e = task.getException().toString();
                                Toast.makeText(Login.this, "Lỗi" + e, Toast.LENGTH_SHORT).show();
                            }
                    }
                });
    }

    private void Manhinhchinh()
    {
        Intent intent = new Intent(Login.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
