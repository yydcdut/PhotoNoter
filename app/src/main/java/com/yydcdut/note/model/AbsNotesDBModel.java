package com.yydcdut.note.model;

import android.content.Context;

import com.yydcdut.note.model.sqlite.NotesSQLite;

/**
 * Created by yuyidong on 15/10/15.
 */
public abstract class AbsNotesDBModel implements IModel {
    private static final String NAME = "Notes.db";
    private static final int VERSION = 2;

    protected NotesSQLite mNotesSQLite;

    protected AbsNotesDBModel(Context context) {
        if (mNotesSQLite == null) {
            mNotesSQLite = new NotesSQLite(context, NAME, null, VERSION);
        }
    }

}
