from __future__ import annotations
from abc import ABC, abstractmethod
from typing import Any, Dict, Iterable, List, Optional


Record = Dict[str, Any]
Query = Dict[str, Any]


class AbstractCRUD(ABC):
    """
    Database-agnostic CRUD interface.
    All methods should return consistent, plain Python types.
    """

    @abstractmethod
    def create(self, record: Record) -> Optional[str]:
        pass

    @abstractmethod
    def read(self, query: Optional[Query] = None, limit: Optional[int] = None) -> List[Record]:
        pass

    @abstractmethod
    def update(self, query: Query, values: Record) -> int:
        pass

    @abstractmethod
    def delete(self, query: Query) -> int:
        pass

    @abstractmethod
    def close(self) -> None:
        pass

    # combination helper method for insert and update
    # this is just a placeholder that will be overloaded via inheritance
    def _upsert(self, query: Query, values: Record) -> int:
        return self.update(query, values)