package com.example.phoneauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    String newPhone="";

    FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    TextView phone_no;
    Button signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth=FirebaseAuth.getInstance();



         phone_no=findViewById(R.id.id_phone);
         signUp=findViewById(R.id.id_signup);




        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  String phoneno=phone_no.getText().toString().trim();

                Log.i("MasterOne","Send Code pressed");

                 sendCode();


            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {


        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("MasterOne", "signInWithCredential:success");

                            Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
                            startActivity(intent);

                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.i("MasterOne", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Log.i("MasterOne", "signInWithCredential:failure", task.getException());
                            }
                        }
                    }
                });
    }
    public void sendCode(){


        newPhone=phone_no.getText().toString().trim().replaceFirst("07","+2547");

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.i("MasterOne", "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);

            }


            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Log.i("MasterOne", "Time out Error");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.i("MasterOne", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    // Invalid request
                    Log.i("MasterOne", "onVerificationFailed-fidelme"+e.getLocalizedMessage());
                    Toast.makeText(MainActivity.this,"Invalid phone number",Toast.LENGTH_SHORT).show();

                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...

                    Toast.makeText(MainActivity.this,"Error is "+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                    Log.i("MasterOne", "onVerificationFailed "+e.getLocalizedMessage());
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
             /*   Log.d("MasterMind", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
*/
                // ...
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                newPhone,        // Phone number to verify
                5,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }



    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser()!=null){
            Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(intent);

        }
    }
}
