from __future__ import annotations
from typing import Optional, List
from .models import Course


# Internal node that we use only inside of the BST
class _BSTNode:
    __slots__ = ("course", "left", "right")
    def __init__(self, course: Course):
        self.course = course
        self.left: Optional["_BSTNode"] = None
        self.right: Optional["_BSTNode"] = None


# Public BST wrapper class
class CourseBST:
    def __init__(self) -> None:
        # Root of the tree
        self._root: Optional[_BSTNode] = None
    
    def is_empty(self) -> bool:
        # Return true if no root node is set
        return self._root is None
    
    def insert(self, course: Course) -> None:
        # Public insert method that then gets delegated to recursive helper
        self._root = self._insert(self._root, course)

    def _insert(self, node: Optional[_BSTNode], course: Course) -> _BSTNode:

        # Recursive insertion, which is based on the lexicographic order of courseName value
        if node is None:
            return _BSTNode(course)
        if course.courseName < node.course.courseName:
            node.left = self._insert(node.left, course)
        else:
            node.right = self._insert(node.right, course)
        return node
    
    def in_order(self) -> List[Course]:
        # Traverse tree left->root-> right, returns sorted courses
        out: List[Course] = []
        self._in_order(self._root, out)
        return out
    
    def _in_order(self, node: Optional[_BSTNode], out: List[Course]) -> None:
        if node is None:
            return
        self._in_order(node.left, out)
        out.append(node.course)
        self._in_order(node.right, out)

    def find(self, courseNumber: str) -> Optional[Course]:
        # Iterative search, walk left or right based on a key comparison
        node = self._root

        while node:
            if courseNumber == node.course.courseName:
                return node.course
            node = node.left if courseNumber < node.course.courseName else node.right
        return None