package com.sigmat.lms.models;

public enum CourseScope {
    INSTITUTE_ONLY,  // Course is only accessible to students of the creating institute
    GLOBAL,          // Course is globally available for purchase/enrollment
    RESTRICTED       // Course has specific access rules
}