from __future__ import annotations
import sys
from typing import List
import os

# relative imports, since refractored is a package
from .modules.manager import CourseManager
from .modules.models import Course


# small helper for pretty-printing
def _print_courses(items: List[Course]) -> None:
    if not items:
        print("No courses found.")
    for item in items:
        # Assuming your dataclass has courseName + courseTitle
        print(f"{item.courseName}: {item.courseTitle}")


def main(argv: list[str]) -> None:
    rl_path = argv[1] if len(argv) > 1 else "courses.csv"
    BASE_DIR = os.path.dirname(__file__) # get base directory
    path = os.path.join(BASE_DIR, rl_path) # join with relative path
    mgr = CourseManager()

    try:
        n = mgr.load_from_csv(path)
        print(f"Loaded {n} courses from {path}")
    except Exception as e:
        print(f"Error loading courses: {e}")
        return

    print("\nCourses (BST | Sorted)")
    _print_courses(mgr.sorted_bst())

    # menu loop
    while True:
        print("\nMenu:")
        print("1. Sorted (BST)")
        print("2. Sorted (LinkedList)")
        print("3. Sorted (Dictionary)")
        print("4. Find (BST)")
        print("5. Find (LinkedList)")
        print("6. Find (Dictionary)")
        print("7. Benchmark Current Dataset")
        print("0. Exit")
        choice = input("Enter your choice (0-7): ")

        if choice == "0":
            print("Exiting...")
            break
        elif choice == "1":
            _print_courses(mgr.sorted_bst())
        elif choice == "2":
            _print_courses(mgr.sorted_ll())
        elif choice == "3":
            _print_courses(mgr.sorted_dict())
        elif choice == "4":
            course_id = input("Enter course ID to find (BST): ")
            course = mgr.find_in_bst(course_id)
            print(f"Found (BST): {course or 'Not found'}")
        elif choice == "5":
            course_id = input("Enter course ID to find (LinkedList): ")
            course = mgr.find_in_ll(course_id)
            print(f"Found (LinkedList): {course or 'Not found'}")
        elif choice == "6":
            course_id = input("Enter course ID to find (Dictionary): ")
            course = mgr.find_in_dict(course_id)
            print(f"Found (Dictionary): {course or 'Not found'}")
            break
        elif choice == "7":
            res = mgr.benchmark_current(repeat=5)
            print("\nAverage Seconds over 5 runs:")
            for k, v in res.items():
                print(f"{k}: {v:.6f} seconds")
        else:
            print("Invalid choice. Please try again.")

        input("\nPress Enter to select another option.")


if __name__ == "__main__":
    main(sys.argv)
