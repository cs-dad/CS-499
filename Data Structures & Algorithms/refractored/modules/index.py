from __future__ import annotations
from typing import Dict, Iterable, Optional
from .models import Course

# Wrapper around a hashmap
class CourseIndex:
    def __init__(self) -> None:
        self._by_key: Dict[str, Course] = {}

    def add(self, course: Course) -> None:
        # Store by courseName -> O(1) avg insert
        self._by_key[course.courseName] = course
    
    def find(self, courseNumber: str) -> Optional[Course]:
        # Direct hash lookup
        return self._by_key.get(courseNumber)
    
    def all(self) -> Iterable[Course]:
        return self._by_key.values()
    
    def __len__(self) -> int:
        return len(self._by_key)