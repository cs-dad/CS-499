from __future__ import annotations
from typing import Any, Dict, List, Optional
from bson import ObjectId
from pymongo import MongoClient
from .abstract import AbstractCRUD, Query, Record


def _stringify_id(doc: Dict[str, Any]) -> Dict[str, Any]:
    # Convert ObjectID to str for portability

    d = dict(doc)
    if "_id" in d and isinstance(d["_id"], ObjectId):
        d["_id"] = str(d["_id"])
    return d

class MongoCRUD(AbstractCRUD):
    """
    MongoDB CRUD implementation.
    Assumes that the collection exists. Does not auto create indexes.
    """

    def __init__(self, uri: str, db_name: str, coll_name: str) -> None:
        self._client = MongoClient(uri)
        self._db = self._client[db_name]
        self._coll = self._db[coll_name]

    def create(self, record: Record) -> Optional[str]:
        if not isinstance(record, dict):
            return None
        res = self._coll.insert_one(record)
        return str(res.inserted_id) if res and res.inserted_id else None
    
    def read(self, query: Optional[Query] = None, limit: Optional[int] = None) -> List[Record]:
        q = query or {}
        cursor = self._coll.find(q)

        if limit:
            cursor = cursor.limit(limit)

        return [_stringify_id(doc) for doc in cursor]
    
    def update(self, query: Query, values: Record) -> int:
        if not isinstance(query, dict) or not isinstance(values, dict):
            return 0
        res = self._coll.update_many(query, {"$set": values})
        return int(res.modified_count) or 0
    
    def delete(self, query: Query) -> int:
        if not isinstance(query, dict):
            return 0
        
        res = self._coll.delete_many(query)
        return int(res.deleted_count) or 0
    
    def close(self) -> None:
        self._client.close()