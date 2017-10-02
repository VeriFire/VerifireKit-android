# VerifireKit-android

Verifire API allows you to verify that a phone number is valid, reachable, and accessible by your user.

## Installation

Add  [verifire-android-sdk-1.08.jar](sample/libs/verifire-android-sdk-1.08.jar)
to `libs` folder of your app.

Add the okhttp3 and gson dependencies to your app's `build.gradle` file
with actual versions:
```groovy
dependencies {
compile 'com.squareup.okhttp3:okhttp:3.9.0'
compile 'com.google.code.gson:gson:2.8.1'
}
```

## Permissions

Default permissions:
```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

Optional permissions (for automatically receive verification SMS/Call):

```xml
<uses-permission android:name="android.permission.RECEIVE_SMS"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
```

## Using

Implement VerificationCallback in your Activity or Presenter class like this:

```java
public class MainActivity extends AppCompatActivity implements VerifireCallback {

protected void onCreate(Bundle savedInstanceState) {

//Initialization
Verifire.initialize(this,"<verifire api key>");

//Send verification SMS to number
findViewById(R.id.verify_btn).setOnClickListener(view ->
Verifire.getInstance().verifyNumber("<user phone number>", Verifire.Method.sms, this)
);

//Check entered verification code
findViewById(R.id.check_btn).setOnClickListener(view ->
Verifire.getInstance().check("<code>", this)
);
}

@Override
public void onVerificationCompleted(VerifireRequest request) {
// you should complete registration on your backend side
// use API method POST /v1/verify/info
// see https://verifire.io/api.html
}

@Override
public void onVerificationFailed(VerifireException e) {
switch (e.getErrorCode()) {
case VerifireException.INVALID_NUMBER:
Toast.makeText(this, R.string.error_wrong_number, Toast.LENGTH_LONG).show();
break;
case VerifireException.INVALID_CODE:
Toast.makeText(this, R.string.error_wrong_code, Toast.LENGTH_LONG).show();
break;

// other cases...
}
}
}
```

## Example App
An example app is provided [here](sample) that shows a simple integration in one Activity flow
