package com.smartgrocery.pantry.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PantryItemDao_Impl implements PantryItemDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PantryItemEntity> __insertionAdapterOfPantryItemEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public PantryItemDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPantryItemEntity = new EntityInsertionAdapter<PantryItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `pantry_items` (`id`,`name`,`category`,`quantity`,`unit`,`expirationDate`,`purchasedDate`,`parLevel`,`avgDailyUse`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PantryItemEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getCategory() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getCategory());
        }
        statement.bindDouble(4, entity.getQuantity());
        if (entity.getUnit() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getUnit());
        }
        if (entity.getExpirationDate() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getExpirationDate());
        }
        if (entity.getPurchasedDate() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPurchasedDate());
        }
        statement.bindDouble(8, entity.getParLevel());
        statement.bindDouble(9, entity.getAvgDailyUse());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM pantry_items WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertAll(final List<PantryItemEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPantryItemEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsert(final PantryItemEntity item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPantryItemEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PantryItemEntity>> observeAll() {
    final String _sql = "SELECT * FROM pantry_items ORDER BY CASE WHEN expirationDate IS NULL THEN 1 ELSE 0 END, expirationDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"pantry_items"}, new Callable<List<PantryItemEntity>>() {
      @Override
      @NonNull
      public List<PantryItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfExpirationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expirationDate");
          final int _cursorIndexOfPurchasedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasedDate");
          final int _cursorIndexOfParLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "parLevel");
          final int _cursorIndexOfAvgDailyUse = CursorUtil.getColumnIndexOrThrow(_cursor, "avgDailyUse");
          final List<PantryItemEntity> _result = new ArrayList<PantryItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PantryItemEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final String _tmpExpirationDate;
            if (_cursor.isNull(_cursorIndexOfExpirationDate)) {
              _tmpExpirationDate = null;
            } else {
              _tmpExpirationDate = _cursor.getString(_cursorIndexOfExpirationDate);
            }
            final String _tmpPurchasedDate;
            if (_cursor.isNull(_cursorIndexOfPurchasedDate)) {
              _tmpPurchasedDate = null;
            } else {
              _tmpPurchasedDate = _cursor.getString(_cursorIndexOfPurchasedDate);
            }
            final double _tmpParLevel;
            _tmpParLevel = _cursor.getDouble(_cursorIndexOfParLevel);
            final double _tmpAvgDailyUse;
            _tmpAvgDailyUse = _cursor.getDouble(_cursorIndexOfAvgDailyUse);
            _item = new PantryItemEntity(_tmpId,_tmpName,_tmpCategory,_tmpQuantity,_tmpUnit,_tmpExpirationDate,_tmpPurchasedDate,_tmpParLevel,_tmpAvgDailyUse);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllOnce(final Continuation<? super List<PantryItemEntity>> $completion) {
    final String _sql = "SELECT * FROM pantry_items";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PantryItemEntity>>() {
      @Override
      @NonNull
      public List<PantryItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfExpirationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expirationDate");
          final int _cursorIndexOfPurchasedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasedDate");
          final int _cursorIndexOfParLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "parLevel");
          final int _cursorIndexOfAvgDailyUse = CursorUtil.getColumnIndexOrThrow(_cursor, "avgDailyUse");
          final List<PantryItemEntity> _result = new ArrayList<PantryItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PantryItemEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final String _tmpExpirationDate;
            if (_cursor.isNull(_cursorIndexOfExpirationDate)) {
              _tmpExpirationDate = null;
            } else {
              _tmpExpirationDate = _cursor.getString(_cursorIndexOfExpirationDate);
            }
            final String _tmpPurchasedDate;
            if (_cursor.isNull(_cursorIndexOfPurchasedDate)) {
              _tmpPurchasedDate = null;
            } else {
              _tmpPurchasedDate = _cursor.getString(_cursorIndexOfPurchasedDate);
            }
            final double _tmpParLevel;
            _tmpParLevel = _cursor.getDouble(_cursorIndexOfParLevel);
            final double _tmpAvgDailyUse;
            _tmpAvgDailyUse = _cursor.getDouble(_cursorIndexOfAvgDailyUse);
            _item = new PantryItemEntity(_tmpId,_tmpName,_tmpCategory,_tmpQuantity,_tmpUnit,_tmpExpirationDate,_tmpPurchasedDate,_tmpParLevel,_tmpAvgDailyUse);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
