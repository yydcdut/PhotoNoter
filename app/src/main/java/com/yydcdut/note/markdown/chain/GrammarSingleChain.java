package com.yydcdut.note.markdown.chain;

import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;

import com.yydcdut.note.markdown.grammar.IGrammar;

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
            mGrammar.format(ssb);
//            YLog.i("yuyidong", "处理--->" + mGrammar.toString() + "  ssb--->" + ssb.toString());
            return true;
        } else {
            if (mNextHandleGrammar != null) {
//                YLog.i("yuyidong", getClass().getSimpleName() + "   " + mGrammar.getClass().getSimpleName() + "  无法处理");
                return mNextHandleGrammar.handleGrammar(ssb);
            } else {
//                YLog.e("yuyidong", "责任链中没有下一任了,这个责任链是--->" + toString());
                return false;
            }
        }
    }

    @Override
    @Deprecated
    public boolean addNextHandleGrammar(@Nullable IResponsibilityChain nextHandleGrammar) {
        return false;
    }

    @Override
    public boolean setNextHandleGrammar(@Nullable IResponsibilityChain nextHandleGrammar) {
        mNextHandleGrammar = nextHandleGrammar;
        return true;
    }

    @Override
    public String toString() {
        return "GrammarSingleChain{" +
                "mGrammar=" + mGrammar +
                ", mNextHandleGrammar=" + mNextHandleGrammar +
                '}';
    }
}
