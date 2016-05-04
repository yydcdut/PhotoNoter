package com.yydcdut.note.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yydcdut.note.R;
import com.yydcdut.note.markdown.grammar.BoldGrammar;
import com.yydcdut.note.markdown.grammar.IGrammar;
import com.yydcdut.note.utils.YLog;

/**
 * Created by yuyidong on 16/4/29.
 */
public class TestUIActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mEditText;
    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ui);
        findViewById(R.id.btn_test).setOnClickListener(this);
        mEditText = (EditText) findViewById(R.id.edit_test);
        mTextView = (TextView) findViewById(R.id.txt_test);
        mEditText.setText("33**11**22");
    }

    @Override
    public void onClick(View v) {
        String text = mEditText.getText().toString();
        String[] lines = text.split("\n");
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        IGrammar iGrammar = new BoldGrammar();
        for (int i = 0; i < lines.length; i++) {
            boolean b = iGrammar.isMatch(lines[i]);
            YLog.i("yuyidong", "b---->" + b);
            ssb.append("\n");
            ssb.append(iGrammar.format(lines[i]));
        }
        mTextView.setText(ssb, TextView.BufferType.SPANNABLE);
        YLog.i("yuyidong", "ssb--->" + ssb.length() + "   ssb.toDS--->" + ssb.toString().length());

//        String[] lines = text.split("\\n");
//        for (int i = 0; i < lines.length; i++) {
//            YLog.i(TAG, lines[i] + "    xxxx");
//        }
    }
}
