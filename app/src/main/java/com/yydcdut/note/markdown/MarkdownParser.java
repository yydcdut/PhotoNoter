package com.yydcdut.note.markdown;

import android.text.SpannableStringBuilder;
import android.widget.TextView;

import com.yydcdut.note.markdown.chain.GrammarSingleChain;
import com.yydcdut.note.markdown.chain.IResponsibilityChain;
import com.yydcdut.note.markdown.chain.MultiGrammarsChain;
import com.yydcdut.note.markdown.grammar.GrammarFactory;

/**
 * Created by yuyidong on 16/5/3.
 */
public class MarkdownParser {

    public static void parse(TextView textView, String content) {
        IResponsibilityChain chain = initChain();
        String[] lines = content.split("\n");
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (String line : lines) {
            SpannableStringBuilder lineSSB = new SpannableStringBuilder(line);
            chain.handleGrammar(lineSSB);
            ssb.append(lineSSB + "\n");
        }
        textView.setText(ssb, TextView.BufferType.SPANNABLE);
    }

    private static IResponsibilityChain initChain() {
        IResponsibilityChain blockQuotesChain = new GrammarSingleChain(GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_BLOCK_QUOTES));
        IResponsibilityChain orderListChain = new GrammarSingleChain(GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_ORDER_LIST));
        IResponsibilityChain unOrderListChain = new GrammarSingleChain(GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_UNORDER_LIST));
        IResponsibilityChain centerAlignChain = new GrammarSingleChain(GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_CENTER_ALIGN));
        IResponsibilityChain headerLine3Chain = new GrammarSingleChain(GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_HEADER_LINE_3));
        IResponsibilityChain headerLine2Chain = new GrammarSingleChain(GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_HEADER_LINE_2));
        IResponsibilityChain headerLine1Chain = new GrammarSingleChain(GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_HEADER_LINE_1));
        IResponsibilityChain multiChain = new MultiGrammarsChain(
                GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_BOLD),
                GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_ITALIC));
        blockQuotesChain.setNextHandleGrammar(orderListChain);
        orderListChain.setNextHandleGrammar(unOrderListChain);
        unOrderListChain.setNextHandleGrammar(centerAlignChain);
        centerAlignChain.setNextHandleGrammar(headerLine3Chain);
        headerLine3Chain.setNextHandleGrammar(headerLine2Chain);
        headerLine2Chain.setNextHandleGrammar(headerLine1Chain);
        headerLine1Chain.addNextHandleGrammar(multiChain);
        return blockQuotesChain;
    }


}
