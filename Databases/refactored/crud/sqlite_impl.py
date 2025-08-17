from __future__ import annotations
import sqlite3
from typing import Any, Dict, List, Optional, Tuple
from .abstract import AbstractCRUD, Query, Record

def _to_where_clause(query: Query) -> Tuple[str, Tuple[Any, ...]]:
    """
    Minimal query translator for SQLite, helps normalize query format.
    Ex: {"breed": "pit bull", "sex_upon_outcome"....} -> WHERE BREED = ? ...
    """

    if not query:
        return "", tuple()
    
    parts = []
    params = []
    for k,v in query.items():
        parts.append(f"{k} = ?")
        params.append(v)
    return " WHERE " + " AND ".join(parts), tuple(params)

def _row_to_dict(cursor: sqlite3.Cursor, row: sqlite3.Row) -> Dict[str, Any]:
    return {desc[0]: row[idx] for idx, desc in enumerate(cursor.description)}

class SQLiteCRUD(AbstractCRUD):
    """
    SQLite CRUD implementation.
    Schema-agnostic, creates a default animals table if ensure_default_schema is called.
    """

    def __init__(self, path: str, table: str = "animals") -> None:
        self._conn = sqlite3.connect(path)
        self._conn.row_factory = sqlite3.Row
        self._table = table

    def ensure_default_schema(self) -> None:
        """
        Creates a minimal schema suitable for the AAC-like dataset.
        """

        sql = f"""
        CREATE TABLE IF NOT EXISTS {self._table} (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            age_upon_outcome TEXT,
            animal_id TEXT,
            animal_type TEXT,
            name TEXT,
            breed TEXT,
            color TEXT,
            date_of_birth TEXT,
            outcome_subtype TEXT,
            outcome_type TEXT,
            sex_upon_outcome TEXT
        );
        """
        self._conn.execute(sql)
        self._conn.commit()

    def create(self, record: Record) -> Optional[str]:
        if not isinstance(record, dict): 
            return None
        
        keys = list(record.keys())

        if not keys:
            return None
        
        cols = ", ".join(keys)
        placeholders = ", ".join("?" for _ in keys)
        sql = f"INSERT INTO {self._table} ({cols}) VALUES ({placeholders})"
        cur = self._conn.execute(sql, tuple(record[k] for k in keys))
        self._conn.commit()
        return str(cur.lastrowid) if cur and cur.lastrowid else None
    
    def read(self, query: Optional[Query] = None, limit: Optional[int] = None) -> List[Record]:
        where, params = _to_where_clause(query or {})

        limit_sql = f"LIMIT {int(limit)}" if limit else ""
        sql = f"SELECT * FROM {self._table}{where}{limit_sql}"
        cur = self._conn.execute(sql, params)
        rows = cur.fetchall()
        return [_row_to_dict(cur, r) for r in rows]
    
    def update(self, query: Query, values: Record) -> int:
        if not isinstance(query, dict) or not isinstance(values, dict) or not values:
            return 0

        set_clause = ", ".join([f"{k} = ?" for k in values.keys()])
        set_params = tuple(values.values())

        where, where_params = _to_where_clause(query)
        sql = f"UPDATE {self._table} SET {set_clause}{where}"
        cur = self._conn.execute(sql, set_params + where_params)
        self._conn.commit()
        return cur.rowcount or 0
    
    def delete(self, query: Query) -> int:
        where, params = _to_where_clause(query)
        sql = f"DELETE FROM {self._table}{where}"
        cur = self._conn.execute(sql, params)
        self._conn.commit()
        return cur.rowcount or 0
    
    def close(self) -> None:
        self._conn.close()