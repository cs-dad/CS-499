from typing import Optional, List
from .models import Course
from .bst import CourseBST
from .linked_list import CourseLinkedList
from .index import CourseIndex
from .io import read_courses_from_csv, build_structures
from .bench import benchmark_sort_methods


# This is a facade class, helps hide the complexity of our 3 list structures behind a singular unified API

class CourseManager:
    def __init__(self) -> None:
        self.bst: Optional[CourseBST] = None
        self.ll: Optional[CourseLinkedList] = None
        self.idx: Optional[CourseIndex] = None

    def load_from_csv(self, file_path: str) -> int:
        # Load courses from CSV and populate the structures with the data
        courses = read_courses_from_csv(file_path)
        self.bst, self.ll, self.idx = build_structures(courses)
        return len(courses)
    
    # Lookup methods for each structure
    def find_in_bst(self, key: str) -> Optional[Course]:
        return None if not self.bst else self.bst.find(key)
    
    def find_in_ll(self, key: str) -> Optional[Course]:
        return None if not self.ll else self.ll.find(key)
    
    def find_in_dict(self, key: str) -> Optional[Course]:
        return None if not self.idx else self.idx.find(key)
    
    # Sorted results for each structure
    def sorted_bst(self) -> List[Course]:
        return [] if not self.bst else self.bst.in_order()
    
    def sorted_ll(self) -> List[Course]:
        if not self.ll: return []
        data = self.ll.traverse()
        data.sort(key=lambda c: c.courseName)
        return data
    
    def sorted_dict(self) -> List[Course]:
        if not self.idx: return []
        data = list(self.idx.all())
        data.sort(key=lambda c: c.courseName)
        return data
    
    # Benchmark dataset that's loaded
    def benchmark_current(self, repeat: int = 5):
        if not self.idx:
            raise RuntimeError("No dataset loaded for benchmarking.")
        data = list(self.idx.all())
        return benchmark_sort_methods(data, repeat=repeat)
    
