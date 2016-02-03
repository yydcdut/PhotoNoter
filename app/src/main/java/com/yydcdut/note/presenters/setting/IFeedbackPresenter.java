package com.yydcdut.note.presenters.setting;

import com.yydcdut.note.presenters.IPresenter;

/**
 * Created by yuyidong on 15/11/13.
 */
public interface IFeedbackPresenter extends IPresenter {
    String TYPE = "type";
    int TYPE_FEEDBACK = 0;
    int TYPE_CONTACT = 1;

    void bindData(int type);

    /**
     * 判断email的内容和content的内容的完整性
     *
     * @return
     */
    boolean checkFeendback();

    /**
     * 发送feedback
     *
     * @param email
     * @param content
     */
    void sendFeedback(String email, String content);

}
