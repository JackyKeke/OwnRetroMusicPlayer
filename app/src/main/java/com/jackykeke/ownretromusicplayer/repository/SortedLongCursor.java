package com.jackykeke.ownretromusicplayer.repository;

import android.database.AbstractCursor;
import android.database.Cursor;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author keyuliang on 2022/9/22.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
/**
 * This cursor basically wraps a song cursor and is given a list of the order of the ids of the
 * contents of the cursor. It wraps the Cursor and simulates the internal cursor being sorted by
 * moving the point to the appropriate spot
 * 这个游标基本上包裹了一个歌曲游标，并给出了一个游标内容的 ID 顺序列表。它包装光标并通过将点移动到适当的位置来模拟内部光标的排序
 */
public class SortedLongCursor extends AbstractCursor {

    // cursor to wrap
    private final Cursor mCursor;
    // the map of external indices to internal indices
    private ArrayList<Integer> mOrderedPositions;
    // this contains the ids that weren't found in the underlying cursor
    private final ArrayList<Long> mMissingIds;
    // this contains the mapped cursor positions and afterwards the extra ids that weren't found
    private HashMap<Long, Integer> mMapCursorPositions;

    public SortedLongCursor(final  Cursor cursor ,final long[] order ,final  String columnName){
        mCursor =cursor;
        mMissingIds = buildCursorPositionMapping(order,columnName);
    }

    private ArrayList<Long> buildCursorPositionMapping(long[] order, String columnName) {
        ArrayList<Long> missingIds = new ArrayList<>();

        mOrderedPositions = new ArrayList<>(mCursor.getCount());

        mMapCursorPositions = new HashMap<>(mCursor.getCount());
        final int idPosition = mCursor.getColumnIndex(columnName);

        if (mCursor.moveToFirst()){
            // first figure out where each of the ids are in the cursor
            // 首先找出每个 id 在光标中的位置
            do {
                mMapCursorPositions.put( mCursor.getLong(idPosition), mCursor.getPosition());
            }while (mCursor.moveToNext());

            // now create the ordered positions to map to the internal cursor given the
            // external sort order   现在创建有序位置以映射到给定外部排序顺序的内部光标
            for (int i = 0; order!=null && i < order.length; i++) {
                final long id =order[i];
                if (mMapCursorPositions.containsKey(id)){
                    mOrderedPositions.add(mMapCursorPositions.get(id));
                    mMapCursorPositions.remove(id);
                }else {
                    missingIds.add(id);
                }
            }

            mCursor.moveToFirst();

        }

        return missingIds;
    }



    /** @return the list of ids that weren't found in the underlying cursor */
    public ArrayList<Long> getMissingIds() {
        return mMissingIds;
    }

    /** @return the list of ids that were in the underlying cursor but not part of the ordered list */
    @NonNull
    public Collection<Long> getExtraIds() {
        return mMapCursorPositions.keySet();
    }

    @Override
    public void close() {
        mCursor.close();
        super.close();
    }

    @Override
    public int getCount() {
        return mOrderedPositions.size();
    }

    @Override
    public String[] getColumnNames() {
        return mCursor.getColumnNames();
    }

    @Override
    public String getString(int column) {
        return mCursor.getString(column);
    }

    @Override
    public short getShort(int column) {
        return mCursor.getShort(column);
    }

    @Override
    public int getInt(int column) {
        return mCursor.getInt(column);
    }

    @Override
    public long getLong(int column) {
        return mCursor.getLong(column);
    }

    @Override
    public float getFloat(int column) {
        return mCursor.getFloat(column);
    }

    @Override
    public double getDouble(int column) {
        return mCursor.getDouble(column);
    }

    @Override
    public boolean isNull(int column) {
        return mCursor.isNull(column);
    }

    @Override
    public boolean onMove(int oldPosition, int newPosition) {
        if (newPosition >= 0 && newPosition< getCount()){
            mCursor.moveToPosition(mOrderedPositions.get(newPosition));
            return true;
        }
        return  false;
    }
}
