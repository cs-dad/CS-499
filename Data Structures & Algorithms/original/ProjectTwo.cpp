// import necessary libraries
#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <string>
#include <algorithm>

// standard namespace
using namespace std;

// course struct
struct Course {
    string courseName;
    string courseTitle;
    vector<string> preReqs;
}

// node for the binary search tree
struct TreeNode {
    Course course;
    TreeNode* left;
    TreeNode* right;

    // constructor
    TreeNode(Course c) : course(c), left(nullptr), right(nullptr) {}
}

// let's create a class to store our BST and its helper methods
class CourseBST {

    // private methods
    private: 
        TreeNode* root;

        // helper method to insert a course into the BST
        TreeNode* insertNode(TreeNode* node, Course course) {

            // if node is null, return a new node with the course
            if (node == nullptr) {
                return new TreeNode(course);
            }

            // if the course number is less than the current node's course number, insert left
            if (course.courseName < node->course.courseName) {
                node->left = insertNode(node->left, course);
            } 
            // other wise insert right
            else {
                node->right = insertNode(node->right, course);
            }

        }

        // helper method to recursively in-order traverse the BST
        void inOrderTraversal(TreeNode* node, vector<Course>& sortedCourses) {
            if (node == nullptr) {
                return;
            }

            // traverse left, add the course, then traverse right
            inOrderTraversal(node->left, sortedCourses);
            sortedCourses.push_back(node->course);
            // traverse right, add the course, then traverse left
            inOrderTraversal(node->right, sortedCourses);
        }

        // helper method to recursively search for a course in the BST
        TreeNode* search(TreeNode* node, const string& courseNumber) {
            if(node == nullptr | node->course.courseName == courseNumber) {
                return node;
            }

            // traverse left
            if(courseNumber < node->course.courseName) {
                return search(node->left, courseNumber);
            }


            // traverse right
            return search(node->right, courseNumber);
        }
    
    public:
        // constructor
        CourseBST() : root(nullptr) {}

        // insert a course into the BST
        void insert(Course course) {
            root = insertNode(root, course);
        }

        // get a sorted list of courses
        vector<Course> getSortedCourses() {
            vector<Course> sortedCourses;
            inOrderTraversal(root, sortedCourses);
            return sortedCourses;
        }

        // find a course by its coursenumber
        Course* findCourse(const string& courseNumber) {
            TreeNode* node = search(root, courseNumber);

            // if returned node isn't null return the course, otherwise return nullptr
            return node != nullptr ? &node->course : nullptr;
        }

        // check if the BST is empty
        bool isEmpty() {
            return root = nullptr;
        }
}

// function to load courses from file
void loadCoursesFromFile(const string& filename, CourseBST& courseBST) {

    ifstream file(filename);

    if(!file.is_open()) {
        cerr << "Error: could not open file: " << filename << endl;
        return;
    }

    string line;
    int lineNumber = 0;
    while(getline(file, line)) {
        lineNumber++;
        stringstream ss(line);
        string courseNumber, courseTitle, preReq;
        vector<string> preReqs;

        getline(ss, courseNumber, ',');
        getline(ss, courseTitle, ',');

        if(courseNumber.empty() || courseTitle.empty()) {
            cerr << "Error: invalid course data at line " << lineNumber << endl;
            continue;
        }

        // remainder of the string is inputted into the pre req array
        while(getline(ss, preReq, ',')) {
            preReqs.push_back(preReq);
        }

        // create a new course
        Course course = {courseNumber, courseTitle, preReqs};

        // insert the course into the BST
        courseBST.insert(course);
    }

    file.close();   



}

// function to display a course
void displayCourse(const Course& course) {
    cout << "Course Number: " << course.courseName << endl;
    cout << "Course Title: " << course.courseTitle << endl;
    cout << "Prerequisites: ";

    if(course.preReqs.empty()) {
        cout << "None" << endl;
    } else {
        for(const string& preReq : course.preReqs) {
            cout << preReq << " ";
        }
    }
    cout << endl;
}


// function to display menu

void displayMenu() {
    cout << "\nMenu Options:" << endl;
    cout << "1. Load Course Data" << endl;
    cout << "2. Print All Courses" << endl;
    cout << "3. Search for a Course" << endl;
    cout << "4. Print Sorted Courses" << endl;
    cout << "9. Exit" << endl;
    cout << "Enter your choice: ";
}

// Function to print all courses in alphanumeric order
void printAllCourses(CourseBST& bst) {
    if (bst.isEmpty()) {
        cout << "No courses available to display." << endl;
        return;
    }

    vector<Course> sortedCourses = bst.getSortedCourses();
    cout << "\nCourses in Alphanumeric Order:" << endl;
    for (const Course& course : sortedCourses) {
        displayCourse(course);
    }
}

int main() {

    CourseBST bst;
    int choice;
    string filename;

    do {

        displayMenu();

        cin >> choice;

        switch(choice) {
            case 1:
                cout << "Enter the file name to load courses: "
                getline(cin, filename);
                loadCoursesFromFile(filename, bst);
                break;

            case 2:
                printAllCourses(bst);
                break;

            case 3: 
                string courseNumber;
                cout << "Enter course number: ";
                cin >> courseNumber;

                Course* course = bst.findCourse(courseNumber);

                if(course) {
                    displayCourse
                } else {
                    cout << "Course not found." << endl;
                }
                break;
            
            case 4:
                printAllCourses(bst);
                break;
            case 9:
                cout << "Exiting program..." << endl;
                break;
            default:
                cout << "Invalid choice. Please try again." << endl;
        }

    } while(choice != 9) 

    return 0;
}