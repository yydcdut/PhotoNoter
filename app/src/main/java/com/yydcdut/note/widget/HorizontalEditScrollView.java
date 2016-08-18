package com.yydcdut.note.widget;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.yydcdut.note.R;
import com.yydcdut.note.markdown.BlockQuotesController;
import com.yydcdut.note.markdown.CenterAlignController;
import com.yydcdut.note.markdown.CodeController;
import com.yydcdut.note.markdown.HeaderController;
import com.yydcdut.note.markdown.HorizontalRulesController;
import com.yydcdut.note.markdown.ImageController;
import com.yydcdut.note.markdown.LinkController;
import com.yydcdut.note.markdown.ListController;
import com.yydcdut.note.markdown.StrikeThroughController;
import com.yydcdut.note.markdown.StyleController;
import com.yydcdut.note.markdown.TodoController;
import com.yydcdut.rxmarkdown.RxMDConfiguration;
import com.yydcdut.rxmarkdown.RxMDEditText;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by yuyidong on 16/8/17.
 */
public class HorizontalEditScrollView extends FrameLayout {
    private RxMDEditText mRxMDEditText;

    private HeaderController mHeaderController;
    private StyleController mStyleController;
    private CenterAlignController mCenterAlignController;
    private HorizontalRulesController mHorizontalRulesController;
    private TodoController mTodoController;
    private StrikeThroughController mStrikeThroughController;
    private CodeController mCodeController;
    private BlockQuotesController mBlockQuotesController;
    private ListController mListController;
    private ImageController mImageController;
    private LinkController mLinkController;

    public HorizontalEditScrollView(Context context) {
        this(context, null);
    }

    public HorizontalEditScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalEditScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_horizontal_scroll, this, true);
        ButterKnife.bind(view, this);
    }

    public void setEditTextAndConfig(@NonNull RxMDEditText rxMDEditText,
                                     @NonNull RxMDConfiguration rxMDConfiguration) {
        mRxMDEditText = rxMDEditText;
        mHeaderController = new HeaderController(rxMDEditText, rxMDConfiguration);
        mStyleController = new StyleController(rxMDEditText, rxMDConfiguration);
        mCenterAlignController = new CenterAlignController(rxMDEditText, rxMDConfiguration);
        mHorizontalRulesController = new HorizontalRulesController(rxMDEditText, rxMDConfiguration);
        mTodoController = new TodoController(rxMDEditText, rxMDConfiguration);
        mStrikeThroughController = new StrikeThroughController(rxMDEditText, rxMDConfiguration);
        mCodeController = new CodeController(rxMDEditText, rxMDConfiguration);
        mBlockQuotesController = new BlockQuotesController(rxMDEditText, rxMDConfiguration);
        mListController = new ListController(rxMDEditText, rxMDConfiguration);
        mImageController = new ImageController(rxMDEditText, rxMDConfiguration);
        mLinkController = new LinkController(rxMDEditText, rxMDConfiguration);
    }

    @OnClick(R.id.img_header1)
    public void header1Click(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mHeaderController.doHeader(1);
    }

    @OnClick(R.id.img_header2)
    public void header2Click(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mHeaderController.doHeader(2);
    }

    @OnClick(R.id.img_header3)
    public void header3Click(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mHeaderController.doHeader(3);
    }

    @OnClick(R.id.img_header4)
    public void header4Click(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mHeaderController.doHeader(4);
    }

    @OnClick(R.id.img_header5)
    public void header5Click(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mHeaderController.doHeader(5);
    }

    @OnClick(R.id.img_header6)
    public void header6Click(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mHeaderController.doHeader(6);
    }

    @OnClick(R.id.img_bold)
    public void boldClick(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mStyleController.doBold();
    }

    @OnClick(R.id.img_italic)
    public void italicClick(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mStyleController.doItalic();
    }

    @OnClick(R.id.img_center_align)
    public void centerAlignClick(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mCenterAlignController.doCenter();
    }

    @OnClick(R.id.img_horizontal_rules)
    public void horizontalRulesClick(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mHorizontalRulesController.doHorizontalRules();
    }

    @OnClick(R.id.img_todo)
    public void todoClick(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mTodoController.doTodo();
    }

    @OnClick(R.id.img_todo_done)
    public void todoDoneClick(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mTodoController.doTodoDone();
    }

    @OnClick(R.id.img_strike_through)
    public void strikeThroughClick(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mStrikeThroughController.doStrikeThrough();
    }

    @OnClick(R.id.img_inline_code)
    public void inlineCodeClick(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mCodeController.doInlineCode();
    }

    @OnClick(R.id.img_code)
    public void codeClick(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mCodeController.doCode();
    }

    @OnClick(R.id.img_block_quote)
    public void blockQuotesClick(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mBlockQuotesController.doBlockQuotes();
    }

    @OnClick(R.id.img_unorder_list)
    public void unOrderListClick(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mListController.doUnOrderList();
    }

    @OnClick(R.id.img_order_list)
    public void orderListClick(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mListController.doOrderList();
    }

    @OnClick(R.id.img_link)
    public void imageClick(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mLinkController.doImage();
    }

    @OnClick(R.id.img_photo)
    public void photoClick(View v) {
        if (mRxMDEditText == null) {
            return;
        }
        mImageController.doImage();
    }

    @OnLongClick(R.id.img_block_quote)
    public boolean blockQuotesLongClick(View view) {
        if (mRxMDEditText == null) {
            return true;
        }
        mBlockQuotesController.addNestedBlockQuotes();
        return true;
    }

    public void handleResult(int requestCode, int resultCode, Intent data) {
        mImageController.handleResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.unbind(this);
    }
}
