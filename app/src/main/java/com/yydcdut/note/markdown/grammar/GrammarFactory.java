package com.yydcdut.note.markdown.grammar;

/**
 * Created by yuyidong on 16/5/4.
 */
public class GrammarFactory {
    public static final int GRAMMAR_BLOCK_QUOTES = 0;
    public static final int GRAMMAR_ORDER_LIST = 1;
    public static final int GRAMMAR_UNORDER_LIST = 2;
    public static final int GRAMMAR_CENTER_ALIGN = 10;
    public static final int GRAMMAR_HEADER_LINE_1 = 11;
    public static final int GRAMMAR_HEADER_LINE_2 = 12;
    public static final int GRAMMAR_HEADER_LINE_3 = 13;
    public static final int GRAMMAR_BOLD = 14;
    public static final int GRAMMAR_ITALIC = 15;

    public static IGrammar getGrammar(int grammar) {
        switch (grammar) {
            case GRAMMAR_BLOCK_QUOTES:
                return new BlockQuotesGrammar();
            case GRAMMAR_ORDER_LIST:
                return new OrderListGrammar();
            case GRAMMAR_UNORDER_LIST:
                return new UnOrderListGrammar();
            case GRAMMAR_CENTER_ALIGN:
                return new CenterAlignGrammar();
            case GRAMMAR_HEADER_LINE_1:
                return new HeadLine1Grammar();
            case GRAMMAR_HEADER_LINE_2:
                return new HeadLine2Grammar();
            case GRAMMAR_HEADER_LINE_3:
                return new HeadLine3Grammar();
            case GRAMMAR_BOLD:
                return new BoldGrammar();
            case GRAMMAR_ITALIC:
                return new ItalicGrammar();
            default:
                return new NormalGrammar();
        }
    }
}
