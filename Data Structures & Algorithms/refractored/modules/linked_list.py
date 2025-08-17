from __future__ import annotations
from typing import Optional, List
from .models import Course

# Node type for the linked list
class _LLNode:
    __slots__ = ("course", "next")
    def __init__(self, course: Course):
        self.course = course
        self.next: Optional["_LLNode"] = None

# linked list wrapper
class CourseLinkedList:
    def __init__(self) -> None:
        self._head: Optional[_LLNode] = None
        self._tail: Optional[_LLNode] = None
        self._size = 0
    
    def append(self, course: Course) -> None:
        # Always appends to tail
        node = _LLNode(course)
        if self._tail is None:
            self._head = self._tail = node
        else:
            self._tail.next = node
            self._tail = node
        self._size += 1
    
    def traverse(self) -> List[Course]:
        # Collect all courses via node walkthrough
        cur = self._head
        out: List[Course] = []
        while cur:
            out.append(cur.course)
            cur = cur.next
        return out
    
    def find(self, courseNumber: str) -> Optional[Course]:
        # Linear search through all nodes

        cur = self._head
        while cur:
            if cur.course.courseName == courseNumber:
                return cur.course
            cur = cur.next
        return None
    
    def __len__(self) -> int:
        return self._size
    

