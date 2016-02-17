package com.yydcdut.note.note;

import android.graphics.ImageFormat;
import android.test.InstrumentationTestCase;

import com.yydcdut.note.bean.SandExif;
import com.yydcdut.note.bean.SandPhoto;
import com.yydcdut.note.model.rx.RxSandBox;
import com.yydcdut.note.utils.YLog;

import rx.Subscriber;

/**
 * Created by yuyidong on 15/11/6.
 */
public class SandBoxTest extends InstrumentationTestCase {
    private RxSandBox mRxSandBox;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mRxSandBox = new RxSandBox(this.getInstrumentation().getTargetContext());
    }

    public void testSave() {
        SandPhoto sandPhoto = new SandPhoto(SandPhoto.ID_NULL, 1l, "0", 1, false, 1, "111", 11, ImageFormat.JPEG,
                new SandExif(0, "s", "ss", 0, 0, 0, 1, "1", "1"));
        mRxSandBox.saveOne(sandPhoto)
                .subscribe(new Subscriber<SandPhoto>() {
                    @Override
                    public void onCompleted() {
                        YLog.i("yuyidong", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        YLog.i("yuyidong", "onError");

                    }

                    @Override
                    public void onNext(SandPhoto sandPhoto) {
                        YLog.i("yuyidong", sandPhoto.toString());
                    }
                });
    }

    public void testFindOneAndDelete() {
        mRxSandBox.findFirstOne()
                .subscribe(new Subscriber<SandPhoto>() {
                    @Override
                    public void onCompleted() {
                        YLog.i("yuyidong", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        YLog.i("yuyidong", "onError");

                    }

                    @Override
                    public void onNext(SandPhoto sandPhoto) {
                        YLog.i("yuyidong", sandPhoto.toString());
                        mRxSandBox.deleteOne(sandPhoto)
                                .subscribe(new Subscriber<Integer>() {
                                    @Override
                                    public void onCompleted() {
                                        YLog.i("yuyidong", "onCompleted");
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        YLog.i("yuyidong", "onError");

                                    }

                                    @Override
                                    public void onNext(Integer Integer) {
                                        YLog.i("yuyidong", "Integer--->" + Integer);
                                    }
                                });
                    }
                });
    }

    public void testGetNumber() {
        mRxSandBox.getNumber()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        YLog.i("yuyidong", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        YLog.i("yuyidong", "onError");

                    }

                    @Override
                    public void onNext(Integer Integer) {
                        YLog.i("yuyidong", "Integer--->" + Integer);
                    }
                });
    }
}
