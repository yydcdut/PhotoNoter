package com.yydcdut.note.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yydcdut.note.R;
import com.yydcdut.note.markdown.MarkdownParser;

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
        mEditText.setText(
                "# 一级标题\n" + "");
//                "## 二级标题\n" +
//                "### 三级标题\n" +
//                "\n" +
//                "### **加粗功能**\n" +
//                "使用一组星号“**”来加粗一段文字\n" +
//                "\n" +
//                "例如：\n" +
//                "这是**加粗的文字**\n" +
//                "\n" +
//                "### **居中**\n" +
//                "使用一对中括号“[文字]”来居中一段文字，也可以和标题叠加使用\n" +
//                "\n" +
//                "例如：\n" +
//                "[### 这是一个居中的标题]\n" +
//                "\n" +
//                "### **引用**\n" +
//                "使用“>”在段首来引用一段文字\n" +
//                "\n" +
//                "例如：\n" +
//                "   > [**这是一段引用**]\n" +
//                "> 这是一段引用\n" +
//                "\n" +
//                "### **无序列表**\n" +
//                "使用“-”、“*”或“+”加空格来创建无序列表\n" +
//                "\n" +
//                "例如：\n" +
//                "- 这是一个无序列表\n" +
//                "+ 这是一个无序列表\n" +
//                "* 这是一个无序列表\n" +
//                "\n" +
//                "### **有序列表**\n" +
//                "使用数字圆点加空格如“1. ”、“2. ”来创建有序列表\n" +
//                "\n" +
//                "例如：\n" +
//                "3. 这是一个有序列表\n" +
//                "2. 这是一个有序列表\n" +
//                "3. 这是一个有序列表\n" +
//                "\n" +
//                "\n" +
//                "111**2222**3333**444444**55555 ");
    }

    @Override
    public void onClick(View v) {
        String text = mEditText.getText().toString();
        MarkdownParser.parse(mTextView, text);


//        String[] lines = text.split("\n");
//        SpannableStringBuilder ssb = new SpannableStringBuilder();
//        IGrammar iGrammar = GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_HEADER_LINE_1);
//        for (int i = 0; i < lines.length; i++) {
//            boolean b = iGrammar.isMatch(lines[i]);
//            YLog.i("yuyidong", "b---->" + b);
//            ssb.append("\n");
//            ssb.append(iGrammar.format(new SpannableStringBuilder(lines[i])));
//        }
//        mTextView.setText(ssb, TextView.BufferType.SPANNABLE);
//        YLog.i("yuyidong", "ssb--->" + ssb.length() + "   ssb.toDS--->" + ssb.toString().length());

//        String[] lines = text.split("\\n");
//        for (int i = 0; i < lines.length; i++) {
//            YLog.i(TAG, lines[i] + "    xxxx");
//        }
    }
}
