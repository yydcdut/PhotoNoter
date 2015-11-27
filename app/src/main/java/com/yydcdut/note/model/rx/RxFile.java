package com.yydcdut.note.model.rx;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by yuyidong on 15/11/25.
 */
public class RxFile {
    @Inject
    public RxFile() {
    }

    /**
     * 计算文件夹中图片有多少张
     *
     * @param files
     * @return
     */
    public Observable<Integer> getPhotosNumberInDisk(File[] files) {
        return Observable.from(files)
                .subscribeOn(Schedulers.io())
                .filter(new Func1<File, Boolean>() {//过滤掉 文件夹
                    @Override
                    public Boolean call(File file) {
                        return !file.isDirectory();
                    }
                })
                .filter(new Func1<File, Boolean>() {//过滤出 jpg、png、JPEG
                    @Override
                    public Boolean call(File file) {
                        return file.getName().toLowerCase().endsWith(".jpg") ||
                                file.getName().toLowerCase().endsWith(".png") ||
                                file.getName().toLowerCase().endsWith(".jpeg");
                    }
                })
                .count();
    }

//    public Observable<Long> getFolderStorage(File dir) {
//        return Observable.just(dir)
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Func1<File, Observable<File>>() {
//                    @Override
//                    public Observable<File> call(File file) {
//                        return Observable.from(file.listFiles());
//                    }
//                })
//
//                .map(new Func1<File, Long>() {
//                    @Override
//                    public Long call(File file) {
////                        if (file.isDirectory()) {
////                            return getFolderStorage(file);
////                        } else {
//                        return file.length();
////                        }
//                    }
//                })
//                .countLong();//todo wrong
//    }

//    private Observable<File> a(File dir) {
//        return Observable.just(dir)
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Func1<File, Observable<File>>() {
//                    @Override
//                    public Observable<File> call(File file) {
//                        return Observable.from(file.listFiles());
//                    }
//                })
//                .lift(new Observable.Operator<File, File>() {
//                    @Override
//                    public Subscriber<? super File> call(Subscriber<? super File> subscriber) {
//
//                        return new Subscriber<File>() {
//                            @Override
//                            public void onCompleted() {
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                            }
//
//                            @Override
//                            public void onNext(File file) {
//                                if (file.isDirectory()) {
//                                    a(file).
//                                } else {
//                                    subscriber.onNext(file);
//                                }
//                            }
//                        };
//                    }
//                });
//    }


}
