package com.yydcdut.note.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yydcdut.note.R;
import com.yydcdut.note.markdown.grammar.BlockQuotesGrammar;
import com.yydcdut.note.markdown.grammar.IGrammar;

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
        mEditText.setText("> 11111]\n> 2222222\n> 33333333");
        mTextView = (TextView) findViewById(R.id.txt_test);
    }

    @Override
    public void onClick(View v) {
        String text = mEditText.getText().toString();
        String[] lines = text.split("\n");
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        IGrammar iGrammar = new BlockQuotesGrammar();
        for (int i = 0; i < lines.length; i++) {
            ssb.append("\n");
            ssb.append(iGrammar.format(lines[i]));
        }
        mTextView.setText(ssb, TextView.BufferType.SPANNABLE);

//        String[] lines = text.split("\\n");
//        for (int i = 0; i < lines.length; i++) {
//            YLog.i(TAG, lines[i] + "    xxxx");
//        }
    }
}
