package kr.ive.ive_offerwall_sdk_sample;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import kr.ive.offerwall_sdk.IveOfferwall;
import kr.ive.offerwall_sdk.IveOfferwallStyle;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IveOfferwall.GetPointListener, IveOfferwall.UsePointListener, AdapterView.OnItemSelectedListener {
    public static final String TAG = "MainActivity";

    private static final String PREF_NAME = "pref_name";
    private static final String PREF_KEY_SERVER_TYPE_INDEX = "server_type_index";
    private static final String PREF_KEY_LIST_TYPE_INDEX = "list_type_index";

    private TextView mPointTextView;
    private String mTransactionKey;

    private String mType;


    private interface OnNumberInputDialogListener {
        void onNumberInput(int number);
    }

    private EditText mIdEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIdEditText = (EditText) findViewById(R.id.id_edittext);
        mIdEditText.setText(loadId());

        mPointTextView = ((TextView) findViewById(R.id.point_textview));

        Button openOfferwallActivityButton = (Button) findViewById(R.id.open_offerwall_activity_button);
        openOfferwallActivityButton.setOnClickListener(this);

        Button openOfferwallFragmentButton = (Button) findViewById(R.id.open_offerwall_fragment_button);
        openOfferwallFragmentButton.setOnClickListener(this);

        Button getUserPointButton = (Button) findViewById(R.id.get_user_point_button);
        getUserPointButton.setOnClickListener(this);

        Button useUserPointButton = (Button) findViewById(R.id.use_user_point_button);
        useUserPointButton.setOnClickListener(this);

        Spinner typeSpinner = (Spinner) findViewById(R.id.type_spinner);
        initSpinner(typeSpinner, R.array.types_array);
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int selectedListTypeIndex = sharedPreferences.getInt(PREF_KEY_LIST_TYPE_INDEX, 0);
        typeSpinner.setSelection(selectedListTypeIndex);
    }

    private void initSpinner(Spinner spinner, int stringArrayId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                stringArrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "onActivityResult() requestCode = " + requestCode);
    }

    private void saveId() {
        SharedPreferences pref = getPref();
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id", getId());
        editor.apply();
    }

    private String loadId() {
        SharedPreferences pref = getPref();
        return pref.getString("id", "");
    }

    private SharedPreferences getPref() {
        return getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveId();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_offerwall_activity_button:
                openOfferwallActivity();
                break;
            case R.id.open_offerwall_fragment_button:
                openOfferwallFragment();
                break;
            case R.id.get_user_point_button:
                getUserPoint();
                break;
            case R.id.use_user_point_button:
                useUsePoint();
                break;
        }
    }

    private void openOfferwallActivity() {
        String id = getId();
        if(checkValidId(id)) {
            IveOfferwallStyle style = makeStyle();

            IveOfferwall.openActivityForResult(this, id, 100, style);
        }
    }

    private void openOfferwallFragment() {
        String id = getId();
        if(checkValidId(id)) {
            IveOfferwallStyle style = makeStyle();

            setFragment(IveOfferwall.createFragment(this, id, style));
        }
    }

    @NonNull
    private IveOfferwallStyle makeStyle() {
        IveOfferwallStyle style = new IveOfferwallStyle();
        style.setColor(IveOfferwallStyle.Color.STATUS_BAR, Color.parseColor("#dddddd"));
        style.setColor(IveOfferwallStyle.Color.TOOL_BAR_BG, ContextCompat.getColor(this, android.R.color.white));
        style.setColor(IveOfferwallStyle.Color.TOOL_BAR_TEXT, ContextCompat.getColor(this, android.R.color.black));
        style.setColor(IveOfferwallStyle.Color.BUTTON_BG, Color.parseColor("#ff31aa"));
        style.setColor(IveOfferwallStyle.Color.BUTTON_TEXT, ContextCompat.getColor(this, android.R.color.white));
        style.setColor(IveOfferwallStyle.Color.ACCENT_TEXT, Color.parseColor("#ff31aa"));
        style.setType(mType);
        return style;
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content, fragment);
        fragmentTransaction.commit();
    }

    private void getUserPoint() {
        String id = getId();
        if(checkValidId(id)) {
            mTransactionKey = IveOfferwall.getPoint(this, id, this);
        }
    }

    private void useUsePoint() {
        final String id = getId();
        if(checkValidId(id)) {
            showNumberInputDialog("사용할 포인트를 입력해주세요.", new OnNumberInputDialogListener() {
                @Override
                public void onNumberInput(int number) {
                    mTransactionKey = IveOfferwall.usePoint(MainActivity.this, id, number, MainActivity.this);
                }
            });
        }
    }

    private boolean checkValidId(String id) {
        boolean isValid = !TextUtils.isEmpty(id);

        if(!isValid) {
            showAlertDialog("ID를 입력해 주세요.", null);
        }

        return isValid;
    }

    private String getId() {
        return mIdEditText.getText().toString();
    }

    private void showAlertDialog(String message, DialogInterface.OnClickListener onOkClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setPositiveButton("확인", onOkClickListener);

        builder.create().show();
    }

    private void showNumberInputDialog(String message, final OnNumberInputDialogListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setMessage(message);
        builder.setView(editText);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    int number = Integer.parseInt(editText.getText().toString());
                    if(listener != null)
                        listener.onNumberInput(number);
                } catch (NumberFormatException e) {
                    Log.w(TAG, e.getMessage());
                }
            }
        });

        builder.create().show();
    }

    @Override
    public void onGetPointComplete(boolean isSuccess, long point, String errorMessage, String hash) {
        boolean isValid = IveOfferwall.isValidTransaction(getId(), point, mTransactionKey, hash);
        if(isValid) {
            setPoint(point);
        } else {
            setPoint(0);
            showAlertDialog("트랜잭션이 유효하지 않습니다.", null);
        }
    }

    @Override
    public void onUsePointComplete(boolean isSuccess, long remainPoint, String errorMessage, String hash) {
        boolean isValid = IveOfferwall.isValidTransaction(getId(), remainPoint, mTransactionKey, hash);
        if(isSuccess) {
            if(isValid) {
                setPoint(remainPoint);
            } else {
                setPoint(0);
                showAlertDialog("트랜잭션이 유효하지 않습니다.", null);
            }
        } else {
            showAlertDialog(errorMessage, null);
        }
    }

    private void setPoint(long point) {
        mPointTextView.setText(String.valueOf(point));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int viewId = parent.getId();
        if(viewId == R.id.type_spinner) {
            onTypeSpinnerSelected(position);
        }
    }

    private void onTypeSpinnerSelected(int position) {
        if(position == 0) { //NORMAL
            mType = IveOfferwallStyle.Type.NORMAL;
        } else if(position == 1) {  //BIG
            mType = IveOfferwallStyle.Type.BIG;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(PREF_KEY_LIST_TYPE_INDEX, position);
        edit.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}