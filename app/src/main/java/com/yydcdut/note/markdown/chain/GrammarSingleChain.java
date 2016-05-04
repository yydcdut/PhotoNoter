package com.yydcdut.note.markdown.chain;

import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;

import com.yydcdut.note.markdown.grammar.IGrammar;
import com.yydcdut.note.utils.YLog;

/**
 * Created by yuyidong on 16/5/4.
 */
public class GrammarSingleChain implements IResponsibilityChain {
    private IGrammar mGrammar;

    private IResponsibilityChain mNextHandleGrammar = null;

    public GrammarSingleChain(@Nullable IGrammar grammar) {
        mGrammar = grammar;
    }

    @Nullable
    @Override
    public boolean handleGrammar(@Nullable SpannableStringBuilder ssb) {
        if (mGrammar.isMatch(ssb.toString())) {
            return true;
        } else {
            if (mNextHandleGrammar != null) {
                return mNextHandleGrammar.handleGrammar(ssb);
            } else {
                YLog.e("yuyidong", "责任链中没有下一任了");
                return false;
            }
        }
    }

    @Override
    public boolean addNextHandleGrammar(@Nullable IResponsibilityChain nextHandleGrammar) {
        mNextHandleGrammar = nextHandleGrammar;
        return true;
    }

    @Override
    public boolean setNextHandleGrammar(@Nullable IResponsibilityChain nextHandleGrammar) {
        return false;
    }


}
