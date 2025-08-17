from __future__ import annotations
from dataclasses import dataclass, field
from typing import List

# Course Data Model
# Represents a single course record
# It's comparable so that Python can sort them by course name / title.
@dataclass(order=True)
class Course:
    courseName: str
    courseTitle: str
    preReqs: List[str] = field(default_factory=list)


    def __str__(self) -> str:
        reqs = "None" if not self.preReqs else ", ".join(self.preReqs)
        return f"{self.courseName}: {self.courseTitle} | Prerequisites: {reqs}"
    
    
