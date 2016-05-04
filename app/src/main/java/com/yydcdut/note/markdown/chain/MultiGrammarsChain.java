package com.yydcdut.note.markdown.chain;

import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;

import com.yydcdut.note.markdown.grammar.IGrammar;
import com.yydcdut.note.utils.YLog;

/**
 * Created by yuyidong on 16/5/4.
 */
public class MultiGrammarsChain implements IResponsibilityChain {
    private IGrammar[] mGrammars;

    private IResponsibilityChain mNextHandleGrammar = null;

    public MultiGrammarsChain(@Nullable IGrammar... grammars) {
        mGrammars = grammars;
    }

    @Nullable
    @Override
    public boolean handleGrammar(@Nullable SpannableStringBuilder ssb) {
        boolean handled = false;
        for (IGrammar iGrammar : mGrammars) {
            if (iGrammar.isMatch(ssb.toString())) {
                ssb = iGrammar.format(ssb);
                handled |= true;
            }
        }
        if (handled) {
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
        return false;
    }

    @Override
    public boolean setNextHandleGrammar(@Nullable IResponsibilityChain nextHandleGrammar) {
        mNextHandleGrammar = nextHandleGrammar;
        return true;
    }
}
