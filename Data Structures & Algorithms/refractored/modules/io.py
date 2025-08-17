import csv
from typing import Iterable, List, Tuple
from .models import Course
from .bst import CourseBST
from .linked_list import CourseLinkedList
from .index import CourseIndex


# Reads CSV rows and serializes them into Course objects
def read_courses_from_csv(file_path: str) -> Iterable[Course]:
    courses: List[Course] = []

    with open(file_path, newline="", encoding="utf-8") as f:
        reader = csv.reader(f)
        for row in reader:
            if not row:
                continue

            name = (row[0] or "").strip()
            title = (row[1] or "").strip() if len(row) > 1 else ""
            if not name or not title:
                continue
            prereqs = [c.strip() for c in row[2:] if c and c.strip()]
            courses.append(Course(name, title, prereqs))
    
    return courses

# Helper method to help build all three structures at once
def build_structures(courses: Iterable[Course]) -> Tuple[CourseBST, CourseLinkedList, CourseIndex]:
    bst = CourseBST()
    ll = CourseLinkedList()
    idx = CourseIndex()

    for c in courses:
        bst.insert(c)
        ll.append(c)
        idx.add(c)
    return bst, ll, idx