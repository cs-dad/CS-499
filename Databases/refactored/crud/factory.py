from __future__ import annotations
from typing import Literal, Optional, TypedDict

from .abstract import AbstractCRUD
from .mongo_impl import MongoCRUD
from .sqlite_impl import SQLiteCRUD


class MongoConfig(TypedDict):
    kind: Literal["mongo"]
    uri: str #e.g. mongodb://localhost:27017
    db: str
    collection: str

class SQLiteConfig(TypedDict):
    kind: Literal["sqlite"]
    path: str # e.g "./data/animals.db"
    table: str


Config = MongoConfig | SQLiteConfig

def build_crud(cfg: Config) -> AbstractCRUD:
    if cfg["kind"] == "mongo":
        return MongoCRUD(uri=cfg["uri"], db_name=cfg["db"], coll_name=cfg["collection"])
    elif cfg["kind"] == "sqlite":
        return SQLiteCRUD(path=cfg["path"], table=cfg["table"])
    
    raise ValueError(f"Unsupported config kind: {cfg['kind']}")