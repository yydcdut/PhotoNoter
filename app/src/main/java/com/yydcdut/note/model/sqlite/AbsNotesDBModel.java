package com.yydcdut.note.model.sqlite;

import android.content.Context;

/**
 * Created by yuyidong on 15/10/15.
 */
public abstract class AbsNotesDBModel {
    private static final String NAME = "Notes.db";
    private static final int VERSION = 3;

    protected NotesSQLite mNotesSQLite;

    protected AbsNotesDBModel(Context context) {
        if (mNotesSQLite == null) {
            mNotesSQLite = new NotesSQLite(context, NAME, null, VERSION);
        }
    }

}
