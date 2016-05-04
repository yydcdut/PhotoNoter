package com.yydcdut.note.markdown.chain;

import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;

import com.yydcdut.note.markdown.grammar.IGrammar;
import com.yydcdut.note.utils.YLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 16/5/4.
 */
public class GrammarMultiChains implements IResponsibilityChain {
    private IGrammar mGrammar;

    private List<IResponsibilityChain> mNextHandleGrammarList = null;

    public GrammarMultiChains(@Nullable IGrammar grammar) {
        mGrammar = grammar;
    }

    @Nullable
    @Override
    public boolean handleGrammar(@Nullable SpannableStringBuilder ssb) {
        if (mGrammar.isMatch(ssb.toString())) {
            mGrammar.format(ssb);
            YLog.i("yuyidong", "处理--->" + mGrammar.toString() + "  ssb--->" + ssb.toString());
        }
        if (mNextHandleGrammarList != null) {
            boolean handled = false;
            for (IResponsibilityChain responsibilityChain : mNextHandleGrammarList) {
                handled |= responsibilityChain.handleGrammar(ssb);
            }
            return handled;
        } else {
            YLog.e("yuyidong", "责任链中没有下一任了,这个责任链是--->" + toString());
            return false;
        }
    }

    @Override
    public boolean addNextHandleGrammar(@Nullable IResponsibilityChain nextHandleGrammar) {
        if (mNextHandleGrammarList == null) {
            mNextHandleGrammarList = new ArrayList<>();
        }
        mNextHandleGrammarList.add(nextHandleGrammar);
        return true;
    }

    @Override
    @Deprecated
    public boolean setNextHandleGrammar(@Nullable IResponsibilityChain nextHandleGrammar) {
        return false;
    }

    @Override
    public String toString() {
        return "GrammarMultiChains{" +
                "mGrammar=" + mGrammar +
                ", mNextHandleGrammarList=" + mNextHandleGrammarList +
                '}';
    }
}
