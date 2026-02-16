package com.user.Enum;


public enum FollowStatus {

    SELF,               // viewing own profile
    NOT_FOLLOWING,      // no relationship
    FOLLOWING,          // A follows B
    REQUESTED,          // A sent follow request (private account)
    FOLLOWED_BY,        // B follows A
    MUTUAL              // both follow each other
}
