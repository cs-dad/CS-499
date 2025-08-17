from __future__ import annotations
from typing import Dict, Iterable, List
from time import perf_counter
from random import randint
from .models import Course
from .bst import CourseBST
from .linked_list import CourseLinkedList
from .index import CourseIndex

def benchmark_sort_methods(dataset: List[Course], repeat: int = 5) -> Dict[str, float]:
    results: Dict[str, float] = {}

    # BST: insert + inorder
    total = 0.0
    for _ in range(repeat):
        bst = CourseBST()
        t0 = perf_counter()
        for c in dataset:
            bst.insert(c)
        _ = bst.in_order()
        total += perf_counter() - t0
    results["BST_insert+inorder"] = total / repeat

    # LinkedList: append + traverse + sort
    total = 0.0
    for _ in range(repeat):
        ll = CourseLinkedList()
        t0 = perf_counter()
        for c in dataset:
            ll.append(c)
        temp = ll.traverse()
        temp.sort(key=lambda x: x.courseName)
        total += perf_counter() - t0
    results["LinkedList_traverse+sort"] = total / repeat

    # Dict: build + values + sort
    total = 0.0
    for _ in range(repeat):
        idx = CourseIndex()
        t0 = perf_counter()
        for c in dataset:
            idx.add(c)
        temp = list(idx.all())
        temp.sort(key=lambda x: x.courseName)
        total += perf_counter() - t0
    results["Dict_values+sort"] = total / repeat

    return results

def _gen_fake_courses(n: int) -> List[Course]:
    items: List[Course] = []
    for i in range(n):
        key = f"CSCI{1000 + i:04d}"
        title = f"Course {i}"
        prereq_count = randint(0, 2)
        prereqs = [f"CSCI{1000 + randint(0, i):04d}" for _ in range(prereq_count)] if i else []
        items.append(Course(key, title, prereqs))
    return items

def benchmark_scaling(sizes: Iterable[int] = (100, 1_000, 5_000), repeat: int = 3) -> None:
    print("\n=== Sort Complexity Benchmark (empirical) ===")
    print("Average seconds over repeats\n")
    header = f"{'N':>6} | {'BST_insert+inorder':>20} | {'LL_traverse+sort':>18} | {'Dict_values+sort':>18}"
    print(header)
    print("-" * len(header))
    for n in sizes:
        data = _gen_fake_courses(n)
        res = benchmark_sort_methods(data, repeat=repeat)
        print(f"{n:>6} | {res['BST_insert+inorder']:>20.6f} | {res['LinkedList_traverse+sort']:>18.6f} | {res['Dict_values+sort']:>18.6f}")
