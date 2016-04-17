package com.yydcdut.note.note;

import android.test.InstrumentationTestCase;

import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.utils.YLog;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by yuyidong on 15/11/28.
 */
public class PhotoNoteTest extends InstrumentationTestCase {
    private RxPhotoNote mRxPhotoNote;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mRxPhotoNote = new RxPhotoNote(this.getInstrumentation().getTargetContext());
    }

    public void testFindByCategoryId() {
        mRxPhotoNote.findByCategoryId(1, -1)
                .subscribe(new Subscriber<List<PhotoNote>>() {
                    @Override
                    public void onCompleted() {
                        YLog.i("yuyidong", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        YLog.i("yuyidong", "onError--->" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<PhotoNote> photoNoteList) {
                        YLog.i("yuyidong", "photoNoteList.size()--->" + photoNoteList.size());
                        for (PhotoNote photoNote : photoNoteList) {
                            YLog.i("yuyidong", photoNote.toString());
                        }
                    }
                });
    }

    public void testRefreshByCategoryId() {
        mRxPhotoNote.refreshByCategoryId(1, -1)
                .subscribe(new Subscriber<List<PhotoNote>>() {
                    @Override
                    public void onCompleted() {
                        YLog.i("yuyidong", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        YLog.i("yuyidong", "onError--->" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<PhotoNote> photoNoteList) {
                        YLog.i("yuyidong", "photoNoteList.size()--->" + photoNoteList.size());
                        for (PhotoNote photoNote : photoNoteList) {
                            YLog.i("yuyidong", photoNote.toString());
                        }
                    }
                });
    }

    public void testUpdatePhotoNotes() {
        mRxPhotoNote.findByCategoryId(1, -1)
                .subscribe(new Subscriber<List<PhotoNote>>() {
                    @Override
                    public void onCompleted() {
                        YLog.i("yuyidong", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        YLog.i("yuyidong", "onError--->" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<PhotoNote> photoNoteList) {
                        YLog.i("yuyidong", "photoNoteList.size()--->" + photoNoteList.size());
                        for (PhotoNote photoNote : photoNoteList) {
                            YLog.i("yuyidong", photoNote.toString());
                            photoNote.setContent("11111111111");
                        }
                        mRxPhotoNote.updatePhotoNotes(photoNoteList)
                                .subscribe(new Subscriber<List<PhotoNote>>() {
                                    @Override
                                    public void onCompleted() {
                                        YLog.i("yuyidong", "onCompleted11111");
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        YLog.i("yuyidong", "onError11111--->" + e.getMessage());
                                    }

                                    @Override
                                    public void onNext(List<PhotoNote> photoNoteList) {
                                        YLog.i("yuyidong", "1111111photoNoteList.size()--->" + photoNoteList.size());
                                        for (PhotoNote photoNote : photoNoteList) {
                                            YLog.i("yuyidong", "111111" + photoNote.toString());
                                        }
                                    }
                                });
                    }
                });
    }

    public void testUpdatePhotoNote() {
        mRxPhotoNote.findByCategoryId(1, -1)
                .subscribe(new Action1<List<PhotoNote>>() {
                    @Override
                    public void call(List<PhotoNote> photoNoteList) {
                        YLog.i("yuyidong", "photoNoteList.size()--->" + photoNoteList.size());
                        for (PhotoNote photoNote : photoNoteList) {
                            YLog.i("yuyidong", photoNote.toString());
                            photoNote.setContent(photoNote.getTitle());
                            mRxPhotoNote.updatePhotoNote(photoNote)
                                    .subscribe(new Subscriber<List<PhotoNote>>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onNext(List<PhotoNote> photoNoteList) {
                                            YLog.i("yuyidong", "photoNoteList.size()--->" + photoNoteList.size());
                                            for (PhotoNote photoNote : photoNoteList) {
                                                YLog.i("yuyidong", photoNote.toString());
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public void testSavePhotoNotes() {
        PhotoNote photoNote = new PhotoNote("1", 1l, 1l, "1", "1", 1l, 1l, 1);
        PhotoNote photoNote2 = new PhotoNote("2", 2l, 2l, "2", "2", 2l, 2l, 1);
        ArrayList<PhotoNote> arrayList = new ArrayList<>();
        arrayList.add(photoNote);
        arrayList.add(photoNote2);
        mRxPhotoNote.savePhotoNotes(arrayList)
                .subscribe(new Subscriber<List<PhotoNote>>() {
                    @Override
                    public void onCompleted() {
                        YLog.i("yuyidong", "onCompleted--->");
                    }

                    @Override
                    public void onError(Throwable e) {
                        YLog.i("yuyidong", "onError--->" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<PhotoNote> photoNoteList) {
                        YLog.i("yuyidong", "photoNote--->" + photoNoteList.size());
                    }
                });

    }

    public void testSavePhotoNote() {
        PhotoNote photoNote = new PhotoNote("3", 3l, 3l, "3", "3", 3l, 3l, 1);
        mRxPhotoNote.savePhotoNote(photoNote)
                .subscribe(new Action1<PhotoNote>() {
                    @Override
                    public void call(PhotoNote photoNote) {
                        YLog.i("yuyidong", "photoNote--->" + photoNote.toString());
                    }
                });
    }

    public void testDeletePhotoNotes() {
        mRxPhotoNote.findByCategoryId(1, -1)
                .subscribe(new Action1<List<PhotoNote>>() {
                    @Override
                    public void call(List<PhotoNote> photoNoteList) {
                        YLog.i("yuyidong", "photoNoteList.size()--->" + photoNoteList.size());
                        ArrayList<PhotoNote> arrayList = new ArrayList<PhotoNote>();
                        for (PhotoNote photoNote : photoNoteList) {
                            YLog.i("yuyidong", photoNote.getContent());
                            if (photoNote.getContent().equals("1") || photoNote.getContent().equals("2")) {
                                arrayList.add(photoNote);
                            }
                        }
                        mRxPhotoNote.deletePhotoNotes(arrayList, 1)
                                .subscribe(new Action1<List<PhotoNote>>() {
                                    @Override
                                    public void call(List<PhotoNote> photoNoteList) {
                                        YLog.i("yuyidong", "photoNoteList.size()--->" + photoNoteList.size());
                                    }
                                });
                    }
                });
    }

    public void testDeletePhotoNote() {
        mRxPhotoNote.findByCategoryId(1, -1)
                .subscribe(new Action1<List<PhotoNote>>() {
                    @Override
                    public void call(List<PhotoNote> photoNoteList) {
                        YLog.i("yuyidong", "photoNoteList.size()--->" + photoNoteList.size());
                        for (PhotoNote photoNote : photoNoteList) {
                            if (photoNote.getContent().equals("3")) {
                                mRxPhotoNote.deletePhotoNote(photoNote)
                                        .subscribe(new Subscriber<List<PhotoNote>>() {
                                            @Override
                                            public void onCompleted() {
                                                YLog.i("yuyidong", "onCompleted--->");
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                YLog.i("yuyidong", "onError--->" + e.getMessage());
                                            }

                                            @Override
                                            public void onNext(List<PhotoNote> photoNoteList) {
                                                YLog.i("yuyidong", "photoNoteList.size()--->" + photoNoteList.size());
                                                for (PhotoNote photoNote : photoNoteList) {
                                                    YLog.i("yuyidong", photoNote.toString());
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    public void testGetAllPhotoNotesNumber() {
        mRxPhotoNote.getAllPhotoNotesNumber()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        YLog.i("yuyidong", "onCompleted--->");
                    }

                    @Override
                    public void onError(Throwable e) {
                        YLog.i("yuyidong", "onError--->" + e.getMessage());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        YLog.i("yuyidong", "integer--->" + integer);
                    }
                });
    }
}
